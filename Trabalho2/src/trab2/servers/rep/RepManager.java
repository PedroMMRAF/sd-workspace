package trab2.servers.rep;

import java.util.List;

import trab2.api.java.Result;
import trab2.kafka.KafkaPublisher;
import trab2.kafka.KafkaSubscriber;
import trab2.kafka.RecordProcessor;
import trab2.kafka.methods.Method;
import trab2.kafka.sync.SyncPoint;

public class RepManager {
    private static final String FROM_BEGINNING = "earliest";
    private static final String TOPIC = "gorila";
    private static final String KAFKA_BROKERS = "kafka:9092";

    private long currentVersion;
    private final KafkaPublisher<Method> sender;
    private final KafkaSubscriber<Method> receiver;
    private final SyncPoint<Result<?>> sync;

    public RepManager() {
        sender = new KafkaPublisher<>(KAFKA_BROKERS);
        receiver = new KafkaSubscriber<>(KAFKA_BROKERS, List.of(TOPIC), FROM_BEGINNING);
        sync = SyncPoint.getInstance();
        currentVersion = -1L;
    }

    public long getCurrentVersion() {
        return currentVersion;
    }

    public void register(RecordProcessor<Method> processor) {
        receiver.start(false, processor);
    }

    public Result<?> update(Method function) {
        return waitForResult(sender.publish(TOPIC, function));
    }

    public Result<?> waitForResult(long version) {
        return sync.waitForResult(version);
    }

    public void setVersion(long version, Result<?> result) {
        sync.setResult(currentVersion = version, result);
    }
}
