package api.server.feeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.glassfish.jersey.client.ClientConfig;

import api.User;
import api.Message;
import api.Discovery;
import api.rest.FeedsService;
import api.rest.UsersService;
import api.server.users.UsersServer;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.client.ClientBuilder;

public class FeedsResource implements FeedsService {
    private static Logger Log = Logger.getLogger(FeedsResource.class.getName());

    private final Map<Long, Message> allMessages;
    private final Map<String, Map<Long, Message>> feeds;

    public FeedsResource() {
        allMessages = new HashMap<>();
        feeds = new HashMap<>();
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

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        if (msg == null)
            throw new WebApplicationException(Status.BAD_REQUEST);

        getUser(user, pwd);

        feeds.putIfAbsent(user, new HashMap<>());

        long id = allMessages.size();

        msg.setId(id);

        allMessages.put(id, msg);
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
        Message msg = allMessages.get(msgId);

        if (feed == null || msg == null)
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subUser'");
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unsubscribeUser'");
    }

    @Override
    public List<User> listSubs(String user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listSubs'");
    }
}
