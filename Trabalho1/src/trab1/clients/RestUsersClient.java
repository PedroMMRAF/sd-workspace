package trab1.clients;

import trab1.api.User;
import trab1.api.java.Result;
import trab1.api.java.Users;
import trab1.api.rest.UsersService;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

public class RestUsersClient extends RestClient implements Users {
    private final WebTarget target;

    public RestUsersClient(URI serverURI) {
        super();

        target = client.target(serverURI).path(UsersService.PATH);
    }

    private Result<String> clt_createUser(User user) {
        Response r = target.request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(user));

        return Result.fromResponse(r, String.class);
    }

    private Result<User> clt_getUser(String name, String pwd) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        return Result.fromResponse(r, User.class);
    }

    private Result<User> clt_updateUser(String name, String pwd, User user) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.json(user));

        return Result.fromResponse(r, User.class);
    }

    private Result<User> clt_deleteUser(String name, String pwd) {
        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        return Result.fromResponse(r, User.class);
    }

    private Result<List<User>> clt_searchUsers(String pattern) {
        Response r = target.queryParam(UsersService.QUERY, pattern)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        return Result.fromResponse(r, new GenericType<List<User>>() {
        });
    }

    private Result<Boolean> cls_hasUsers(String name) {
        Response r = target.path("exists").path(name)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        return Result.fromResponse(r, Boolean.class);
    }

    @Override
    public Result<String> createUser(User user) {
        return retry(() -> clt_createUser(user));
    }

    @Override
    public Result<User> getUser(String name, String pwd) {
        return retry(() -> clt_getUser(name, pwd));
    }

    @Override
    public Result<User> updateUser(String name, String pwd, User user) {
        return retry(() -> clt_updateUser(name, pwd, user));
    }

    @Override
    public Result<User> deleteUser(String name, String pwd) {
        return retry(() -> clt_deleteUser(name, pwd));
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return retry(() -> clt_searchUsers(pattern));
    }

    @Override
    public Result<Boolean> hasUser(String name) {
        return retry(() -> cls_hasUsers(name));
    }
}
