package trab2.clients;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import trab2.api.Message;
import trab2.api.User;
import trab2.api.java.Feeds;
import trab2.api.java.Result;
import trab2.api.rest.FeedsService;

import java.net.URI;
import java.util.List;

public class RestFeedsClient extends RestClient implements Feeds {
    private final WebTarget target;

    public RestFeedsClient(URI serverURI) {
        super();

        target = client.target(serverURI).path(FeedsService.PATH);
    }

    public Result<Long> clt_postMessage(String user, String pwd, Message msg) {
        Response r = target.path(user)
                .queryParam(FeedsService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(msg));

        return Result.fromResponse(r, Long.class);
    }

    public Result<Long> clt_postMessageOtherDomain(String user, Message msg) {
        Response r = target.path("propagate").path(user)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(msg));

        return Result.fromResponse(r, Long.class);
    }

    public Result<Void> clt_removeFromPersonalFeed(String user, long mid, String pwd) {
        Response r = target.path(user).path(Long.toString(mid))
                .queryParam(FeedsService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        return Result.fromResponse(r);
    }

    public Result<Message> clt_getMessage(String user, long mid) {
        Response r = target.path(user).path(Long.toString(mid))
                .request().accept(MediaType.APPLICATION_JSON).get();

        return Result.fromResponse(r, Message.class);
    }

    public Result<List<Message>> clt_getMessages(String user, long time) {
        Response r = target.path(user)
                .queryParam(FeedsService.TIME, time).request()
                .accept(MediaType.APPLICATION_JSON).get();

        return Result.fromResponse(r, new GenericType<List<Message>>() {
        });
    }

    public Result<Void> clt_subUser(String user, String userSub, String pwd) {
        Response r = target.path("sub").path(user).path(userSub)
                .queryParam(FeedsService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(null));

        return Result.fromResponse(r);
    }

    private Result<Void> clt_subUserOtherDomain(String user, String userSub) {
        Response r = target.path("propagate").path(user).path(userSub)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(null));

        return Result.fromResponse(r);
    }

    public Result<Void> clt_unsubscribeUser(String user, String userSub, String pwd) {
        Response r = target.path("sub").path(user).path(userSub)
                .queryParam(FeedsService.PWD, pwd)
                .request().accept(MediaType.APPLICATION_JSON).delete();

        return Result.fromResponse(r);
    }

    private Result<Void> clt_unsubUserOtherDomain(String user, String userSub) {
        Response r = target.path("propagate").path(user).path(userSub)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        return Result.fromResponse(r);
    }

    public Result<List<String>> clt_listSubs(String user) {
        Response r = target.path("sub").path("list").path(user)
                .request().accept(MediaType.APPLICATION_JSON)
                .delete();

        return Result.fromResponse(r, new GenericType<List<String>>() {
        });
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        return retry(() -> clt_postMessage(user, pwd, msg));
    }

    @Override
    public Result<Long> postMessageOtherDomain(String user, Message msg) {
        return retry(() -> clt_postMessageOtherDomain(user, msg));
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        return retry(() -> clt_removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        return retry(() -> clt_getMessage(user, mid));
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        return retry(() -> clt_getMessages(user, time));
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        return retry(() -> clt_subUser(user, userSub, pwd));
    }

    @Override
    public Result<Void> subUserOtherDomain(String user, String userSub) {
        return retry(() -> clt_subUserOtherDomain(user, userSub));
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        return retry(() -> clt_unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public Result<Void> unsubUserOtherDomain(String user, String userSub) {
        return retry(() -> clt_unsubUserOtherDomain(user, userSub));
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        return retry(() -> clt_listSubs(user));
    }

    // Unimplemented on client

    @Override
    public Result<User> getUser(String user, String pwd) {
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public Result<Boolean> hasUser(String user) {
        throw new UnsupportedOperationException("Unimplemented method 'hasUser'");
    }

    @Override
    public Result<Long> postMessagePropagate(String user, Message msg) {
        throw new UnsupportedOperationException("Unimplemented method 'postMessagePropagate'");
    }

    @Override
    public Result<Void> subUserPropagate(String user, String userSub) {
        throw new UnsupportedOperationException("Unimplemented method 'subUserPropagate'");
    }

    @Override
    public Result<Void> unsubUserPropagate(String user, String userSub) {
        throw new UnsupportedOperationException("Unimplemented method 'unsubUserPropagate'");
    }

    @Override
    public Result<Message> forwardGetMessage(String user, long msgId) {
        throw new UnsupportedOperationException("Unimplemented method 'forwardGetMessage'");
    }

    @Override
    public Result<List<Message>> forwardGetMessages(String user, long time) {
        throw new UnsupportedOperationException("Unimplemented method 'forwardGetMessages'");
    }
}
