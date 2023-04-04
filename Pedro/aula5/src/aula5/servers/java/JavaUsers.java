package aula5.servers.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import aula5.api.User;
import aula5.api.java.Result;
import aula5.api.java.Result.ErrorCode;
import aula5.api.java.Users;

public class JavaUsers implements Users {
	private static Logger Log = Logger.getLogger(JavaUsers.class.getName());

	private final Map<String, User> users = new HashMap<>();

	@Override
	public Result<String> createUser(User user) {
		Log.info("createUser : " + user);

		if (user.getName() == null || user.getPwd() == null || user.getDisplayName() == null
				|| user.getDomain() == null) {
			Log.info("User object invalid.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		if (users.putIfAbsent(user.getName(), user) != null) {
			Log.info("User already exists.");
			return Result.error(ErrorCode.CONFLICT);
		}

		return Result.ok(user.getName());
	}

	@Override
	public Result<User> getUser(String name, String pwd) {
		Log.info("getUser : user = " + name + "; pwd = " + pwd);

		if (name == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		User user = users.get(name);

		if (user == null) {
			Log.info("User does not exist.");
			return Result.error(ErrorCode.NOT_FOUND);
		}

		if (!user.getPwd().equals(pwd)) {
			Log.info("Password is incorrect.");
			return Result.error(ErrorCode.FORBIDDEN);
		}

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String name, String pwd, User newUser) {
		Log.info("updateUser : name = " + name + ", pwd = " + pwd + ", user = " + newUser);

		if (name == null || pwd == null || newUser == null) {
			Log.info("Null field.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		Result<User> res = getUser(name, pwd);

		if (!res.isOK()) {
			return res;
		}

		if (newUser.getPwd() != null)
			res.value().setPwd(newUser.getPwd());

		if (newUser.getDisplayName() != null)
			res.value().setDisplayName(newUser.getDisplayName());

		return res;
	}

	@Override
	public Result<User> deleteUser(String name, String pwd) {
		Log.info("deleteUser : name = " + name + ", pwd = " + pwd);

		Result<User> res = getUser(name, pwd);

		if (!res.isOK()) {
			return res;
		}

		users.remove(name);

		return res;
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);

		if (pattern == null) {
			Log.info("Pattern is null.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		return Result.ok(
				users.values().stream()
						.filter(u -> u.getName().toLowerCase().contains(pattern.toLowerCase()))
						.map(u -> new User(u.getName(), "", u.getDomain(), u.getDisplayName()))
						.toList());
	}

	@Override
	public Result<Void> verifyPassword(String name, String pwd) {
		var res = getUser(name, pwd);
		if (res.isOK())
			return Result.ok();
		else
			return Result.error(res.error());
	}
}
