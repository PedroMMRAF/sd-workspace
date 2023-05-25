package trab2.kafka.methods;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.apache.kafka.common.serialization.Serializer;

public class MethodSerializer implements Serializer<Method> {
    @Override
    public byte[] serialize(String topic, Method data) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(data);
            objectStream.close();
            return byteStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
