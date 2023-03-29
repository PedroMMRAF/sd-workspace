package trab1.server.feeds;

import java.util.logging.Logger;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
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
    private static Logger Log = Logger.getLogger(RestServer.class.getName());

    private User srv_getUser(String user, String pwd) {
        String[] userInfo = user.split("@");
        String name = userInfo[0];
        String domain = userInfo[1];

        Log.info("Requesting user info...");

        WebTarget target = getServiceTarget(domain, UsersServer.SERVICE);

        Response response = target.path(UsersService.PATH).path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            return response.readEntity(User.class);

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    private int srv_postMessagePropagate(String user, Message msg) {
        Log.info("Sending message...");

        WebTarget target = getServiceTarget(user.split("@")[1], FeedsServer.SERVICE);

        Response response = target.path(FeedsService.PATH).path(FeedsService.PROPAGATE)
                .path(user)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(msg));

        if (response.getStatus() == Status.NO_CONTENT.getStatusCode())
            return 0;

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    private int srv_subUserPropagate(String user, String userSub) {
        Log.info("Sending user sub...");

        WebTarget target = getServiceTarget(userSub.split("@")[1], FeedsServer.SERVICE);

        Response response = target.path(FeedsService.PATH).path(FeedsService.PROPAGATE)
                .path(user).path(userSub)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(null));

        if (response.getStatus() == Status.NO_CONTENT.getStatusCode())
            return 0;

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    private int srv_unsubUserPropagate(String user, String userSub) {
        Log.info("Sending user sub...");

        WebTarget target = getServiceTarget(userSub.split("@")[1], FeedsServer.SERVICE);

        Response response = target.path(FeedsService.PATH).path(FeedsService.PROPAGATE)
                .path(user).path(userSub)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (response.getStatus() == Status.NO_CONTENT.getStatusCode())
            return 0;

        throw new WebApplicationException(Status.FORBIDDEN);
    }

    protected User getUser(String user, String pwd) {
        return retry(() -> srv_getUser(user, pwd));
    }

    protected int postMessagePropagate(String user, Message msg) {
        return retry(() -> srv_postMessagePropagate(user, msg));
    }

    protected int subUserPropagate(String user, String userSub) {
        return retry(() -> srv_subUserPropagate(user, userSub));
    }

    protected int unsubUserPropagate(String user, String userSub) {
        return retry(() -> srv_unsubUserPropagate(user, userSub));
    }
}
