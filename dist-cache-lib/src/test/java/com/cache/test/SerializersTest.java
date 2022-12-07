package com.cache.test;

import com.cache.interfaces.DistSerializer;
import com.cache.serializers.*;
import com.cache.utils.CacheUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SerializersTest {
    private static final Logger log = LoggerFactory.getLogger(SerializersTest.class);

    @Test
    public void serializerDefinitionTest() {
        log.info("START ------ serialization test for different objects and serializers");
        ComplexSerializer complexSerializer = ComplexSerializer.createComplexSerializer("java.lang.String=StringSerializer,default=ObjectStreamSerializer");
        System.out.println("Serializer keys: " + complexSerializer.getSerializerKeys());
        System.out.println("Serializer classes: " + complexSerializer.getSerializerClasses());
        System.out.println("------------------------------------------------------");
        log.info("END-----");
    }

    @Test
    public void serializerAllTest() {
        log.info("START ------ serialization test for different objects and serializers");
        Object[] testObjs = new Object[] {
                "abc",
                "aaaabbbbbbccccccccccccccccccccddddddddddddddddddeeeeeeeeeeeeeee",
                new SimpleObject("ooooooooooooooooooooo", 1111),
                new AtomicLong(110001101),
                Map.of("kkkkkkkk", "vvvvvvvv"),
                new HashMap<String, String>(),
                List.of("elem1", "elem2", "elem3", "elem4", "elem5"),
                CacheUtils.generateAgentGuid(),
                CacheUtils.randomTable(50, 10000)
        };
        DistSerializer[] serializers = new DistSerializer[] {
                new ObjectStreamSerializer(),
                new ObjectStreamCompressedSerializer(),
                new StringSerializer(),
                new Base64Serializer()
        };
        for (int s=0; s<serializers.length; s++) {
            for (int i=0; i<testObjs.length; i++) {
                Object obj = testObjs[i];
                DistSerializer serializer = serializers[s];
                System.out.println("------------------------------------------------------");
                System.out.println("OBJ[" + i + "].serializer=" + serializer.getClass().getSimpleName());
                System.out.println("OBJ[" + i + "].before=" + obj);
                try {
                    String objClassName = obj.getClass().getName();
                    System.out.println("OBJ[" + i + "].class=" + objClassName);
                    String serialized = serializer.serializeToString(obj);
                    System.out.println("OBJ[" + i + "].serialized=" + serialized);
                    Object objDes = serializer.deserializeFromString(objClassName, serialized);
                    System.out.println("OBJ[" + i + "].after=" + objDes);
                    System.out.println("OBJ[" + i + "].class=" + objDes.getClass().getName());
                    System.out.println("OBJ[" + i + "].equalsString=" + obj.toString().equals(objDes.toString()));
                    System.out.println("OBJ[" + i + "].equalsType=" + objClassName.equals(objDes.getClass().getName()));
                } catch (Exception ex) {
                    System.out.println("OBJ[" + i + "].exception=" + ex.getMessage());
                }
            }
        }
        System.out.println("------------------------------------------------------");
        log.info("END-----");
    }

    @Test
    public void serializerClassesTest() {
        log.info("START ------ serialization test for different objects and serializers");
        ComplexSerializer complexSerializer = ComplexSerializer.createComplexSerializer("java.lang.String=StringSerializer,default=ObjectStreamSerializer");
        System.out.println("------------------------------------------------------");
        log.info("END-----");
    }

}

class SimpleObject implements Serializable {
    public String name;
    public int number;
    public SimpleObject(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String toString() {
        return "name=" + name + ", number=" + number;
    }
}