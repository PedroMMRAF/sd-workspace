package aula5.servers.soap;

import java.util.List;
import java.util.logging.Logger;

import aula5.api.User;
import aula5.api.java.Users;
import aula5.api.soap.UsersException;
import aula5.api.soap.UsersService;
import aula5.servers.java.JavaUsers;
import jakarta.jws.WebService;

@WebService(serviceName = UsersService.NAME, targetNamespace = UsersService.NAMESPACE, endpointInterface = UsersService.INTERFACE)
public class SoapUsersWebService extends SoapWebService<UsersException> implements UsersService {
	static Logger Log = Logger.getLogger(SoapUsersWebService.class.getName());

	final Users impl;

	public SoapUsersWebService() {
		super((result) -> new UsersException(result.error().toString()));
		this.impl = new JavaUsers();
	}

	@Override
	public String createUser(User user) throws UsersException {
		return fromJavaResult(impl.createUser(user));
	}

	@Override
	public User getUser(String name, String pwd) throws UsersException {
		return fromJavaResult(impl.getUser(name, pwd));
	}

	@Override
	public void verifyPassword(String name, String pwd) throws UsersException {
		fromJavaResult(impl.verifyPassword(name, pwd));
	}

	@Override
	public User updateUser(String name, String pwd, User user) throws UsersException {
		return fromJavaResult(impl.updateUser(name, pwd, user));
	}

	@Override
	public User deleteUser(String name, String pwd) throws UsersException {
		return fromJavaResult(impl.deleteUser(name, pwd));
	}

	@Override
	public List<User> searchUsers(String pattern) throws UsersException {
		return fromJavaResult(impl.searchUsers(pattern));
	}

}
