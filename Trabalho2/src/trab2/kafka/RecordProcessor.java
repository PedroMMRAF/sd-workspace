package trab2.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface RecordProcessor<T> {
    void onReceive(ConsumerRecord<String, T> r);
}
