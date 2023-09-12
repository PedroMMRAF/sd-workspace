package trab2.servers.soap;

import java.util.List;
import java.util.logging.Logger;

import jakarta.jws.WebService;
import trab2.api.User;
import trab2.api.java.Users;
import trab2.api.soap.UsersException;
import trab2.api.soap.UsersService;
import trab2.servers.java.JavaUsers;

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

    @Override
    public boolean hasUser(String name) throws UsersException {
        return fromJavaResult(impl.hasUser(name));
    }
}
