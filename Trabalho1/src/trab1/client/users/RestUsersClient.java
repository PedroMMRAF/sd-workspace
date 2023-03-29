package trab1.client.users;

import trab1.User;
import trab1.client.RestClient;
import trab1.rest.UsersService;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.List;

public class RestUsersClient extends RestClient implements UsersService {
    private static final String SERVICE = "users";

    private final WebTarget target;

    public RestUsersClient(String domain) {
        super(domain, SERVICE);

        target = client.target(serverURI).path(UsersService.PATH);
    }

    private String clt_createUser(User user) {
        System.out.println(target);
        Response r = target.request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(user));

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(String.class);

        throw new WebApplicationException(r.getStatus());
    }

    private User clt_getUser(String name, String pwd) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(User.class);

        throw new WebApplicationException(r.getStatus());
    }

    private User clt_updateUser(String name, String pwd, User user) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.json(user));

        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(User.class);

        throw new WebApplicationException(r.getStatus());
    }

    private User clt_deleteUser(String name, String pwd) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(User.class);

        throw new WebApplicationException(r.getStatus());
    }

    private List<User> clt_searchUsers(String pattern) {
        Response r = target.queryParam(UsersService.QUERY, pattern)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(new GenericType<>() {
            });

        throw new WebApplicationException(r.getStatus());
    }

    @Override
    public String createUser(User user) {
        return retry(() -> clt_createUser(user));
    }

    @Override
    public User getUser(String name, String pwd) {
        return retry(() -> clt_getUser(name, pwd));
    }

    @Override
    public User updateUser(String name, String pwd, User user) {
        return retry(() -> clt_updateUser(name, pwd, user));
    }

    @Override
    public User deleteUser(String name, String pwd) {
        return retry(() -> clt_deleteUser(name, pwd));
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return retry(() -> clt_searchUsers(pattern));
    }
}
