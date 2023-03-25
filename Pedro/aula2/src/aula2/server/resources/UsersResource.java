package aula2.server.resources;

import aula2.api.User;
import aula2.api.service.RestUsers;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class UsersResource implements RestUsers {

    private static Logger Log = Logger.getLogger(UsersResource.class.getName());
    private final Map<String, User> users = new HashMap<>();

    public UsersResource() {
    }

    @Override
    public String createUser(User user) {
        Log.info("createUser : " + user);

        // Check if user data is valid
        if (user.getUserId() == null || user.getPassword() == null || user.getFullName() == null || user.getEmail() == null) {
            Log.info("User object invalid.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        // Insert new user, checking if userId already exists
        if (users.putIfAbsent(user.getUserId(), user) != null) {
            Log.info("User already exists.");
            throw new WebApplicationException(Status.CONFLICT);
        }
        return user.getUserId();
    }


    @Override
    public User getUser(String userId, String password) {
        Log.info("getUser : user = " + userId + "; pwd = " + password);

        // Check if user is valid
        if (userId == null || password == null) {
            Log.info("UserId or password null.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        var user = users.get(userId);

        // Check if user exists
        if (user == null) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        //Check if the password is correct
        if (!user.getPassword().equals(password)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        return user;
    }


    @Override
    public User updateUser(String userId, String password, User user) {
        Log.info("updateUser : user = " + userId + "; pwd = " + password + " ; user = " + user);

        if (userId == null || password == null) {
            Log.info("UserId or password null.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        var oldUser = users.get(userId);

        if (oldUser == null) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        if (!oldUser.getPassword().equals(password)) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        if (oldUser.equals(user)) {
            Log.info("Invalid user data.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        users.put(userId, user);

        return user;
    }


    @Override
    public User deleteUser(String userId, String password) {
        Log.info("deleteUser : user = " + userId + "; pwd = " + password);

        if (userId == null || password == null) {
            Log.info("UserId or password null.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        var user = users.get(userId);

        if (user == null) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        if (!user.getPassword().equals(password)) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        users.remove(userId);

        return user;
    }


    @Override
    public List<User> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);

        if (pattern == null) {
            Log.info("Pattern is null.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        List<User> matched = new ArrayList<>();

        for (var user : users.values()) {
            if (user.getFullName().toLowerCase().contains(pattern.toLowerCase())) {
                matched.add(new User(
                        user.getUserId(),
                        user.getEmail(),
                        user.getFullName(),
                        ""
                ));
            }
        }

        return matched;
    }

}
