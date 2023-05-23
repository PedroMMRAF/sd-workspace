package trab2.servers.rep;

import java.util.List;

import jakarta.inject.Singleton;
import trab2.api.Message;
import trab2.api.java.Feeds;
import trab2.api.rest.FeedsService;
import trab2.servers.java.JavaFeeds;
import trab2.servers.rest.RestResource;

@Singleton
public class RepFeedsResource extends RestResource implements FeedsService {
    private final Feeds impl;

    public RepFeedsResource(int seq) {
        impl = new JavaFeeds(seq);
    }

    @Override
    public long postMessage(Long version, String user, String pwd, Message msg) {
        return fromJavaResult(impl.postMessage(version, user, pwd, msg));
    }

    @Override
    public long postMessageOtherDomain(Long version, String user, Message msg) {
        return fromJavaResult(impl.postMessageOtherDomain(version, user, msg));
    }

    @Override
    public void removeFromPersonalFeed(Long version, String user, long mid, String pwd) {
        fromJavaResult(impl.removeFromPersonalFeed(version, user, mid, pwd));
    }

    @Override
    public Message getMessage(Long version, String user, long mid) {
        return fromJavaResult(impl.getMessage(version, user, mid));
    }

    @Override
    public List<Message> getMessages(Long version, String user, long time) {
        return fromJavaResult(impl.getMessages(version, user, time));
    }

    @Override
    public void subUser(Long version, String user, String userSub, String pwd) {
        fromJavaResult(impl.subUser(version, user, userSub, pwd));
    }

    @Override
    public void subUserOtherDomain(Long version, String user, String userSub) {
        fromJavaResult(impl.subUserOtherDomain(version, user, userSub));
    }

    @Override
    public void unsubscribeUser(Long version, String user, String userSub, String pwd) {
        fromJavaResult(impl.unsubscribeUser(version, user, userSub, pwd));
    }

    @Override
    public void unsubUserOtherDomain(Long version, String user, String userSub) {
        fromJavaResult(impl.unsubUserOtherDomain(version, user, userSub));
    }

    @Override
    public List<String> listSubs(Long version, String user) {
        return fromJavaResult(impl.listSubs(version, user));
    }
}
