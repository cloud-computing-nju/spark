package consumer;

import config.ConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import producer.MessageProducer;

import java.util.*;

/**
 * 消息消费者
 */
public class MessageConsumer {

    //实现消息消费的kafka内置类
    private Consumer<String, String> consumer;

    //cache buffer size
    private final int minBatchSize;

    private boolean isOn=false; //consumer status

    private Thread t; //consumer thread

    //output
    Queue<ConsumerRecord<String,String>> outputQueue=new LinkedList<>();

    public MessageConsumer(){
        Properties props=new Properties();
        props.put("bootstrap.servers", ConsumerConfig.SERVER_ADDRESS);
        props.put("group.id", ConsumerConfig.GROUP_ID);
        props.put("enable.auto.commit", ConsumerConfig.ENABLE_AUTO_COMMIT);
        props.put("key.deserializer", ConsumerConfig.KEY_DESERIALIZER);
        props.put("value.deserializer", ConsumerConfig.VALUE_DESERIALIZER);
        if(!ConsumerConfig.ENABLE_AUTO_COMMIT){
            this.minBatchSize=ConsumerConfig.MIN_BATCH_SIZE;
        }
        else{
            this.minBatchSize=0;
            props.put("auto.commit.interval.ms", ConsumerConfig.AUTO_COMMIT_INTERVAL);
        }
        consumer=new KafkaConsumer<String, String>(props);
    }

    public void subscribeTopics(String... args){
        this.consumer.subscribe(Arrays.asList(args));
    }

    public void consumeMessage(){
        MessageConsumer that=this;
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                if(that.minBatchSize==0){
                    //enable auto-offset-commit
                    while (true) {
                        ConsumerRecords<String, String> records = consumer.poll(100);
                        for (ConsumerRecord<String, String> record : records){
                            System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
                            that.outputQueue.offer(record);
                        }
                    }
                }else{
                    List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
                    while (true) {
                        ConsumerRecords<String, String> records = consumer.poll(100);
                        for (ConsumerRecord<String, String> record : records) {
                            buffer.add(record);
                        }
                        if (buffer.size() >= minBatchSize) {
                            System.out.println("cache full");
                            that.outputQueue.addAll(buffer);
                            consumer.commitSync();
                            buffer.clear();
                        }
                    }
                }
            }
        });
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        that.t=t;
        that.isOn=true;
        t.start();
    }

    public boolean stopConsuming(){
        if(this.isOn){
            t.stop();
            this.isOn=false;
            return true;
        }
        else{
            return false;
        }
    }

    public ConsumerRecord<String,String> pollMessageFromQueue(){
        return this.outputQueue.poll();
    }

    public int getMessageRemainsNum(){
        return this.outputQueue.size();
    }

    public static void main(String[] args) {

        MessageConsumer messageConsumer=new MessageConsumer();
        messageConsumer.subscribeTopics("TestTopic");
        messageConsumer.consumeMessage();
        MessageProducer.main(args);
    }
}
