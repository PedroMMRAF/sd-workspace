package trab1.servers.rest;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;

import trab1.Discovery;
import trab1.api.Message;
import trab1.api.User;
import trab1.api.java.Result;
import trab1.api.rest.FeedsService;
import trab1.api.rest.UsersService;
import trab1.servers.java.JavaFeeds;

public class RestFeeds extends JavaFeeds {
    private static Logger Log = Logger.getLogger(RestFeeds.class.getName());

    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;

    protected static final int RETRY_SLEEP = 3000;
    protected static final int MAX_RETRIES = 10;

    protected final Client client;
    protected final ClientConfig config;

    public RestFeeds() {
        config = new ClientConfig();

        config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

        client = ClientBuilder.newClient(config);
    }

    public WebTarget getServiceTarget(String domain, String service) {
        return client.target(Discovery.getInstance().knownUrisOf(domain, service, 1)[0].toString());
    }

    protected <T> Result<T> retry(Supplier<T> func) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                return Result.ok(func.get());
            } catch (ProcessingException x) {
                Log.fine("ProcessingException: " + x.getMessage());
                sleep(RETRY_SLEEP);
            }
        }
        return Result.error(Result.ErrorCode.INTERNAL_ERROR);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException x) {
        }
    }

    public Result<User> getUser(String user, String pwd) {
        return retry(() -> srv_getUser(user, pwd));
    }

    public boolean hasUser(String user) {
        return retry(() -> srv_hasUser(user)).value();
    }

    public void postMessagePropagate(String user, Message msg) {
        retry(() -> srv_postMessagePropagate(user, msg));
    }

    public void subUserPropagate(String user, String userSub) {
        retry(() -> srv_subUserPropagate(user, userSub));
    }

    public void unsubUserPropagate(String user, String userSub) {
        retry(() -> srv_unsubUserPropagate(user, userSub));
    }

    public Result<Message> forwardGetMessage(String user, long msgId) {
        return retry(() -> srv_forwardGetMessage(user, msgId));
    }

    public Result<List<Message>> forwardGetMessages(String user, long time) {
        return retry(() -> srv_forwardGetMessages(user, time));
    }

    private User srv_getUser(String user, String pwd) {
        String[] userInfo = user.split("@");
        String name = userInfo[0];
        String domain = userInfo[1];

        WebTarget target = getServiceTarget(domain, RestUsersServer.SERVICE).path(UsersService.PATH);

        Response response = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            return response.readEntity(User.class);

        throw new WebApplicationException(response.getStatus());
    }

    private boolean srv_hasUser(String user) {
        String[] userInfo = user.split("@");
        String name = userInfo[0];
        String domain = userInfo[1];

        WebTarget target = getServiceTarget(domain, RestUsersServer.SERVICE).path(UsersService.PATH);

        Response response = target.queryParam(UsersService.QUERY, name)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != Status.OK.getStatusCode())
            return false;

        List<User> users = response.readEntity(new GenericType<List<User>>() {
        });

        return users.stream().map(u -> u.getName()).toList().contains(name);
    }

    private int srv_postMessagePropagate(String user, Message msg) {
        WebTarget target = getServiceTarget(user.split("@")[1], RestFeedsServer.SERVICE);

        Response response = target.path(FeedsService.PATH)
                .path(FeedsService.PROPAGATE).path(user)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(msg));

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            return 0;

        throw new WebApplicationException(response.getStatus());
    }

    private int srv_subUserPropagate(String user, String userSub) {
        WebTarget target = getServiceTarget(userSub.split("@")[1], RestFeedsServer.SERVICE);

        Response response = target.path(FeedsService.PATH).path(FeedsService.PROPAGATE)
                .path(user).path(userSub)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(null));

        if (response.getStatus() == Status.NO_CONTENT.getStatusCode())
            return 0;

        throw new WebApplicationException(response.getStatus());
    }

    private int srv_unsubUserPropagate(String user, String userSub) {
        WebTarget target = getServiceTarget(userSub.split("@")[1], RestFeedsServer.SERVICE);

        Response response = target.path(FeedsService.PATH).path(FeedsService.PROPAGATE)
                .path(user).path(userSub)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (response.getStatus() == Status.NO_CONTENT.getStatusCode())
            return 0;

        throw new WebApplicationException(response.getStatus());
    }

    private Message srv_forwardGetMessage(String user, long msgId) {
        WebTarget target = getServiceTarget(user.split("@")[1], RestFeedsServer.SERVICE);

        Response response = target.path(FeedsService.PATH)
                .path(user).path(Long.toString(msgId))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            return response.readEntity(Message.class);

        throw new WebApplicationException(response.getStatus());
    }

    private List<Message> srv_forwardGetMessages(String user, long time) {
        WebTarget target = getServiceTarget(user.split("@")[1], RestFeedsServer.SERVICE);

        Response response = target.path(FeedsService.PATH)
                .path(user).queryParam(FeedsService.TIME, time)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            return response.readEntity(new GenericType<List<Message>>() {
            });

        throw new WebApplicationException(response.getStatus());
    }
}
