package trab2.servers.soap;

import java.util.List;
import java.util.logging.Logger;

import jakarta.jws.WebService;
import trab2.api.Message;
import trab2.api.java.Feeds;
import trab2.api.soap.FeedsException;
import trab2.api.soap.FeedsService;
import trab2.servers.java.JavaFeeds;

@WebService(serviceName = FeedsService.NAME, targetNamespace = FeedsService.NAMESPACE, endpointInterface = FeedsService.INTERFACE)
public class SoapFeedsWebService extends SoapWebService<FeedsException> implements FeedsService {
    static Logger Log = Logger.getLogger(SoapFeedsWebService.class.getName());

    final Feeds impl;

    public SoapFeedsWebService() {
        super((result) -> new FeedsException(result.error().toString()));
        this.impl = new JavaFeeds();
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) throws FeedsException {
        return fromJavaResult(impl.postMessage(user, pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) throws FeedsException {
        fromJavaResult(impl.removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) throws FeedsException {
        return fromJavaResult(impl.getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(String user, long time) throws FeedsException {
        return fromJavaResult(impl.getMessages(user, time));
    }

    @Override
    public void subUser(String user, String userSub, String pwd) throws FeedsException {
        fromJavaResult(impl.subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) throws FeedsException {
        fromJavaResult(impl.unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public List<String> listSubs(String user) throws FeedsException {
        return fromJavaResult(impl.listSubs(user));
    }

    // Internal methods

    @Override
    public long postMessageOtherDomain(String user, String secret, Message msg) throws FeedsException {
        return fromJavaResult(impl.postMessageOtherDomain(user, secret, msg));
    }

    @Override
    public void subUserOtherDomain(String user, String userSub, String secret) throws FeedsException {
        fromJavaResult(impl.subUserOtherDomain(user, userSub, secret));
    }

    @Override
    public void unsubUserOtherDomain(String user, String userSub, String secret) throws FeedsException {
        fromJavaResult(impl.unsubUserOtherDomain(user, userSub, secret));
    }
}
