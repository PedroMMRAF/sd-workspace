package trab2.servers.proxy;

import java.util.List;

import jakarta.inject.Singleton;
import trab2.api.Message;
import trab2.api.java.Feeds;
import trab2.api.rest.FeedsService;
import trab2.mastodon.Mastodon;
import trab2.servers.rest.RestResource;

@Singleton
public class ProxyFeedsResource extends RestResource implements FeedsService {
    private final Feeds impl;

    public ProxyFeedsResource() {
        impl = Mastodon.getInstance();
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return fromJavaResult(impl.postMessage(user, pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        fromJavaResult(impl.removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) {
        return fromJavaResult(impl.getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return fromJavaResult(impl.getMessages(user, time));
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        fromJavaResult(impl.subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        fromJavaResult(impl.unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public List<String> listSubs(String user) {
        return fromJavaResult(impl.listSubs(user));
    }

    // Internal methods (not used on proxy)

    @Override
    public long postMessageOtherDomain(String user, String secret, Message msg) {
        return fromJavaResult(impl.postMessageOtherDomain(user, secret, msg));
    }

    @Override
    public void subUserOtherDomain(String user, String userSub, String secret) {
        fromJavaResult(impl.subUserOtherDomain(user, userSub, secret));
    }

    @Override
    public void unsubUserOtherDomain(String user, String userSub, String secret) {
        fromJavaResult(impl.unsubUserOtherDomain(user, userSub, secret));
    }
}
