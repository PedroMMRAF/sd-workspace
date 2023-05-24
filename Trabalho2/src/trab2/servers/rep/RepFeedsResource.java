package trab2.servers.rep;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import trab2.api.Message;
import trab2.api.java.Feeds;
import trab2.kafka.RecordProcessor;
import trab2.kafka.api.FeedsService;
import trab2.servers.java.JavaFeeds;
import trab2.servers.rest.RestResource;

public class RepFeedsResource extends RestResource implements FeedsService, RecordProcessor {
    private final Feeds impl;
    private final RepManager repManager;

    public RepFeedsResource(int initialSequence, RepManager repManager) {
        impl = new JavaFeeds(initialSequence);
        this.repManager = repManager;
        repManager.register(this);
    }

    @Override
    public long postMessage(Long version, String user, String pwd, Message msg) {
        repManager.update(List.of("postMessage", user, pwd, msg));
        repManager.waitForResult(version);
        return fromJavaResult(impl.postMessage(user, pwd, msg));
    }

    @Override
    public long postMessageOtherDomain(Long version, String user, Message msg) {
        repManager.update(List.of("postMessageOtherDomain", user, msg));
        repManager.waitForResult(version);
        return fromJavaResult(impl.postMessageOtherDomain(user, msg));
    }

    @Override
    public void removeFromPersonalFeed(Long version, String user, long mid, String pwd) {
        repManager.update(List.of("removeFromPersonalFeed", user, mid, pwd));
        repManager.waitForResult(version);
        fromJavaResult(impl.removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(Long version, String user, long mid) {
        repManager.update(List.of("getMessage", user, mid));
        repManager.waitForResult(version);
        return fromJavaResult(impl.getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(Long version, String user, long time) {
        repManager.update(List.of("getMessages", user, time));
        repManager.waitForResult(version);
        return fromJavaResult(impl.getMessages(user, time));
    }

    @Override
    public void subUser(Long version, String user, String userSub, String pwd) {
        repManager.update(List.of("subUser", user, userSub, pwd));
        repManager.waitForResult(version);
        fromJavaResult(impl.subUser(user, userSub, pwd));
    }

    @Override
    public void subUserOtherDomain(Long version, String user, String userSub) {
        repManager.update(List.of("subUserOtherDomain", user, userSub));
        repManager.waitForResult(version);
        fromJavaResult(impl.subUserOtherDomain(user, userSub));
    }

    @Override
    public void unsubscribeUser(Long version, String user, String userSub, String pwd) {
        repManager.update(List.of("unsubscribeUser", user, userSub, pwd));
        repManager.waitForResult(version);
        fromJavaResult(impl.unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public void unsubUserOtherDomain(Long version, String user, String userSub) {
        repManager.update(List.of("unsubUserOtherDomain", user, userSub));
        repManager.waitForResult(version);
        fromJavaResult(impl.unsubUserOtherDomain(user, userSub));
    }

    @Override
    public List<String> listSubs(Long version, String user) {
        repManager.update(List.of("listSubs", user));
        repManager.waitForResult(version);
        return fromJavaResult(impl.listSubs(user));
    }

    @Override
    public void onReceive(ConsumerRecord<String, List<Object>> r) {
        List<Object> function = r.value();

        switch ((String) function.get(0)) {
            case "postMessage" ->
                impl.postMessage((String) function.get(1), (String) function.get(2), (Message) function.get(3));
            case "postMessageOtherDomain" ->
                impl.postMessageOtherDomain((String) function.get(1), (Message) function.get(2));
            case "removeFromPersonalFeed" ->
                impl.removeFromPersonalFeed((String) function.get(1), (Long) function.get(2), (String) function.get(3));
            case "subUser" ->
                impl.subUser((String) function.get(1), (String) function.get(2), (String) function.get(3));
            case "subUserOtherDomain" ->
                impl.subUserOtherDomain((String) function.get(1), (String) function.get(2));
            case "unsubscribeUser" ->
                impl.unsubscribeUser((String) function.get(1), (String) function.get(2), (String) function.get(3));
            case "unsubUserOtherDomain" ->
                impl.unsubUserOtherDomain((String) function.get(1), (String) function.get(2));
        }

        repManager.setVersion(r);
    }
}
