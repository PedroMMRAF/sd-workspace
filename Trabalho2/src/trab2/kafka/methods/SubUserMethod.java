package trab2.kafka.methods;

import trab2.api.java.Feeds;
import trab2.api.java.Result;

public record SubUserMethod(String user, String userSub, String pwd) implements Method {
    @Override
    public Result<?> call(Feeds impl) {
        return impl.subUser(user, userSub, pwd);
    }
}
