package trab2.api.java;

import java.util.List;

import trab2.api.Message;
import trab2.api.User;

public interface Feeds {
    public static final String NAME = "feeds";

    Result<Long> postMessage(Long version, String user, String pwd, Message msg);

    Result<Long> postMessagePropagate(Long version, String user, Message msg);

    Result<Long> postMessageOtherDomain(Long version, String user, Message msg);

    Result<Void> removeFromPersonalFeed(Long version, String user, long mid, String pwd);

    Result<Message> getMessage(Long version, String user, long mid);

    Result<List<Message>> getMessages(Long version, String user, long time);

    Result<Void> subUser(Long version, String user, String userSub, String pwd);

    Result<Void> subUserPropagate(Long version, String user, String userSub);

    Result<Void> subUserOtherDomain(Long version, String user, String userSub);

    Result<Void> unsubscribeUser(Long version, String user, String userSub, String pwd);

    Result<Void> unsubUserPropagate(Long version, String user, String userSub);

    Result<Void> unsubUserOtherDomain(Long version, String user, String userSub);

    Result<List<String>> listSubs(Long version, String user);

    // Auxiliary methods

    Result<User> getUser(Long version, String user, String pwd);

    Result<Boolean> hasUser(Long version, String user);

    Result<Message> forwardGetMessage(Long version, String user, long msgId);

    Result<List<Message>> forwardGetMessages(Long version, String user, long time);
}
