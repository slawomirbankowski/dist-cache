package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.CacheMode;
import com.cache.api.CacheObjectInfo;
import com.cache.api.DistMessage;
import com.cache.api.DistMessageStatus;
import com.cache.interfaces.Agent;
import com.cache.interfaces.Cache;
import com.cache.utils.CacheUtils;
import com.cache.utils.DistMessageProcessor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageProcessorTest {
    private static final Logger log = LoggerFactory.getLogger(MessageProcessorTest.class);

    @Test
    public void messageProcessorTest() {
        log.info("START ------ clean test");
        DistMessageProcessor processor = new DistMessageProcessor();
        assertNotNull(processor, "Processor should be not null");
        SampleTestService srv = new SampleTestService();
        assertNotNull(srv, "Service should be not null");
        processor.addMethods(srv);
        assertEquals(processor.getMethodsCount(), 4, "There should be 3 methods registered");
        processor.addMethod("sampleMethod", (mth, msg) -> {
            return msg.response("", DistMessageStatus.ok);
        });
        assertEquals(processor.getMethodsCount(), 5, "There should be 4 methods registered");
        DistMessage msg = DistMessage.createEmpty();
        processor.process("sampleMethod", msg);
        processor.process("sampleMethod", msg);
        processor.process("sampleMethod", msg);
        processor.process("sampleMethod", msg);
        processor.process("sampleMethod", msg);
        processor.process("sampleMethod", msg);
        assertEquals(processor.getReceivedMessagesCount(), 6, "There should be 6 received messages");

        log.info("END-----");
    }
}

class SampleTestService {

    public DistMessage methodOne(DistMessage msg) {
        return msg;
    }
    protected DistMessage methodTwo(DistMessage msg) {
        return msg;
    }
    private DistMessage methodThree(DistMessage msg) {
        return msg;
    }
    DistMessage methodFour(DistMessage msg) {
        return msg;
    }
}