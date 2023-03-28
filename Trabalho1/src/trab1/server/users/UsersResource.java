package trab1.server.users;

import trab1.User;
import trab1.rest.UsersService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class UsersResource implements UsersService {

    private static Logger Log = Logger.getLogger(UsersResource.class.getName());

    private final Map<String, User> users;

    public UsersResource() {
        users = new HashMap<>();
    }

    @Override
    public String createUser(User user) {
        Log.info("createUser : " + user);

        if (user == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        if (users.containsKey(user.getName())) {
            throw new WebApplicationException(Status.CONFLICT);
        }

        users.put(user.getIdentifier(), user);

        return user.getIdentifier();
    }

    @Override
    public User getUser(String userName, String password) {
        Log.info("getUser : user = " + userName + "; pwd = " + password);

        User user = users.get(userName);

        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        if (!user.getPwd().equals(password)) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        return user;
    }

    @Override
    public User updateUser(String userName, String password, User newUser) {
        Log.info("updateUser : user = " + userName + "; pwd = " + password + " ; user = " + newUser);

        if (newUser == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        User user = getUser(userName, password);

        user.setPwd(newUser.getPwd());
        user.setDisplayName(newUser.getDisplayName());

        return user;
    }

    @Override
    public User deleteUser(String userName, String password) {
        Log.info("deleteUser : user = " + userName + "; pwd = " + password);

        getUser(userName, password);

        return users.remove(userName);
    }

    @Override
    public List<User> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);

        if (pattern == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        List<User> matched = new ArrayList<>();

        for (User user : users.values()) {
            if (user.getDisplayName().toLowerCase().contains(pattern.toLowerCase())) {
                matched.add(new User(
                        user.getName(),
                        "",
                        user.getDomain(),
                        user.getDisplayName()));
            }
        }

        return matched;
    }

}
