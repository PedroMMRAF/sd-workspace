package trab1.servers.java;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import trab1.api.Message;
import trab1.api.User;
import trab1.api.java.Feeds;
import trab1.api.java.Result;
import trab1.clients.FeedsClientFactory;
import trab1.clients.UsersClientFactory;
import trab1.servers.Domain;

public class JavaFeeds implements Feeds {
    private static Logger Log = Logger.getLogger(JavaFeeds.class.getName());

    private Map<String, Map<Long, Message>> feeds;
    private Map<String, Set<String>> following;
    private Map<String, Set<String>> followers;

    public JavaFeeds() {
        feeds = new ConcurrentHashMap<>();
        following = new ConcurrentHashMap<>();
        followers = new ConcurrentHashMap<>();
    }

    private Map<Long, Message> getFeed(String user) {
        feeds.putIfAbsent(user, new ConcurrentHashMap<>());
        return feeds.get(user);
    }

    private Set<String> getFollowing(String user) {
        following.putIfAbsent(user, new CopyOnWriteArraySet<>());
        return following.get(user);
    }

    private Set<String> getFollowers(String user) {
        followers.putIfAbsent(user, new CopyOnWriteArraySet<>());
        return followers.get(user);
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        String userDomain = user.split("@")[1];

        if (msg == null
                || msg.getUser() == null
                || msg.getDomain() == null
                || !msg.getDomain().equals(userDomain)
                || !Domain.get().equals(userDomain))
            return Result.error(Result.ErrorCode.BAD_REQUEST);

        long id = UUID.randomUUID().getMostSignificantBits();
        msg.setId(id);

        Log.info("postMessage : " + msg);

        Result<User> res = getUser(user, pwd);

        if (!res.isOK())
            return Result.error(res.error());

        getFeed(user).put(id, msg);

        for (String u : getFollowers(user)) {
            String subDomain = u.split("@")[1];

            if (subDomain.equals(Domain.get())) {
                getFeed(u).put(id, msg);
            } else {
                new Thread(() -> postMessagePropagate(u, msg)).start();
            }
        }

        return Result.ok(id);
    }

    @Override
    public Result<Long> postMessageOtherDomain(String user, Message msg) {
        Log.info("postMessageOtherDomain : " + msg);

        getFeed(user).put(msg.getId(), msg);

        return Result.ok(msg.getId());
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        Log.info("removeFromPersonalFeed : " + mid);

        Result<User> res = getUser(user, pwd);

        if (!res.isOK())
            return Result.error(res.error());

        if (getFeed(user).remove(mid) == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok();
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        Log.info("getMessage : " + mid);

        String domain = user.split("@")[1];

        if (!domain.equals(Domain.get()))
            return forwardGetMessage(user, mid);

        if (!hasUser(user).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        Message msg = getFeed(user).get(mid);

        if (msg == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok(msg);
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        Log.info("getMessages : " + time);

        String domain = user.split("@")[1];

        if (!domain.equals(Domain.get()))
            return forwardGetMessages(user, time);

        if (!hasUser(user).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok(getFeed(user).values().stream()
                .filter((e) -> e.getCreationTime() > time).toList());
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        Log.info("subUser : " + user + ", " + userSub);

        getUser(user, pwd);

        String subDomain = userSub.split("@")[1];

        if (!hasUser(userSub).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        if (!subDomain.equals(Domain.get()))
            subUserPropagate(user, userSub);

        getFollowing(user).add(userSub);
        getFollowers(userSub).add(user);

        return Result.ok();
    }

    @Override
    public Result<Void> subUserOtherDomain(String user, String userSub) {
        Log.info("subUserOtherDomain : " + user + ", " + userSub);

        getFollowing(user).add(userSub);
        getFollowers(userSub).add(user);

        return Result.ok();
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        Log.info("unsubscribeUser : " + user + ", " + userSub);

        getUser(user, pwd);

        String subDomain = userSub.split("@")[1];

        if (!hasUser(userSub).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        if (!subDomain.equals(Domain.get()))
            unsubUserPropagate(user, userSub);

        getFollowing(user).remove(userSub);
        getFollowers(userSub).remove(user);

        return Result.ok();
    }

    @Override
    public Result<Void> unsubUserOtherDomain(String user, String userSub) {
        Log.info("unsubUserOtherDomain : " + user + ", " + userSub);

        getFollowing(user).remove(userSub);
        getFollowers(userSub).remove(user);

        return Result.ok();
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        Log.info("listSubs : " + user);

        if (!hasUser(user).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok(getFollowing(user).stream().toList());
    }

    @Override
    public Result<User> getUser(String user, String pwd) {
        Log.info("getUser : " + user);

        String[] userInfo = user.split("@");
        return UsersClientFactory.get(userInfo[1]).getUser(userInfo[0], pwd);
    }

    @Override
    public Result<Boolean> hasUser(String user) {
        Log.info("hasUser : " + user);

        String[] userInfo = user.split("@");
        return UsersClientFactory.get(userInfo[1]).hasUser(userInfo[0]);
    }

    @Override
    public Result<Long> postMessagePropagate(String user, Message msg) {
        Log.info("postMessagePropagate : " + user);

        String[] userInfo = user.split("@");
        return FeedsClientFactory.get(userInfo[1]).postMessageOtherDomain(user, msg);
    }

    @Override
    public Result<Void> subUserPropagate(String user, String userSub) {
        Log.info("subUserPropagate : " + user);

        String[] userInfo = user.split("@");
        return FeedsClientFactory.get(userInfo[1]).subUserOtherDomain(user, userSub);
    }

    @Override
    public Result<Void> unsubUserPropagate(String user, String userSub) {
        Log.info("unsubUserPropagate : " + user);

        String[] userInfo = user.split("@");
        return FeedsClientFactory.get(userInfo[1]).unsubUserOtherDomain(user, userSub);
    }

    @Override
    public Result<Message> forwardGetMessage(String user, long mid) {
        Log.info("forwardGetMessage : " + user);

        String[] userInfo = user.split("@");
        return FeedsClientFactory.get(userInfo[1]).getMessage(user, mid);
    }

    @Override
    public Result<List<Message>> forwardGetMessages(String user, long time) {
        Log.info("forwardGetMessages : " + user);

        String[] userInfo = user.split("@");
        return FeedsClientFactory.get(userInfo[1]).getMessages(user, time);
    }
}
