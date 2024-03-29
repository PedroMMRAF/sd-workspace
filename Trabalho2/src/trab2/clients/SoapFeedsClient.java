package trab2.clients;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import trab2.api.Message;
import trab2.api.User;
import trab2.api.java.Feeds;
import trab2.api.java.Result;
import trab2.api.soap.FeedsService;

public class SoapFeedsClient extends SoapClient implements Feeds {
    private FeedsService stub;

    public SoapFeedsClient(URI serverURI) {
        super(serverURI);
    }

    private FeedsService stub() {
        if (stub == null) {
            QName QNAME = new QName(FeedsService.NAMESPACE, FeedsService.NAME);
            Service service = Service.create(toURL(serverURI + WSDL), QNAME);
            stub = service.getPort(trab2.api.soap.FeedsService.class);
            setTimeouts((BindingProvider) stub);
        }
        return stub;
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        return retry(() -> toJavaResult(() -> stub().postMessage(user, pwd, msg)));
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        return retry(() -> toJavaResult(() -> stub().removeFromPersonalFeed(user, mid, pwd)));
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        return retry(() -> toJavaResult(() -> stub().getMessage(user, mid)));
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        return retry(() -> toJavaResult(() -> stub().getMessages(user, time)));
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        return retry(() -> toJavaResult(() -> stub().subUser(user, userSub, pwd)));
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        return retry(() -> toJavaResult(() -> stub().unsubscribeUser(user, userSub, pwd)));
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        return retry(() -> toJavaResult(() -> stub().listSubs(user)));
    }

    // Internal methods

    @Override
    public Result<Long> postMessageOtherDomain(String user, String secret, Message msg) {
        return retry(() -> toJavaResult(() -> stub().postMessageOtherDomain(user, secret, msg)));
    }

    @Override
    public Result<Void> subUserOtherDomain(String user, String userSub, String secret) {
        return retry(() -> toJavaResult(() -> stub().subUserOtherDomain(user, userSub, secret)));
    }

    @Override
    public Result<Void> unsubUserOtherDomain(String user, String userSub, String secret) {
        return retry(() -> toJavaResult(() -> stub().unsubUserOtherDomain(user, userSub, secret)));
    }

    // Unimplemented on client

    @Override
    public Result<User> getUser(String user, String pwd) {
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public Result<Boolean> hasUser(String user) {
        throw new UnsupportedOperationException("Unimplemented method 'hasUser'");
    }

    @Override
    public Result<Long> postMessagePropagate(String user, Message msg) {
        throw new UnsupportedOperationException("Unimplemented method 'postMessagePropagate'");
    }

    @Override
    public Result<Void> subUserPropagate(String user, String userSub) {
        throw new UnsupportedOperationException("Unimplemented method 'subUserPropagate'");
    }

    @Override
    public Result<Void> unsubUserPropagate(String user, String userSub) {
        throw new UnsupportedOperationException("Unimplemented method 'unsubUserPropagate'");
    }

    @Override
    public Result<Message> forwardGetMessage(String user, long msgId) {
        throw new UnsupportedOperationException("Unimplemented method 'forwardGetMessage'");
    }

    @Override
    public Result<List<Message>> forwardGetMessages(String user, long time) {
        throw new UnsupportedOperationException("Unimplemented method 'forwardGetMessages'");
    }
}
