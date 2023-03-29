package trab1.server.feeds;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import trab1.Domain;
import trab1.Message;
import trab1.rest.FeedsService;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

public class FeedsResource extends FeedsRest implements FeedsService {
    private final Map<String, Map<Long, Message>> feeds;
    private final Map<String, Set<String>> followers;
    private final Map<String, Set<String>> following;

    public FeedsResource() {
        super();

        feeds = new ConcurrentHashMap<>();
        followers = new ConcurrentHashMap<>();
        following = new ConcurrentHashMap<>();
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        if (msg == null)
            throw new WebApplicationException(Status.BAD_REQUEST);

        getUser(user, pwd);

        feeds.putIfAbsent(user, new ConcurrentHashMap<>());

        long id = UUID.randomUUID().getMostSignificantBits();

        msg.setId(id);

        for (String u : followers.get(user)) {
            String subDomain = u.split("@")[1];

            if (!subDomain.equals(Domain.get())) {
                new Thread(() -> {
                    retry(() -> postMessagePropagate(u, msg));
                }).start();
            }

            Map<Long, Message> userFeed = feeds.get(u);
            userFeed.put(id, msg);
        }

        feeds.get(user).put(id, msg);

        return id;
    }

    @Override
    public long postMessageOtherDomain(String user, Message msg) {
        if (msg == null)
            throw new WebApplicationException(Status.BAD_REQUEST);

        feeds.putIfAbsent(user, new ConcurrentHashMap<>());

        Map<Long, Message> userFeed = feeds.get(user);
        userFeed.put(msg.getId(), msg);

        return msg.getId();
    }

    @Override
    public void removeFromPersonalFeed(String user, long msgId, String pwd) {
        if (msgId < 0)
            throw new WebApplicationException(Status.BAD_REQUEST);

        getUser(user, pwd);

        feeds.putIfAbsent(user, new ConcurrentHashMap<>());

        if (feeds.get(user).remove(msgId) == null)
            throw new WebApplicationException(Status.NOT_FOUND);
    }

    @Override
    public Message getMessage(String user, long msgId) {
        if (msgId < 0)
            throw new WebApplicationException(Status.BAD_REQUEST);

        Map<Long, Message> feed = feeds.get(user);

        if (feed == null)
            throw new WebApplicationException(Status.NOT_FOUND);

        Message msg = feed.get(msgId);

        if (msg == null)
            throw new WebApplicationException(Status.NOT_FOUND);

        return msg;
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        Map<Long, Message> feed = feeds.get(user);

        if (feed == null)
            throw new WebApplicationException(Status.NOT_FOUND);

        return feed.values().stream().filter((e) -> e.getCreationTime() >= time).toList();
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        getUser(user, pwd);

        String subDomain = userSub.split("@")[1];

        if (!subDomain.equals(Domain.get()))
            subUserPropagate(user, userSub);

        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new CopyOnWriteArraySet<>());
        followers.putIfAbsent(userSub, new CopyOnWriteArraySet<>());

        following.get(user).add(userSub);
        followers.get(userSub).add(user);
    }

    @Override
    public void subUserOtherDomain(String user, String userSub) {
        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new CopyOnWriteArraySet<>());
        followers.putIfAbsent(userSub, new CopyOnWriteArraySet<>());

        following.get(user).add(userSub);
        followers.get(userSub).add(user);
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        getUser(user, pwd);

        String subDomain = userSub.split("@")[1];

        if (!subDomain.equals(Domain.get()))
            retry(() -> unsubUserPropagate(user, userSub));

        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new CopyOnWriteArraySet<>());
        followers.putIfAbsent(userSub, new CopyOnWriteArraySet<>());

        following.get(user).remove(userSub);
        followers.get(userSub).remove(user);
    }

    @Override
    public void unsubUserOtherDomain(String user, String userSub) {
        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new CopyOnWriteArraySet<>());
        followers.putIfAbsent(userSub, new CopyOnWriteArraySet<>());

        following.get(user).remove(userSub);
        followers.get(userSub).remove(user);
    }

    @Override
    public List<String> listSubs(String user) {
        if (followers.get(user) == null)
            throw new WebApplicationException(Status.NOT_FOUND);

        return followers.get(user).stream().toList();
    }
}
