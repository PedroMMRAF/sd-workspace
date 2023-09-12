package trab2.api.java;

import java.util.List;

import trab2.api.User;

public interface Users {
    public static final String NAME = "users";

    Result<String> createUser(User user);

    Result<User> getUser(String name, String pwd);

    Result<User> updateUser(String name, String pwd, User user);

    Result<User> deleteUser(String name, String pwd);

    Result<List<User>> searchUsers(String pattern);

    Result<Boolean> hasUser(String name);
}
