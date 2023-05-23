package trab2.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaPublisher {
    private static final String BROKERS = "kafka:9092";

    private final KafkaProducer<String, String> producer;

    private KafkaPublisher(KafkaProducer<String, String> producer) {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    public void close() {
        this.producer.close();
    }

    public long publish(String topic, String key, String value) {
        try {
            long offset = producer.send(new ProducerRecord<>(topic, key, value)).get().offset();
            return offset;
        } catch (ExecutionException | InterruptedException x) {
            x.printStackTrace();
        }
        return -1;
    }

    public long publish(String topic, String value) {
        try {
            long offset = producer.send(new ProducerRecord<>(topic, value)).get().offset();
            return offset;
        } catch (ExecutionException | InterruptedException x) {
            x.printStackTrace();
        }
        return -1;
    }
}
