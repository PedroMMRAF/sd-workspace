package trab2.kafka;

import java.util.Properties;
// import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import trab2.kafka.methods.MethodSerializer;

public class KafkaPublisher<T> {
    private final KafkaProducer<String, T> producer;

    public KafkaPublisher(String brokers) {
        Properties props = new Properties();

        // Localização dos servidores kafka (lista de máquinas + porto)
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);

        // Classe para serializar as chaves dos eventos (string)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, MethodSerializer.class.getName());

        // Classe para serializar os valores dos eventos (string)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MethodSerializer.class.getName());

        this.producer = new KafkaProducer<String, T>(props);
    }

    public void close() {
        this.producer.close();
    }

    public long publish(String topic, T value) {
        try {
            return producer.send(new ProducerRecord<>(topic, value)).get().offset();
        } catch (Exception x) {
            x.printStackTrace();
        }
        return -1;
    }
}
