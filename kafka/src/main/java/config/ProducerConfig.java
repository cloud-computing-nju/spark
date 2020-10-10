package config;

/**
 * MessageProducer连接服务器的配置类
 */
public class ProducerConfig {
    public static String SERVER_ADDRESS="127.0.0.1:9092"; //kafka服务器默认开放9092端口,集群地址多台服务器ip:port之间用分号隔开
    public static String ACK="all";
    public static int RETRIES=0;
    public static int BATCH_SIZE=16384;
    public static int LINGER_MS=1;
    public static long BUFFER_MEMORY=33554432;
    public static String PARTITIONER="com.example.kafka.producer.Partitioner";
    public static String KEY_SERIALIZER="org.apache.kafka.common.serialization.StringSerializer";
    public static String VALUE_SERIALIZER="org.apache.kafka.common.serialization.StringSerializer";

}
