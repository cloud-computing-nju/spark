package producer;

/**
 * MessageProducer连接服务器的配置类
 */
public class ProducerConfig {
    protected static String SERVER_ADDRESS="192.168.1.124:9092"; //kafka服务器默认开放9092端口
    protected static String ACK="all";
    protected static int RETRIES=0;
    protected static int BATCH_SIZE=16384;
    protected static int LINGER_MS=1;
    protected static long BUFFER_MEMORY=33554432;
    protected static String PARTITIONER="com.example.kafka.consumer.MessageConsumer";
    protected static String KEY_SERIALIZER="org.apache.kafka.common.serialization.StringSerializer";
    protected static String VALUE_SERIALIZER="org.apache.kafka.common.serialization.StringSerializer";
}
