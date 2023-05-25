package trab2.servers.rep;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import trab2.api.Message;
import trab2.api.java.Feeds;
import trab2.api.java.Result;
import trab2.kafka.RecordProcessor;
import trab2.kafka.api.FeedsService;
import trab2.kafka.methods.*;
import trab2.servers.java.JavaFeeds;
import trab2.servers.rest.RestResource;

public class RepFeedsResource extends RestResource implements FeedsService, RecordProcessor<Method> {
    private final Feeds impl;
    private final RepManager repManager;

    public RepFeedsResource(RepManager repManager) {
        impl = new JavaFeeds(0);
        this.repManager = repManager;
        repManager.register(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public long postMessage(Long version, String user, String pwd, Message msg) {
        return fromJavaResult((Result<Long>) repManager.update(new PostMessageMethod(user, pwd, msg)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public long postMessageOtherDomain(Long version, String user, Message msg) {
        return fromJavaResult((Result<Long>) repManager.update(new PostMessageOtherDomainMethod(user, msg)));
    }

    @Override
    public void removeFromPersonalFeed(Long version, String user, long mid, String pwd) {
        fromJavaResult(repManager.update(new RemoveFromPersonalFeedMethod(user, mid, pwd)));
    }

    @Override
    public Message getMessage(Long version, String user, long mid) {
        repManager.waitForResult(version);
        return fromJavaResult(impl.getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(Long version, String user, long time) {
        repManager.waitForResult(version);
        return fromJavaResult(impl.getMessages(user, time));
    }

    @Override
    public void subUser(Long version, String user, String userSub, String pwd) {
        fromJavaResult(repManager.update(new SubUserMethod(user, userSub, pwd)));
    }

    @Override
    public void subUserOtherDomain(Long version, String user, String userSub) {
        fromJavaResult(repManager.update(new SubUserOtherDomainMethod(user, userSub)));
    }

    @Override
    public void unsubscribeUser(Long version, String user, String userSub, String pwd) {
        fromJavaResult(repManager.update(new UnsubscribeUserMethod(user, userSub, pwd)));
    }

    @Override
    public void unsubUserOtherDomain(Long version, String user, String userSub) {
        fromJavaResult(repManager.update(new UnsubUserOtherDomainMethod(user, userSub)));
    }

    @Override
    public List<String> listSubs(Long version, String user) {
        repManager.waitForResult(version);
        return fromJavaResult(impl.listSubs(user));
    }

    @Override
    public void onReceive(ConsumerRecord<String, Method> r) {
        repManager.setVersion(r.offset(), r.value().call(impl));
    }
}
