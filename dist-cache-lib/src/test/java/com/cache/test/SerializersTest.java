package com.cache.test;

import com.cache.api.CacheObject;
import com.cache.interfaces.DistSerializer;
import com.cache.serializers.*;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializersTest {
    private static final Logger log = LoggerFactory.getLogger(SerializersTest.class);

    @Test
    public void serializerDefinitionTest() {
        log.info("START ------ serialization test for different objects and serializers");
        ComplexSerializer complexSerializer = ComplexSerializer.createComplexSerializer("java.lang.String=StringSerializer,default=ObjectStreamSerializer");
        assertTrue(complexSerializer != null, "Serializer should not be NULL");

        assertEquals(2, complexSerializer.getSerializerKeys().size(), "There should be two serializer keys");
        assertTrue(complexSerializer.getSerializerClasses().contains("com.cache.serializers.StringSerializer"), "");

        log.info("Serializer keys: " + complexSerializer.getSerializerKeys());
        log.info("Serializer classes: " + complexSerializer.getSerializerClasses());
        log.info("------------------------------------------------------");
        log.info("END-----");
    }

    @Test
    public void serializerAllTest() {
        log.info("START ------ serialization test for different objects and serializers");
        Object[] testObjs = new Object[] {
                "abc",
                "aaaabbbbbbccccccccccccccccccccddddddddddddddddddeeeeeeeeeeeeeee",
                new BasicTestObject("ooooooooooooooooooooo", 1111),
                new AtomicLong(110001101),
                Map.of("kkkkkkkk", "vvvvvvvv"),
                new HashMap<String, String>(),
                List.of("elem1", "elem2", "elem3", "elem4", "elem5"),
                DistUtils.generateAgentGuid(),
                DistUtils.randomTable(50, 10000)
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
                log.info("------------------------------------------------------");
                log.info("OBJ[" + i + "].serializer=" + serializer.getClass().getSimpleName());
                log.info("OBJ[" + i + "].before=" + obj);
                try {
                    String objClassName = obj.getClass().getName();
                    log.info("OBJ[" + i + "].class=" + objClassName);
                    String serialized = serializer.serializeToString(obj);
                    log.info("OBJ[" + i + "].serialized=" + serialized);
                    Object objDes = serializer.deserializeFromString(objClassName, serialized);
                    log.info("OBJ[" + i + "].after=" + objDes);
                    log.info("OBJ[" + i + "].class=" + objDes.getClass().getName());
                    log.info("OBJ[" + i + "].equalsString=" + obj.toString().equals(objDes.toString()));
                    log.info("OBJ[" + i + "].equalsValue=" + obj.equals(objDes));
                    log.info("OBJ[" + i + "].equalsType=" + objClassName.equals(objDes.getClass().getName()));
                } catch (Exception ex) {
                    log.info("OBJ[" + i + "].exception=" + ex.getMessage());
                }
                log.info("------------------------------------------------------");
            }
        }

        DistSerializer ooSerializer = new ObjectStreamSerializer();
        for (int i=0; i<testObjs.length; i++) {
            Object obj = testObjs[i];
            CacheObject co = new CacheObject("key" + i, obj);
            String coStr = co.serializedFullCacheObjectToString(ooSerializer);
            Optional<CacheObject> coDeser = CacheObject.fromSerializedString(ooSerializer, coStr);
            log.info("Serialized" + co.getValue() + ", deserialized: " + coDeser.get().getValue());
            assertTrue(coDeser.isPresent(), "Deserialized object should be NON NULL");
            assertEquals(co.getValue().getClass().getName(), coDeser.get().getValue().getClass().getName(), "Deserialized object should have the same class");
        }

        log.info("------------------------------------------------------");
        log.info("END-----");
    }

    @Test
    public void serializerClassesTest() {
        log.info("START ------ serialization test for different objects and serializers");
        ComplexSerializer complexSerializer = ComplexSerializer.createComplexSerializer("java.lang.String=StringSerializer,default=ObjectStreamSerializer");
        log.info("------------------------------------------------------");
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