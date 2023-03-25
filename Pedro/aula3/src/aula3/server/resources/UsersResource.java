package aula3.server.resources;

import aula3.api.User;
import aula3.api.rest.UsersService;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class UsersResource implements UsersService {
    private final Map<String, User> users = new HashMap<>();

    private static Logger Log = Logger.getLogger(UsersResource.class.getName());

    public UsersResource() {
    }

    @Override
    public String createUser(User user) {
        Log.info("createUser : " + user);

        // Check if user data is valid
        if (user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
            Log.info("User object invalid.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        // Insert user, checking if name already exists
        if (users.putIfAbsent(user.getName(), user) != null) {
            Log.info("User already exists.");
            throw new WebApplicationException(Status.CONFLICT);
        }

        return user.getName();
    }

    @Override
    public User getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);

        // Check if user is valid
        if (name == null || pwd == null) {
            Log.info("Name or Password null.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        User user = users.get(name);

        // Check if user exists
        if (user == null) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        //Check if the password is correct
        if (!user.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        return user;
    }

    @Override
    public User updateUser(String name, String pwd, User newUser) {
        User user = getUser(name, pwd);

        if (newUser.getDisplayName() != null)
            user.setDisplayName(newUser.getDisplayName());

        if (newUser.getDomain() != null)
            user.setDomain(newUser.getDomain());

        if (newUser.getPwd() != null)
            user.setPwd(newUser.getPwd());

        return user;
    }

    @Override
    public User deleteUser(String name, String pwd) {
        getUser(name, pwd);

        return users.remove(name);
    }

    @Override
    public List<User> searchUsers(String pattern) {
        if (pattern == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        return users.values().stream().filter((user) -> matchesPattern(user, pattern))
                    .map(this::withPasswordRemoved).toList();
    }

    private boolean matchesPattern(User user, String pattern) {
        return user.getDisplayName().toLowerCase().contains(pattern.toLowerCase());
    }

    private User withPasswordRemoved(User user) {
        return new User(user.getName(), "", user.getDomain(), user.getDisplayName());
    }
}
