package trab2.servers.rep;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import jakarta.inject.Singleton;
import trab2.api.Message;
import trab2.api.java.Feeds;
import trab2.kafka.methods.*;
import trab2.kafka.RecordProcessor;
import trab2.kafka.api.FeedsService;
import trab2.servers.Domain;
import trab2.servers.java.JavaFeeds;
import trab2.servers.rest.RestResource;

@Singleton
public class RepFeedsResource extends RestResource implements FeedsService, RecordProcessor<Method> {
    private Feeds impl;
    private RepManager repManager;
    private AtomicLong sequence;

    public RepFeedsResource() {
        impl = new JavaFeeds();
        sequence = new AtomicLong(Domain.sequence() * 0xFFFF);
        repManager = RepManager.getInstance();

        repManager.register(this);
    }

    @Override
    public long postMessage(Long version, String user, String pwd, Message msg) {
        msg.setId(sequence.getAndIncrement());
        return (long) fromJavaResult(repManager.update(new PostMessageMethod(user, pwd, msg)));
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
    public void unsubscribeUser(Long version, String user, String userSub, String pwd) {
        fromJavaResult(repManager.update(new UnsubscribeUserMethod(user, userSub, pwd)));
    }

    @Override
    public List<String> listSubs(Long version, String user) {
        repManager.waitForResult(version);
        return fromJavaResult(impl.listSubs(user));
    }

    // Internal methods

    @Override
    public long postMessageOtherDomain(Long version, String user, String secret, Message msg) {
        return (long) fromJavaResult(repManager.update(new PostMessageOtherDomainMethod(user, secret, msg)));
    }

    @Override
    public void subUserOtherDomain(Long version, String user, String userSub, String secret) {
        fromJavaResult(repManager.update(new SubUserOtherDomainMethod(user, userSub, secret)));
    }

    @Override
    public void unsubUserOtherDomain(Long version, String user, String userSub, String secret) {
        fromJavaResult(repManager.update(new UnsubUserOtherDomainMethod(user, userSub, secret)));
    }

    // RecordProcessor method

    @Override
    public void onReceive(ConsumerRecord<String, Method> r) {
        repManager.setResult(r.offset(), r.value().call(impl));
    }
}
