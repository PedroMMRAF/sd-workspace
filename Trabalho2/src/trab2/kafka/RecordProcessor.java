package trab2.kafka;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface RecordProcessor {
    void onReceive(ConsumerRecord<String, List<Object>> r);
}
