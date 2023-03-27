package trab1.server.feeds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.glassfish.jersey.client.ClientConfig;

import trab1.User;
import trab1.Message;
import trab1.Discovery;
import trab1.rest.FeedsService;
import trab1.rest.UsersService;
import trab1.server.users.UsersServer;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

public class FeedsResource implements FeedsService {
    private static Logger Log = Logger.getLogger(FeedsResource.class.getName());

    private final Map<Long, Message> allMessages;
    private final Map<String, Map<Long, Message>> feeds;
    private final Map<String, Set<String>> followers;
    private final Map<String, Set<String>> following;

    public FeedsResource() {
        allMessages = new HashMap<>();
        feeds = new HashMap<>();

        followers = new HashMap<>();
        following = new HashMap<>();
    }

    private User getUser(String user, String pwd) {
        String serverUrl = Discovery.getInstance().knownUrisOf(UsersServer.SERVICE, 1)[0].toString();

        Log.info("Requesting user info...");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverUrl).path(UsersService.PATH);

        Response response = target.path(user)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            return response.readEntity(User.class);

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    private List<User> getUsers(Set<String> users) {
        String serverUrl = Discovery.getInstance().knownUrisOf(UsersServer.SERVICE, 1)[0].toString();

        System.out.println("Sending request to server.");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverUrl).path(UsersService.PATH);

        Response response = target.path(UsersService.GET)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(users, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            return response.readEntity(new GenericType<List<User>>() {
            });

        throw new WebApplicationException(response.getStatus());
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        if (msg == null)
            throw new WebApplicationException(Status.BAD_REQUEST);

        getUser(user, pwd);

        feeds.putIfAbsent(user, new HashMap<>());

        long id = UUID.randomUUID().getMostSignificantBits();

        msg.setId(id);

        allMessages.put(id, msg);
        for (String u : followers.get(user)) {
            Map<Long, Message> userFeed = feeds.get(u);
            userFeed.put(id, msg);
        }
        feeds.get(user).put(id, msg);

        return id;
    }

    @Override
    public void removeFromPersonalFeed(String user, long msgId, String pwd) {
        if (msgId < 0)
            throw new WebApplicationException(Status.BAD_REQUEST);

        getUser(user, pwd);

        feeds.putIfAbsent(user, new HashMap<>());

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

        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new HashSet<>());
        followers.putIfAbsent(userSub, new HashSet<>());

        following.get(user).add(userSub);
        followers.get(userSub).add(user);
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        getUser(user, pwd);

        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new HashSet<>());
        followers.putIfAbsent(userSub, new HashSet<>());

        following.get(user).remove(userSub);
        followers.get(userSub).remove(user);
    }

    @Override
    public List<User> listSubs(String user) {
        return getUsers(followers.get(user));
    }
}
