package trab2.servers.java;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import trab2.api.Message;
import trab2.api.User;
import trab2.api.java.Feeds;
import trab2.api.java.Result;
import trab2.clients.FeedsClientFactory;
import trab2.clients.UsersClientFactory;
import trab2.servers.Domain;

public class JavaFeeds implements Feeds {
    private static Logger Log = Logger.getLogger(JavaFeeds.class.getName());

    private AtomicLong seq;
    private Map<String, Map<Long, Message>> feeds;
    private Map<String, Set<String>> following;
    private Map<String, Set<String>> followers;

    public JavaFeeds(int seq) {
        this.seq = new AtomicLong(seq * 65535);
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

    private static void logInfo(String name, Object... pairs) {
        StringBuilder result = new StringBuilder("Feeds : ");

        result.append(name);
        result.append(" : ");

        for (int i = 0; i < pairs.length; i += 2) {
            result.append(pairs[i]);
            result.append(" = ");
            result.append(pairs[i + 1]);

            if (i < pairs.length - 2)
                result.append(", ");
        }

        Log.info(result.toString());
    }

    @Override
    public Result<Long> postMessage(Long version, String user, String pwd, Message msg) {
        String userDomain = user.split("@")[1];

        if (msg == null
                || msg.getUser() == null
                || msg.getDomain() == null
                || !msg.getDomain().equals(userDomain)
                || !Domain.get().equals(userDomain))
            return Result.error(Result.ErrorCode.BAD_REQUEST);

        long id = seq.getAndIncrement();
        msg.setId(id);

        logInfo("postMessage", "user", user, "pwd", pwd, "msg", msg);

        Result<User> res = getUser(version, user, pwd);

        if (!res.isOK())
            return Result.error(res.error());

        getFeed(user).put(id, msg);

        for (String u : getFollowers(user)) {
            String subDomain = u.split("@")[1];

            if (subDomain.equals(Domain.get())) {
                getFeed(u).put(id, msg);
            } else {
                new Thread(() -> postMessagePropagate(version, u, msg)).start();
            }
        }

        return Result.ok(id);
    }

    @Override
    public Result<Long> postMessageOtherDomain(Long version, String user, Message msg) {
        logInfo("postMessageOtherDomain", "user", user, "msg", msg);

        getFeed(user).put(msg.getId(), msg);

        return Result.ok(msg.getId());
    }

    @Override
    public Result<Void> removeFromPersonalFeed(Long version, String user, long mid, String pwd) {
        logInfo("postMessageOtherDomain", "user", user, "mid", mid, "pwd", pwd);

        Result<User> res = getUser(version, user, pwd);

        if (!res.isOK())
            return Result.error(res.error());

        if (getFeed(user).remove(mid) == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok();
    }

    @Override
    public Result<Message> getMessage(Long version, String user, long mid) {
        logInfo("getMessage", "user", user, "mid", mid);

        String domain = user.split("@")[1];

        if (!domain.equals(Domain.get()))
            return forwardGetMessage(version, user, mid);

        if (!hasUser(version, user).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        Message msg = getFeed(user).get(mid);

        if (msg == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok(msg);
    }

    @Override
    public Result<List<Message>> getMessages(Long version, String user, long time) {
        logInfo("getMessages", "user", user, "time", time);

        String domain = user.split("@")[1];

        if (!domain.equals(Domain.get()))
            return forwardGetMessages(version, user, time);

        if (!hasUser(version, user).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok(getFeed(user).values().stream()
                .filter(m -> m.getCreationTime() > time).toList());
    }

    @Override
    public Result<Void> subUser(Long version, String user, String userSub, String pwd) {
        logInfo("subUser", "user", user, "userSub", userSub, "pwd", pwd);

        getUser(version, user, pwd);

        String subDomain = userSub.split("@")[1];

        if (!hasUser(version, userSub).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        if (!subDomain.equals(Domain.get()))
            subUserPropagate(version, user, userSub);

        getFollowing(user).add(userSub);
        getFollowers(userSub).add(user);

        return Result.ok();
    }

    @Override
    public Result<Void> subUserOtherDomain(Long version, String user, String userSub) {
        logInfo("subUserOtherDomain", "user", user, "userSub", userSub);

        getFollowing(user).add(userSub);
        getFollowers(userSub).add(user);

        return Result.ok();
    }

    @Override
    public Result<Void> unsubscribeUser(Long version, String user, String userSub, String pwd) {
        logInfo("unsubscribeUser", "user", user, "userSub", userSub, "pwd", pwd);

        getUser(version, user, pwd);

        String subDomain = userSub.split("@")[1];

        if (!hasUser(version, userSub).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        if (!subDomain.equals(Domain.get()))
            unsubUserPropagate(version, user, userSub);

        getFollowing(user).remove(userSub);
        getFollowers(userSub).remove(user);

        return Result.ok();
    }

    @Override
    public Result<Void> unsubUserOtherDomain(Long version, String user, String userSub) {
        logInfo("unsubUserOtherDomain", "user", user, "userSub", userSub);

        getFollowing(user).remove(userSub);
        getFollowers(userSub).remove(user);

        return Result.ok();
    }

    @Override
    public Result<List<String>> listSubs(Long version, String user) {
        logInfo("listSubs", "user", user);

        if (!hasUser(version, user).value())
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok(getFollowing(user).stream().toList());
    }

    @Override
    public Result<User> getUser(Long version, String user, String pwd) {
        logInfo("getUser", "user", user, "pwd", pwd);

        String[] userInfo = user.split("@");
        return UsersClientFactory.get(userInfo[1]).getUser(userInfo[0], pwd);
    }

    @Override
    public Result<Boolean> hasUser(Long version, String user) {
        logInfo("hasUser", "user", user);

        String[] userInfo = user.split("@");
        return UsersClientFactory.get(userInfo[1]).hasUser(userInfo[0]);
    }

    @Override
    public Result<Long> postMessagePropagate(Long version, String user, Message msg) {
        logInfo("postMessagePropagate", "user", user, "msg", msg);

        String[] userInfo = user.split("@");
        return FeedsClientFactory.get(userInfo[1]).postMessageOtherDomain(version, user, msg);
    }

    @Override
    public Result<Void> subUserPropagate(Long version, String user, String userSub) {
        logInfo("subUserPropagate", "user", user, "msg", userSub);

        String subDomain = userSub.split("@")[1];
        return FeedsClientFactory.get(subDomain).subUserOtherDomain(version, user, userSub);
    }

    @Override
    public Result<Void> unsubUserPropagate(Long version, String user, String userSub) {
        logInfo("unsubUserPropagate", "user", user, "msg", userSub);

        String subDomain = userSub.split("@")[1];
        return FeedsClientFactory.get(subDomain).unsubUserOtherDomain(version, user, userSub);
    }

    @Override
    public Result<Message> forwardGetMessage(Long version, String user, long mid) {
        logInfo("forwardGetMessage", "user", user, "mid", mid);

        String[] userInfo = user.split("@");
        return FeedsClientFactory.get(userInfo[1]).getMessage(version, user, mid);
    }

    @Override
    public Result<List<Message>> forwardGetMessages(Long version, String user, long time) {
        logInfo("forwardGetMessages", "user", user, "time", time);

        String[] userInfo = user.split("@");
        return FeedsClientFactory.get(userInfo[1]).getMessages(version, user, time);
    }
}
