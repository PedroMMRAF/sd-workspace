package trab2.kafka.methods;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.apache.kafka.common.serialization.Deserializer;

public class MethodDeserializer implements Deserializer<Method> {
    @Override
    public Method deserialize(String topic, byte[] data) {
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            Object obj = objectStream.readObject();
            objectStream.close();
            return (Method) obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
