package trab2.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaSubscriber {
    private static final long POLL_TIMEOUT = 1L;

    private final KafkaConsumer<String, List<Object>> consumer;

    public KafkaSubscriber(String brokers, List<String> topics, String mode) {
        Properties props = new Properties();

        // Localização dos servidores kafka (lista de máquinas + porto)
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);

        // Configura o modo de subscrição (ver documentação em kafka.apache.org)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, mode);

        // Configura o grupo de consumidores (ver documentação em kafka.apache.org)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grp" + System.nanoTime());

        // Classe para serializar as chaves dos eventos (string)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Classe para serializar os valores dos eventos (string)
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Cria um consumidor (assinante/subscriber)
        this.consumer = new KafkaConsumer<String, List<Object>>(props);
        this.consumer.subscribe(topics);
    }

    public void start(boolean block, RecordProcessor processor) {
        if (block)
            consume(processor);
        else
            new Thread(() -> consume(processor)).start();
    }

    private void consume(RecordProcessor processor) {
        while (true)
            consumer.poll(Duration.ofSeconds(POLL_TIMEOUT)).forEach(r -> {
                processor.onReceive(r);
            });
    }
}
