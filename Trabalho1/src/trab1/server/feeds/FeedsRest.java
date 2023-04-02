package trab1.server.feeds;

import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.WebApplicationException;

import trab1.User;
import trab1.Message;
import trab1.rest.FeedsService;
import trab1.rest.UsersService;
import trab1.server.RestServer;
import trab1.server.users.UsersServer;

public class FeedsRest extends RestServer {
    private User srv_getUser(String user, String pwd) {
        String[] userInfo = user.split("@");
        String name = userInfo[0];
        String domain = userInfo[1];

        WebTarget target = getServiceTarget(domain, UsersServer.SERVICE).path(UsersService.PATH);

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

        WebTarget target = getServiceTarget(domain, UsersServer.SERVICE).path(UsersService.PATH);

        Response response = target.queryParam(UsersService.QUERY, name)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != Status.OK.getStatusCode())
            return false;

        List<User> users = response.readEntity(new GenericType<List<User>>() {
        });

        List<String> userNames = users.stream().map(u -> u.getName()).toList();

        if (userNames.contains(name))
            return true;

        return false;
    }

    private int srv_postMessagePropagate(String user, Message msg) {
        WebTarget target = getServiceTarget(user.split("@")[1], FeedsServer.SERVICE);

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
        WebTarget target = getServiceTarget(userSub.split("@")[1], FeedsServer.SERVICE);

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
        WebTarget target = getServiceTarget(userSub.split("@")[1], FeedsServer.SERVICE);

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
        WebTarget target = getServiceTarget(user.split("@")[1], FeedsServer.SERVICE);

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
        WebTarget target = getServiceTarget(user.split("@")[1], FeedsServer.SERVICE);

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

    protected User getUser(String user, String pwd) {
        return retry(() -> srv_getUser(user, pwd));
    }

    protected boolean hasUser(String user) {
        return retry(() -> srv_hasUser(user));
    }

    protected void postMessagePropagate(String user, Message msg) {
        retry(() -> srv_postMessagePropagate(user, msg));
    }

    protected void subUserPropagate(String user, String userSub) {
        retry(() -> srv_subUserPropagate(user, userSub));
    }

    protected void unsubUserPropagate(String user, String userSub) {
        retry(() -> srv_unsubUserPropagate(user, userSub));
    }

    protected Message forwardGetMessage(String user, long msgId) {
        return retry(() -> srv_forwardGetMessage(user, msgId));
    }

    protected List<Message> forwardGetMessages(String user, long time) {
        return retry(() -> srv_forwardGetMessages(user, time));
    }
}
