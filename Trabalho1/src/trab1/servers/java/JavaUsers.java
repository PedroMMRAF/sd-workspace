package trab1.servers.java;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import trab1.api.User;
import trab1.api.java.Result;
import trab1.api.java.Users;
import trab1.servers.Domain;

public class JavaUsers implements Users {
    private static final Logger Log = Logger.getLogger(JavaUsers.class.getName());

    private final Map<String, User> users;

    public JavaUsers() {
        users = new ConcurrentHashMap<>();
    }

    private static void logInfo(String name, Object... pairs) {
        StringBuilder result = new StringBuilder("Users : ");

        result.append(name);
        result.append(" : ");

        for (int i = 0; i < pairs.length; i += 2) {
            result.append(pairs[i]);
            result.append(" = ");
            result.append(pairs[i + 1]);

            if (i < pairs.length - 2)
                result.append(", ");
        }

        Log.info(result.toString());
    }

    @Override
    public Result<String> createUser(User user) {
        logInfo("createUser", "user", user);

        if (user == null
                || user.getName() == null
                || user.getPwd() == null
                || user.getDisplayName() == null
                || user.getDomain() == null
                || !user.getDomain().equals(Domain.get()))
            return Result.error(Result.ErrorCode.BAD_REQUEST);

        if (users.containsKey(user.getName()))
            return Result.error(Result.ErrorCode.CONFLICT);

        users.put(user.getName(), user);

        return Result.ok(user.identifier());
    }

    @Override
    public Result<User> getUser(String name, String pwd) {
        logInfo("getUser", "name", name, "pwd", pwd);

        if (name == null || pwd == null)
            return Result.error(Result.ErrorCode.BAD_REQUEST);

        User user = users.get(name);

        if (user == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);

        if (!user.getPwd().equals(pwd))
            return Result.error(Result.ErrorCode.FORBIDDEN);

        return Result.ok(user);
    }

    @Override
    public Result<User> updateUser(String name, String pwd, User newUser) {
        logInfo("updateUser", "name", name, "pwd", pwd, "newUser", newUser);

        if (newUser == null)
            return Result.error(Result.ErrorCode.BAD_REQUEST);

        Result<User> res = getUser(name, pwd);

        if (!res.isOK())
            return res;

        User user = res.value();

        if ((newUser.getName() != null && !newUser.getName().equals(user.getName()))
                || (newUser.getDomain() != null && !newUser.getDomain().equals(Domain.get())))
            return Result.error(Result.ErrorCode.BAD_REQUEST);

        if (newUser.getPwd() != null)
            user.setPwd(newUser.getPwd());

        if (newUser.getDisplayName() != null)
            user.setDisplayName(newUser.getDisplayName());

        return res;
    }

    @Override
    public Result<User> deleteUser(String name, String pwd) {
        logInfo("deleteUser", "name", name, "pwd", pwd);

        Result<User> res = getUser(name, pwd);

        if (!res.isOK())
            return res;

        users.remove(name);

        return res;
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        logInfo("searchUsers", "pattern", pattern);

        if (pattern == null)
            return Result.error(Result.ErrorCode.BAD_REQUEST);

        return Result.ok(users.values().stream()
                .filter(u -> u.matchesPattern(pattern))
                .map(u -> u.withoutPassword())
                .toList());
    }

    @Override
    public Result<Boolean> hasUser(String name) {
        logInfo("hasUser", "name", name);

        if (name == null)
            return Result.error(Result.ErrorCode.BAD_REQUEST);

        return Result.ok(users.containsKey(name));
    }
}
