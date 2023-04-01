package trab1.server.users;

import trab1.Domain;
import trab1.User;
import trab1.rest.UsersService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class UsersResource implements UsersService {
    private static final Logger Log = Logger.getLogger(UsersResource.class.getName());

    private final Map<String, User> users;

    public UsersResource() {
        users = new ConcurrentHashMap<>();
    }

    @Override
    public String createUser(User user) {
        Log.info("createUser : " + user);

        if (user == null
                || user.getName() == null
                || user.getPwd() == null
                || user.getDisplayName() == null
                || user.getDomain() == null
                || !user.getDomain().equals(Domain.get()))
            throw new WebApplicationException(Status.BAD_REQUEST);

        if (users.containsKey(user.getName()))
            throw new WebApplicationException(Status.CONFLICT);

        users.put(user.getName(), user);

        return user.identifier();
    }

    @Override
    public User getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);

        if (name == null || pwd == null)
            throw new WebApplicationException(Status.BAD_REQUEST);

        User user = users.get(name);

        if (user == null)
            throw new WebApplicationException(Status.NOT_FOUND);

        if (!user.getPwd().equals(pwd))
            throw new WebApplicationException(Status.FORBIDDEN);

        return user;
    }

    @Override
    public User updateUser(String name, String pwd, User newUser) {
        Log.info("updateUser : user = " + name + "; pwd = " + pwd + " ; user = " + newUser);

        User user = getUser(name, pwd);

        if (newUser == null)
            throw new WebApplicationException(Status.BAD_REQUEST);

        if (newUser.getPwd() != null)
            user.setPwd(newUser.getPwd());

        if (newUser.getDisplayName() != null)
            user.setDisplayName(newUser.getDisplayName());

        return user;
    }

    @Override
    public User deleteUser(String name, String pwd) {
        Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

        getUser(name, pwd);

        return users.remove(name);
    }

    private boolean userMatchesPattern(User user, String pattern) {
        return user.getName().toLowerCase().contains(pattern.toLowerCase());
    }

    private User userWithoutPassword(User user) {
        return new User(
                user.getName(),
                "",
                user.getDomain(),
                user.getDisplayName());
    }

    @Override
    public List<User> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);

        if (pattern == null)
            throw new WebApplicationException(Status.BAD_REQUEST);

        return users.values().stream()
                .filter(u -> userMatchesPattern(u, pattern))
                .map(this::userWithoutPassword)
                .toList();
    }
}
