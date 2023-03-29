package trab1.server.feeds;

import java.net.URI;
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
import trab1.Domain;
import trab1.rest.FeedsService;
import trab1.rest.UsersService;
import trab1.server.users.UsersServer;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
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
            String subDomain = u.split("@")[1];

        if (!subDomain.equals(Domain.get())){
            postMessagePropagate(u, msg);
        }
            
            Map<Long, Message> userFeed = feeds.get(u);
            userFeed.put(id, msg);
        }
        feeds.get(user).put(id, msg);

        return id;
    }

    @Override
    public long postMessageOtherDomain(String user, String domain, Message msg) {
        if (msg == null)
            throw new WebApplicationException(Status.BAD_REQUEST);

        feeds.putIfAbsent(user, new HashMap<>());


        allMessages.put(msg.getId(), msg);
            
        Map<Long, Message> userFeed = feeds.get(user);
        userFeed.put(msg.getId(), msg);
        
        return msg.getId();
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

        String subDomain = userSub.split("@")[1];

        if (!subDomain.equals(Domain.get()))
            subUserPropagate(user, userSub);

        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new HashSet<>());
        followers.putIfAbsent(userSub, new HashSet<>());

        following.get(user).add(userSub);
        followers.get(userSub).add(user);
    }

    @Override
    public void subUserOtherDomain(String user, String userSub, String domain) {
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

        String subDomain = userSub.split("@")[1];

        if (!subDomain.equals(Domain.get()))
            unsubUserPropagate(user, userSub);

        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new HashSet<>());
        followers.putIfAbsent(userSub, new HashSet<>());

        following.get(user).remove(userSub);
        followers.get(userSub).remove(user);
    }

    @Override
    public void unsubUserOtherDomain(String user, String userSub, String domain) {
        if (!feeds.containsKey(userSub))
            throw new WebApplicationException(Status.NOT_FOUND);

        following.putIfAbsent(user, new HashSet<>());
        followers.putIfAbsent(userSub, new HashSet<>());

        following.get(user).remove(userSub);
        followers.get(userSub).remove(user);
    }

    @Override
    public List<String> listSubs(String user) {
        return followers.get(user).stream().toList();
    }

    private User getUser(String user, String pwd) {
        String[] userInfo = user.split("@");
        String name = userInfo[0];
        String domain = userInfo[1];

        URI serverURI = Discovery.getInstance().knownUrisOf(domain, UsersServer.SERVICE, 1)[0];

        Log.info("Requesting user info...");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverURI).path(UsersService.PATH);

        Response response = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            return response.readEntity(User.class);

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    private void subUserPropagate(String user, String userSub) {
        URI serverURI = Discovery.getInstance().knownUrisOf(userSub.split("@")[1], FeedsServer.SERVICE, 1)[0];

        Log.info("Sending user sub...");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverURI).path(FeedsService.PATH);

        Response response = target.path("sub").path(user).path(userSub)
                .queryParam(FeedsService.DOMAIN, user.split("@")[1])
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(null, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Status.NO_CONTENT.getStatusCode())
            return;

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    
    private void unsubUserPropagate(String user, String userSub) {
        URI serverURI = Discovery.getInstance().knownUrisOf(userSub.split("@")[1], FeedsServer.SERVICE, 1)[0];

        Log.info("Sending user sub...");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverURI).path(FeedsService.PATH);

        Response response = target.path("sub").path(user).path(userSub)
                .queryParam(FeedsService.DOMAIN, user.split("@")[1])
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (response.getStatus() == Status.NO_CONTENT.getStatusCode())
            return;

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    private void postMessagePropagate(String user, Message msg){
        URI serverURI = Discovery.getInstance().knownUrisOf(user.split("@")[1], FeedsServer.SERVICE, 1)[0];

        Log.info("Sending message...");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverURI).path(FeedsService.PATH);

        Response response = target.path(user)
                .queryParam(FeedsService.DOMAIN, user.split("@")[1])
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(msg, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Status.NO_CONTENT.getStatusCode())
            return;

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    

}
