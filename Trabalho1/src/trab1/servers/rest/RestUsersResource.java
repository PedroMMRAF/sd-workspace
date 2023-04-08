package trab1.servers.rest;

import trab1.api.User;
import trab1.api.rest.UsersService;
import trab1.servers.java.JavaUsers;

import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public class RestUsersResource extends RestResource implements UsersService {
    private final JavaUsers impl;

    public RestUsersResource() {
        impl = new JavaUsers();
    }

    @Override
    synchronized public String createUser(User user) {
        return fromJavaResult(impl.createUser(user));
    }

    @Override
    synchronized public User getUser(String name, String pwd) {
        return fromJavaResult(impl.getUser(name, pwd));
    }

    @Override
    synchronized public User updateUser(String name, String pwd, User newUser) {
        return fromJavaResult(impl.updateUser(name, pwd, newUser));
    }

    @Override
    synchronized public User deleteUser(String name, String pwd) {
        return fromJavaResult(impl.deleteUser(name, pwd));
    }

    @Override
    synchronized public List<User> searchUsers(String pattern) {
        return fromJavaResult(impl.searchUsers(pattern));
    }
}
