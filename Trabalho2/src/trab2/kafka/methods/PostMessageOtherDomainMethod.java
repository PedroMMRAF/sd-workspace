package trab2.kafka.methods;

import trab2.api.Message;
import trab2.api.java.Feeds;
import trab2.api.java.Result;

public record PostMessageOtherDomainMethod(String user, Message msg) implements Method {
    @Override
    public Result<?> call(Feeds impl) {
        return impl.postMessageOtherDomain(user, msg);
    }
}
