package com.cache.dao;

import com.cache.api.DaoParams;
import com.cache.base.DaoBase;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentComponent;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/** base class for any JDBC based DAO */
public class DaoKafkaBase extends DaoBase implements AgentComponent {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(DaoKafkaBase.class);

    private final String brokers;
    private int numPartitions = 1;
    private short replicationFactor = 1;
    private String clientId = "";
    private String groupId = "";
    private AdminClient adminClient;

    /** all producers to write into Kafka topics */
    private KafkaProducer<String, String> producer;
    private Map<String, KafkaReceiver> consumersByTopic = new HashMap<>();

    /** creates new DAO to JDBC database */
    public DaoKafkaBase(DaoParams params, Agent agent) {
        super(params, agent);
        this.brokers = resolve(params.getBrokers());
        this.numPartitions = params.getNumPartitions();
        this.replicationFactor = params.getReplicationFactor();
        this.clientId = resolve(params.getClientId());
        this.groupId = resolve(params.getGroupId());
        onInitialize();
    }
    /** returns true if DAO is connected */
    public boolean isConnected() {
        return true;
    }
    public void onInitialize() {
        try {
            log.info("Connecting to Kafka, BROKERS=" + resolve(brokers));
            adminClient = createAdminClient();
            producer = createKafkaProducer();
            log.info("Connected to Kafka");
        } catch (Exception ex) {
            log.info("Cannot connect to Kafka at URL:" + brokers + ", reason: " + ex.getMessage(), ex);
            parentAgent.addIssue("DaoKafkaBase.onInitialize", ex);
        }
    }
    /** get all topics */
    public Collection<String> getDaoStructures() {
        return getTopics();
    }


    /** */
    private Properties commonKafkaProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        //props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "");
        //props.put(ConsumerConfig.GROUP_ID_CONFIG, "");
        return props;
    }
    private void producerKafkaProperties(Properties props) {
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "1");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class.getName());
    }
    private void consumerKafkaProperties(Properties props) {
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    }
    private AdminClient createAdminClient() {
        var props = commonKafkaProperties();
        AdminClient adminClient = AdminClient.create(props);
        return adminClient;
    }
    /** get all possible topics */
    public Set<String> getTopics() {
        try {
            return adminClient.listTopics().names().get();
        } catch (Exception ex) {
            parentAgent.addIssue("DaoKafkaBase.getTopics", ex);
            return Set.of();
        }
    }

    /** search of topics */
    public Set<String> searchTopics(String contains) {
        return getTopics().stream().filter(t -> t.contains(contains)).collect(Collectors.toSet());
    }
    /** create topics for given names */
    public boolean createTopic(String topicName) {
        return createTopics(Set.of(topicName));
    }
    /** filter topics in set and returns only non-existing topics, all existing topics are removed from the set */
    public Set<String> getNonExistingTopics(Set<String> topicNames) {
        var setCopy = new HashSet<String>(topicNames);
        setCopy.removeAll(getTopics());
        return setCopy;
    }
    /** create topics for given names */
    public boolean createTopics(Set<String> topicNames) {
        return createTopics(topicNames, numPartitions, replicationFactor);
    }
    /** create topics for given names */
    public boolean createTopics(Set<String> topicNames, int numParts, short replFactor) {
        try {
            Set<String> nonExistingTopics = getNonExistingTopics(topicNames);
            log.info("Try to create topics for names: " + nonExistingTopics);
            List<NewTopic> topicsToCreate = nonExistingTopics.stream().map(tn -> new NewTopic(tn, numParts, replFactor)).collect(Collectors.toList());
            CreateTopicsResult createResult = adminClient.createTopics(topicsToCreate);
            createResult.all().get(10, TimeUnit.SECONDS);
            return true;
        } catch (Exception ex) {
            log.warn("Cannot create topics, reason: " + ex.getMessage(), ex);
            parentAgent.addIssue("DaoKafkaBase.createTopics", ex);
            return false;
        }
    }
    /** create new Kafka producer to write messages into topics */
    private KafkaProducer<String, String> createKafkaProducer() {
        Properties props = commonKafkaProperties();
        producerKafkaProperties(props);
        return new KafkaProducer<String, String>(props);
    }

    /** get existing Kafka Consumer or create new one */
    public KafkaReceiver getOrCreateKafkaConsumer(String topicName,  Function<ConsumerRecord<String, String>, Boolean> onReadMessage) {
        return consumersByTopic.computeIfAbsent(topicName, x -> createKafkaConsumer(topicName, onReadMessage));
    }
    /** create new Kafka Consumer and replace current one */
    public KafkaReceiver createKafkaConsumer(String topicName,  Function<ConsumerRecord<String, String>, Boolean> onReadMessage) {
        Properties props = commonKafkaProperties();
        consumerKafkaProperties(props);
        var consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Set.of(topicName));
        KafkaReceiver receiver = new KafkaReceiver(this, topicName, consumer, onReadMessage);
        Thread thread = new Thread(receiver);
        receiver.setThread(thread);
        thread.setDaemon(true);
        thread.start();
        parentAgent.getAgentThreads().registerThread(this, thread, "kafka-receiver-" + topicName);
        KafkaReceiver oldReceiver = consumersByTopic.put(topicName, receiver);
        if (oldReceiver != null) {
            oldReceiver.close();
        }
        return receiver;
    }
    /** set one message using current Kafka producer */
    public Future<RecordMetadata> send(String topicName, String key, String value) {
        Future<RecordMetadata> sentInfo =  producer.send(new ProducerRecord<String, String>(topicName, key, value));
        producer.flush();
        return sentInfo;
    }
    /** close current Kafka producer, admin client, consumers */
    public boolean close() {
        adminClient.close();
        producer.close();
        consumersByTopic.values().stream().forEach(kc -> kc.close());
        return true;
    }

}

/** separated Thread to receive data */
class KafkaReceiver implements Runnable {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(KafkaReceiver.class);
    /** created date and time */
    private LocalDateTime createDate = LocalDateTime.now();
    private DaoKafkaBase dao;
    private String topicName;
    private KafkaConsumer<String, String> consumer;
    private Function<ConsumerRecord<String, String>, Boolean> onReadMessage;
    private Thread thread;
    private boolean working = true;
    private AtomicLong totalRead = new AtomicLong();

    private AtomicLong totalConfirmed = new AtomicLong();
    public KafkaReceiver(DaoKafkaBase dao, String topicName, KafkaConsumer<String, String> consumer, Function<ConsumerRecord<String, String>, Boolean> onReadMessage) {
        this.dao = dao;
        this.topicName = topicName;
        this.consumer = consumer;
        this.onReadMessage = onReadMessage;
    }
    public void setThread(Thread thread) {
        this.thread = thread;
    }
    public DaoKafkaBase getDao() {
        return dao;
    }
    public String getTopicName() {
        return topicName;
    }
    public KafkaConsumer<String, String> getConsumer() {
        return consumer;
    }
    public  Function<ConsumerRecord<String, String>, Boolean> getOnReadMessage() {
        return onReadMessage;
    }
    public Thread getThread() {
        return thread;
    }
    public boolean isWorking() {
        return working;
    }
    /** get date and time of creation */
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    /** get total read */
    public long getTotalRead() {
        return totalRead.get();
    }
    /** get total confirmed */
    public long getTotalConfirmed() {
        return totalConfirmed.get();
    }
    @Override
    public void run() {
        while (working) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
                records.iterator().forEachRemaining(rec -> {
                    totalRead.incrementAndGet();
                    var confirmed = onReadMessage.apply(rec);
                    if (confirmed) {
                        totalConfirmed.incrementAndGet();
                    }
                });
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                working = false;
            } catch (Exception ex) {
                dao.getParentAgent().addIssue("KafkaReceiver.run", ex);
            }
        }
    }
    public void close() {
        working = false;
        try {
            consumer.close();
        } catch (Exception ex) {
            dao.getParentAgent().addIssue("KafkaReceiver.close", ex);
        }
    }

}