package producer;

import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.record.InvalidRecordException;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;


public class Partitioner implements org.apache.kafka.clients.producer.Partitioner {
    public int partition(String topic, Object o, byte[] keyBytes, Object key, byte[] valueBytes, Cluster cluster) {
        //获取topic所有分区
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        int numPartitions = partitionInfos.size();
        //消息必须有key
        if (null == keyBytes || !(key instanceof String)) {
            throw new InvalidRecordException("kafka message must have key");
        }
        //如果只有一个分区，即0号分区
        if (numPartitions == 1) {return 0;}
        return Math.abs(Utils.murmur2(keyBytes)) % (numPartitions - 1);
    }

    public void close() {

    }

    public void configure(Map<String, ?> map) {

    }
}
