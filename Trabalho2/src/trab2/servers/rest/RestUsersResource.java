package trab2.servers.rest;

import java.util.List;

import jakarta.inject.Singleton;
import trab2.api.User;
import trab2.api.rest.UsersService;
import trab2.servers.java.JavaUsers;

@Singleton
public class RestUsersResource extends RestResource implements UsersService {
    private final JavaUsers impl;

    public RestUsersResource() {
        impl = new JavaUsers();
    }

    @Override
    public String createUser(User user) {
        return fromJavaResult(impl.createUser(user));
    }

    @Override
    public User getUser(String name, String pwd) {
        return fromJavaResult(impl.getUser(name, pwd));
    }

    @Override
    public User updateUser(String name, String pwd, User newUser) {
        return fromJavaResult(impl.updateUser(name, pwd, newUser));
    }

    @Override
    public User deleteUser(String name, String pwd) {
        return fromJavaResult(impl.deleteUser(name, pwd));
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return fromJavaResult(impl.searchUsers(pattern));
    }

    @Override
    public boolean hasUser(String name) {
        return fromJavaResult(impl.hasUser(name));
    }
}
