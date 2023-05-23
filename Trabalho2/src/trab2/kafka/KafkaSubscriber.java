package trab2.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaSubscriber {
    private static final String MODE = "earliest";
    private static final String KAFKA_BROKERS = "kafka:9092";
    private static final long POLL_TIMEOUT = 1L;

    private final KafkaConsumer<String, String> consumer;

    public KafkaSubscriber(String topic) {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, MODE);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grp" + System.nanoTime());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        this.consumer = new KafkaConsumer<String, String>(props);
        this.consumer.subscribe(List.of(topic));
    }

    public void start(Function<ConsumerRecord<String, String>, Void> processor) {
        new Thread(() -> consume(processor)).start();
    }

    private void consume(Function<ConsumerRecord<String, String>, Void> processor) {
        while (true)
            consumer.poll(Duration.ofSeconds(POLL_TIMEOUT)).forEach(r -> {
                processor.apply(r);
            });
    }
}
