package trab1.client.users;

import trab1.User;
import trab1.client.RestClient;
import trab1.rest.UsersService;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.List;

public class RestUsersClient extends RestClient implements UsersService {
    private static final String SERVICE_FMT = "%s:users";

    private final WebTarget target;

    public RestUsersClient(String domain) {
        super(String.format(SERVICE_FMT, domain));
        this.target = client.target(serverURI).path(UsersService.PATH);
    }

    private String clt_createUser(User user) {
        Response r = target.request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(String.class);

        printErrorStatus(r.getStatus());
        return null;
    }

    private User clt_getUser(String name, String pwd) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(User.class);

        printErrorStatus(r.getStatus());
        return null;
    }

    private User clt_updateUser(String name, String pwd, User user) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(User.class);

        printErrorStatus(r.getStatus());
        return null;
    }

    private User clt_deleteUser(String name, String pwd) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(User.class);

        printErrorStatus(r.getStatus());
        return null;
    }

    private List<User> clt_searchUsers(String pattern) {
        Response r = target.queryParam(UsersService.QUERY, pattern)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(new GenericType<>() {
            });

        printErrorStatus(r.getStatus());
        return null;
    }

    // #region Ignore

    private void printErrorStatus(int code) {
        System.out.printf("Error, HTTP error status %s", Status.fromStatusCode(code));
    }

    @Override
    public String createUser(User user) {
        return super.retry(() -> clt_createUser(user));
    }

    @Override
    public User getUser(String name, String pwd) {
        return super.retry(() -> clt_getUser(name, pwd));
    }

    @Override
    public User updateUser(String name, String pwd, User user) {
        return super.retry(() -> clt_updateUser(name, pwd, user));
    }

    @Override
    public User deleteUser(String name, String pwd) {
        return super.retry(() -> clt_deleteUser(name, pwd));
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return super.retry(() -> clt_searchUsers(pattern));
    }

    // #endregion
}
