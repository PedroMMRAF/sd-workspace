package trab1.servers.rest;

import java.util.List;

import trab1.api.Message;
import trab1.api.rest.FeedsService;
import trab1.servers.java.JavaFeeds;
import jakarta.inject.Singleton;

@Singleton
public class RestFeedsResource extends RestResource implements FeedsService {
    private final JavaFeeds impl;

    public RestFeedsResource() {
        impl = new JavaFeeds();
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return fromJavaResult(impl.postMessage(user, pwd, msg));
    }

    @Override
    public long postMessageOtherDomain(String user, Message msg) {
        return fromJavaResult(impl.postMessageOtherDomain(user, msg));
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
    public void subUserOtherDomain(String user, String userSub) {
        fromJavaResult(impl.subUserOtherDomain(user, userSub));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        fromJavaResult(impl.unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public void unsubUserOtherDomain(String user, String userSub) {
        fromJavaResult(impl.unsubUserOtherDomain(user, userSub));
    }

    @Override
    public List<String> listSubs(String user) {
        return fromJavaResult(impl.listSubs(user));
    }
}
