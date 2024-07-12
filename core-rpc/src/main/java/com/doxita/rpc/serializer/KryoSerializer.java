package com.doxita.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// kryo线程不安全
@Slf4j
public class KryoSerializer implements Serializer{
    public static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    
    
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        log.info("kryo serialize");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        KRYO_THREAD_LOCAL.get().writeObject(output, object);
        output.flush();
        output.close();
        return byteArrayOutputStream.toByteArray();
    }
    
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException, ClassNotFoundException {
        log.info("kryo deserialize");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T res = KRYO_THREAD_LOCAL.get().readObject(input, type);
        input.close();
        return res ;

    }
}
