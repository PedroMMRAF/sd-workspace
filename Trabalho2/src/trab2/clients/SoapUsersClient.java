package trab2.clients;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import trab2.api.User;
import trab2.api.java.Result;
import trab2.api.java.Users;
import trab2.api.soap.UsersService;

public class SoapUsersClient extends SoapClient implements Users {
    private UsersService stub;

    public SoapUsersClient(URI serverURI) {
        super(serverURI);
    }

    private UsersService stub() {
        if (stub == null) {
            QName QNAME = new QName(UsersService.NAMESPACE, UsersService.NAME);
            Service service = Service.create(toURL(serverURI + WSDL), QNAME);
            stub = service.getPort(trab2.api.soap.UsersService.class);
            setTimeouts((BindingProvider) stub);
        }
        return stub;
    }

    @Override
    public Result<String> createUser(User user) {
        return retry(() -> toJavaResult(() -> stub().createUser(user)));
    }

    @Override
    public Result<User> getUser(String name, String pwd) {
        return retry(() -> toJavaResult(() -> stub().getUser(name, pwd)));
    }

    @Override
    public Result<User> updateUser(String name, String pwd, User user) {
        return retry(() -> toJavaResult(() -> stub().updateUser(name, pwd, user)));
    }

    @Override
    public Result<User> deleteUser(String name, String pwd) {
        return retry(() -> toJavaResult(() -> stub().deleteUser(name, pwd)));
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return retry(() -> toJavaResult(() -> stub().searchUsers(pattern)));
    }

    @Override
    public Result<Boolean> hasUser(String name) {
        return retry(() -> toJavaResult(() -> stub().hasUser(name)));
    }
}
