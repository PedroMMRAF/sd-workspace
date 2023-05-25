package trab2.kafka.methods;

import trab2.api.java.Feeds;
import trab2.api.java.Result;

public record RemoveFromPersonalFeedMethod(String user, long mid, String pwd) implements Method {
    @Override
    public Result<?> call(Feeds impl) {
        return impl.removeFromPersonalFeed(user, mid, pwd);
    }
}
