package producer;

import config.ProducerConfig;
import model.DataModel;
import org.apache.kafka.clients.producer.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 消息生产者
 */

public class MessageProducer {

    //实现消息传输的kafka内置类
    private Producer<String, DataModel> producer;

    private static MessageList messageList=MessageList.getInstance();

    public MessageProducer(){
        Properties props = new Properties();
        props.put("bootstrap.servers", config.ProducerConfig.SERVER_ADDRESS);
        props.put("acks", config.ProducerConfig.ACK);
        props.put("retries", config.ProducerConfig.RETRIES);
        props.put("batch.size", config.ProducerConfig.BATCH_SIZE);
        props.put("linger.ms", config.ProducerConfig.LINGER_MS);
        //props.put("partitioner.class", ProducerConfig.PARTITIONER);
        props.put("buffer.memory", config.ProducerConfig.BUFFER_MEMORY);
        props.put("key.serializer", config.ProducerConfig.KEY_SERIALIZER);
        props.put("value.serializer", ProducerConfig.VALUE_SERIALIZER);
        producer=new KafkaProducer<String, DataModel>(props);
    }

    /*
     * 异步发送消息
     */
    public void sendMessageAsync(Message message, Callback callback){
        String topic=message.getTopic();
        //key:=${topic}_i
        MessageProducer that=this;
        this.producer.send(new ProducerRecord<String, DataModel>(topic, topic.concat("_").concat(String.valueOf(MessageProducer.messageList.getMessageNum(message.getTopic()))), message.getValue()), new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                callback.onCompletion(recordMetadata,e);
                MessageProducer.messageList.addMessage(message.getTopic());
            }
        });
    }

    /*
     * 同步发送消息,返回结果
     */
    public Response sendMessageSync(Message message)  {
        String topic=message.getTopic();
        ProducerRecord<String, DataModel> record=new ProducerRecord<>(topic, topic.concat("_").concat(String.valueOf(MessageProducer.messageList.getMessageNum(message.getTopic()))), message.getValue());
        Response response=new Response();
        try {
            RecordMetadata result = producer.send(record).get();
            response.setSuccess(true);
            response.putResult("partition", String.valueOf(result.partition()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            response.setSuccess(false);
        }
        return response;
    }

}
