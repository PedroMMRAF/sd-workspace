package trab2.kafka.methods;

import java.io.Serializable;

import trab2.api.java.Feeds;
import trab2.api.java.Result;

public interface Method extends Serializable {
    Result<?> call(Feeds impl);
}
