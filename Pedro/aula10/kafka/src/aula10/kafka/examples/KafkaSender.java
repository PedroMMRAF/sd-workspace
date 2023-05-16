package aula10.kafka.examples;

import aula10.kafka.KafkaPublisher;

public class KafkaSender {
	static final String TOPIC = "topic";
	static final String KAFKA_BROKERS = "kafka:9092";

	final KafkaPublisher publisher;

	public KafkaSender() {
		publisher = KafkaPublisher.createPublisher(KAFKA_BROKERS);
	}

	public long send(String msg) {
		long offset = publisher.publish(TOPIC, msg + System.currentTimeMillis());
		if (offset >= 0)
			System.out.println("Message published with sequence number: " + offset);
		else
			System.err.println("Failed to publish message");
		return offset;

	}

	public static void main(String[] args) throws Exception {
		var msg = args.length > 0 ? args[0] : "";

		var sender = new KafkaSender();

		while (true) {
			sender.send(msg + System.currentTimeMillis());
			Thread.sleep(1000);
		}
	}

}
