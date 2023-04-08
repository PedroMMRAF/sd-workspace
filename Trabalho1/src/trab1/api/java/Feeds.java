package trab1.api.java;

import java.util.List;

import trab1.api.Message;
import trab1.api.User;

public interface Feeds {
    Result<Long> postMessage(String user, String pwd, Message msg);

    Result<Long> postMessageOtherDomain(String user, Message msg);

    Result<Void> removeFromPersonalFeed(String user, long mid, String pwd);

    Result<Message> getMessage(String user, long mid);

    Result<List<Message>> getMessages(String user, long time);

    Result<Void> subUser(String user, String userSub, String pwd);

    Result<Void> subUserOtherDomain(String user, String userSub);

    Result<Void> unsubscribeUser(String user, String userSub, String pwd);

    Result<Void> unsubUserOtherDomain(String user, String userSub);

    Result<List<String>> listSubs(String user);

    // Auxiliary methods

    Result<User> getUser(String user, String pwd);

    boolean hasUser(String user);

    void postMessagePropagate(String user, Message msg);

    void subUserPropagate(String user, String userSub);

    void unsubUserPropagate(String user, String userSub);

    Result<Message> forwardGetMessage(String user, long msgId);

    Result<List<Message>> forwardGetMessages(String user, long time);
}
