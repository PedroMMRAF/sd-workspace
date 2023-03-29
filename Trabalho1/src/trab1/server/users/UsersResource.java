package trab1.server.users;

import trab1.User;
import trab1.rest.UsersService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class UsersResource implements UsersService {
    private static Logger Log = Logger.getLogger(UsersResource.class.getName());

    private final Map<String, User> users;

    public UsersResource() {
        users = new ConcurrentHashMap<>();
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

        users.put(user.getName(), user);

        return user.getIdentifier();
    }

    @Override
    public User getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);

        User user = users.get(name);

        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        if (!user.getPwd().equals(pwd)) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        return user;
    }

    @Override
    public User updateUser(String name, String pwd, User user) {
        Log.info("updateUser : user = " + name + "; pwd = " + pwd + " ; user = " + user);

        if (user == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        User oldUser = getUser(name, pwd);

        oldUser.setPwd(user.getPwd());
        oldUser.setDisplayName(user.getDisplayName());

        return user;
    }

    @Override
    public User deleteUser(String name, String pwd) {
        Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

        getUser(name, pwd);

        return users.remove(name);
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
