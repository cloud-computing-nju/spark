package config;

public class ConsumerConfig {
    public static String SERVER_ADDRESS="127.0.0.1:9092"; //kafka服务器默认开放9092端口,集群地址多台服务器ip:port之间用分号隔开
    public static int SPARK_WC_PORT=9093;
    public static String GROUP_ID="default_group";
    public static boolean ENABLE_AUTO_COMMIT=true;
    public static int AUTO_COMMIT_INTERVAL=1000; //ms
    public static String KEY_DESERIALIZER="org.apache.kafka.common.serialization.StringDeserializer";
    public static String VALUE_DESERIALIZER="adapter.DataModelDecoder";
    public static int MIN_BATCH_SIZE=0;//cache buffer size
}
