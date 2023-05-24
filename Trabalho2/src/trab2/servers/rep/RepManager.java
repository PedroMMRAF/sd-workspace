package trab2.servers.rep;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import trab2.kafka.KafkaPublisher;
import trab2.kafka.KafkaSubscriber;
import trab2.kafka.RecordProcessor;
import trab2.kafka.sync.SyncPoint;

public class RepManager {
    private static final String FROM_BEGINNING = "earliest";
    private static final String TOPIC = "gorila";
    private static final String KAFKA_BROKERS = "kafka:9092";

    private long nextVersion;
    private long currentVersion;
    private final KafkaPublisher sender;
    private final KafkaSubscriber receiver;
    private final SyncPoint<List<Object>> sync;

    public RepManager() {
        sender = new KafkaPublisher(KAFKA_BROKERS);
        receiver = new KafkaSubscriber(KAFKA_BROKERS, List.of(TOPIC), FROM_BEGINNING);
        sync = SyncPoint.getInstance();
        nextVersion = -1L;
        currentVersion = -1L;
    }

    public long getCurrentVersion() {
        return currentVersion;
    }

    public void register(RecordProcessor processor) {
        receiver.start(false, processor);
    }

    public void update(List<Object> function) {
        nextVersion = sender.publish(TOPIC, function);
    }

    public void waitForResult(Long version) {
        if (version == null)
            version = nextVersion;

        sync.waitForResult(version);
    }

    public void setVersion(ConsumerRecord<String, List<Object>> r) {
        sync.setResult(currentVersion = r.offset(), r.value());
    }
}
