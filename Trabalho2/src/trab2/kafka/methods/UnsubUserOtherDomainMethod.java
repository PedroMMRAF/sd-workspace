package trab2.kafka.methods;

import trab2.api.java.Feeds;
import trab2.api.java.Result;

public record UnsubUserOtherDomainMethod(String user, String userSub, String secret) implements Method {
    @Override
    public Result<?> call(Feeds impl) {
        return impl.unsubUserOtherDomain(user, userSub, secret);
    }
}
