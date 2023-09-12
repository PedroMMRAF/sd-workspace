package trab2.api.java;

import java.util.List;

import trab2.api.Message;
import trab2.api.User;

public interface Feeds {
    public static final String NAME = "feeds";

    Result<Long> postMessage(String user, String pwd, Message msg);

    Result<Void> removeFromPersonalFeed(String user, long mid, String pwd);

    Result<Message> getMessage(String user, long mid);

    Result<List<Message>> getMessages(String user, long time);

    Result<Void> subUser(String user, String userSub, String pwd);

    Result<Void> unsubscribeUser(String user, String userSub, String pwd);

    Result<List<String>> listSubs(String user);

    // Internal methods

    Result<Long> postMessageOtherDomain(String user, String secret, Message msg);

    Result<Void> subUserOtherDomain(String user, String userSub, String secret);

    Result<Void> unsubUserOtherDomain(String user, String userSub, String secret);

    // Propagate methods

    Result<Long> postMessagePropagate(String user, Message msg);

    Result<Void> subUserPropagate(String user, String userSub);

    Result<Void> unsubUserPropagate(String user, String userSub);

    // Auxiliary methods

    Result<User> getUser(String user, String pwd);

    Result<Boolean> hasUser(String user);

    Result<Message> forwardGetMessage(String user, long msgId);

    Result<List<Message>> forwardGetMessages(String user, long time);
}
