package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import sun.dc.pr.PRError;

import java.util.Properties;

/**
 * 消息生产者
 */

public class MessageProducer {

    //实现消息传输的kafka内置类
    private Producer<String, String> producer;

    public MessageProducer(){
        Properties props = new Properties();
        props.put("bootstrap.servers", ProducerConfig.SERVER_ADDRESS);
        props.put("acks", ProducerConfig.ACK);
        props.put("retries", ProducerConfig.RETRIES);
        props.put("batch.size", ProducerConfig.BATCH_SIZE);
        props.put("linger.ms", ProducerConfig.LINGER_MS);
        props.put("partitioner.class", ProducerConfig.PARTITIONER);
        props.put("buffer.memory", ProducerConfig.BUFFER_MEMORY);
        props.put("key.serializer", ProducerConfig.KEY_SERIALIZER);
        props.put("value.serializer", ProducerConfig.VALUE_SERIALIZER);
        producer=new KafkaProducer<String, String>(props);
    }

    /*
     * 异步发送消息
     */
    public void sendMessageAsync(Message message){

    }

    /*
     * 同步发送消息,返回结果
     */
    public Response sendMessageSync(Message message){
        return new Response();
    }

}
