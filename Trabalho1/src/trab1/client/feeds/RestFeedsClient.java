package trab1.client.feeds;

import trab1.Message;
import trab1.client.RestClient;
import trab1.rest.FeedsService;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.List;

public class RestFeedsClient extends RestClient implements FeedsService {
    private static final String SERVICE = "feeds";

    private final WebTarget target;

    public RestFeedsClient(String domain) {
        super(domain, SERVICE);
        target = client.target(serverURI).path(FeedsService.PATH);
    }

    public long clt_postMessage(String user, String pwd, Message msg) {
        Response r = target.path(user)
                .queryParam(FeedsService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(msg));

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(Long.class);

        throw new WebApplicationException(r.getStatus());
    }

    public int clt_removeFromPersonalFeed(String user, long mid, String pwd) {
        Response r = target.path(user).path(Long.toString(mid))
                .queryParam(FeedsService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return 0;

        throw new WebApplicationException(r.getStatus());
    }

    public Message clt_getMessage(String user, long mid) {
        Response r = target.path(user).path(Long.toString(mid))
                .request().accept(MediaType.APPLICATION_JSON).get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(Message.class);

        throw new WebApplicationException(r.getStatus());
    }

    public List<Message> clt_getMessages(String user, long time) {
        Response r = target.path(user)
                .queryParam(FeedsService.TIME, time).request()
                .accept(MediaType.APPLICATION_JSON).get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(new GenericType<>() {
            });

        throw new WebApplicationException(r.getStatus());
    }

    public int clt_subUser(String user, String userSub, String pwd) {
        Response r = target.path("sub").path(user).path(userSub)
                .queryParam(FeedsService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(null));

        if (r.getStatus() == Status.NO_CONTENT.getStatusCode())
            return 0;

        throw new WebApplicationException(r.getStatus());
    }

    public int clt_unsubscribeUser(String user, String userSub, String pwd) {
        Response r = target.path("sub").path(user).path(userSub)
                .queryParam(FeedsService.PWD, pwd)
                .request().accept(MediaType.APPLICATION_JSON).delete();

        if (r.getStatus() == Status.NO_CONTENT.getStatusCode())
            return 0;

        throw new WebApplicationException(r.getStatus());
    }

    public List<String> clt_listSubs(String user) {
        Response r = target.path("sub").path("list").path(user)
                .request().accept(MediaType.APPLICATION_JSON)
                .delete();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(new GenericType<>() {
            });

        throw new WebApplicationException(r.getStatus());
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return super.retry(() -> clt_postMessage(user, pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        super.retry(() -> clt_removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) {
        return super.retry(() -> clt_getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return super.retry(() -> clt_getMessages(user, time));
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        super.retry(() -> clt_subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        super.retry(() -> clt_unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public List<String> listSubs(String user) {
        return super.retry(() -> clt_listSubs(user));
    }

    @Override
    public long postMessageOtherDomain(String user, Message msg) {
        throw new UnsupportedOperationException("Unimplemented method 'postMessageOtherDomain'");
    }

    @Override
    public void subUserOtherDomain(String user, String userSub) {
        throw new UnsupportedOperationException("Unimplemented method 'subUserOtherDomain'");
    }

    @Override
    public void unsubUserOtherDomain(String user, String userSub) {
        throw new UnsupportedOperationException("Unimplemented method 'unsubUserOtherDomain'");
    }
}
