package trab1.server.feeds;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import trab1.Domain;
import trab1.Message;
import trab1.rest.FeedsService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

@Singleton
public class FeedsResource extends FeedsRest implements FeedsService {
    private static Logger Log = Logger.getLogger(FeedsResource.class.getName());

    private Map<String, Map<Long, Message>> feeds;
    private Map<String, Set<String>> following;
    private Map<String, Set<String>> followers;

    public FeedsResource() {
        super();

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
    public long postMessage(String user, String pwd, Message msg) {
        String userDomain = user.split("@")[1];

        if (msg == null
                || msg.getUser() == null
                || msg.getDomain() == null
                || !msg.getDomain().equals(userDomain)
                || !Domain.get().equals(userDomain))
            throw new WebApplicationException(Status.BAD_REQUEST);

        long id = UUID.randomUUID().getMostSignificantBits();
        msg.setId(id);

        Log.info("postMessage : " + msg);

        getUser(user, pwd);

        getFeed(user).put(id, msg);

        for (String u : getFollowers(user)) {
            String subDomain = u.split("@")[1];

            if (subDomain.equals(Domain.get())) {
                getFeed(u).put(id, msg);
            } else {
                new Thread(() -> postMessagePropagate(u, msg)).start();
            }
        }

        return id;
    }

    @Override
    public long postMessageOtherDomain(String user, Message msg) {
        Log.info("postMessageOtherDomain : " + msg);

        getFeed(user).put(msg.getId(), msg);

        return msg.getId();
    }

    @Override
    public void removeFromPersonalFeed(String user, long msgId, String pwd) {
        Log.info("removeFromPersonalFeed : " + msgId);

        getUser(user, pwd);

        if (getFeed(user).remove(msgId) == null)
            throw new WebApplicationException(Status.NOT_FOUND);
    }

    @Override
    public Message getMessage(String user, long msgId) {
        Log.info("getMessage : " + msgId);

        String userDomain = user.split("@")[1];

        if (!userDomain.equals(Domain.get()))
            return forwardGetMessage(user, msgId);

        if (!hasUser(user))
            throw new WebApplicationException(Status.NOT_FOUND);

        Message msg = getFeed(user).get(msgId);

        if (msg == null)
            throw new WebApplicationException(Status.NOT_FOUND);

        return msg;
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        Log.info("getMessages : " + time);

        String userDomain = user.split("@")[1];

        if (!userDomain.equals(Domain.get()))
            return forwardGetMessages(user, time);

        if (!hasUser(user))
            throw new WebApplicationException(Status.NOT_FOUND);

        return getFeed(user).values().stream().filter((e) -> e.getCreationTime() > time).toList();
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        Log.info("subUser : " + user + ", " + userSub);

        getUser(user, pwd);

        String subDomain = userSub.split("@")[1];

        if (!hasUser(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        if (!subDomain.equals(Domain.get()))
            subUserPropagate(user, userSub);

        getFollowing(user).add(userSub);
        getFollowers(userSub).add(user);
    }

    @Override
    public void subUserOtherDomain(String user, String userSub) {
        Log.info("subUserOtherDomain : " + user + ", " + userSub);

        getFollowing(user).add(userSub);
        getFollowers(userSub).add(user);
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        Log.info("unsubscribeUser : " + user + ", " + userSub);

        getUser(user, pwd);

        String subDomain = userSub.split("@")[1];

        if (!hasUser(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        if (!subDomain.equals(Domain.get()))
            unsubUserPropagate(user, userSub);

        getFollowing(user).remove(userSub);
        getFollowers(userSub).remove(user);
    }

    @Override
    public void unsubUserOtherDomain(String user, String userSub) {
        Log.info("unsubUserOtherDomain : " + user + ", " + userSub);

        getFollowing(user).remove(userSub);
        getFollowers(userSub).remove(user);
    }

    @Override
    public List<String> listSubs(String user) {
        Log.info("listSubs : " + user);

        if (!hasUser(user))
            throw new WebApplicationException(Status.NOT_FOUND);

        return getFollowing(user).stream().toList();
    }
}
