package consumer;

import config.ConsumerConfig;
import model.DataModel;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import preprocessor.DataModelPreprocessor;
import preprocessor.Preprocessor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 消息消费者
 */
public class MessageConsumer {

    //实现消息消费的kafka内置类
    private Consumer<String, DataModel> consumer;

    //cache buffer size
    private final int minBatchSize;

    private boolean isOn=false; //consumer status

    private Thread t; //consumer thread

    //数据预处理器
    private Preprocessor preprocessor;

    //output
    Queue<DataModel> outputQueue=new ConcurrentLinkedQueue();

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
        consumer=new KafkaConsumer<String, DataModel>(props);
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
                        ConsumerRecords<String, DataModel> records = consumer.poll(100);
                        for (ConsumerRecord<String, DataModel> record : records){
                            //System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
                            that.outputQueue.offer(record.value());
                        }
                    }
                }else{
                    List<ConsumerRecord<String, DataModel>> buffer = new ArrayList<>();
                    while (true) {
                        ConsumerRecords<String, DataModel> records = consumer.poll(100);
                        for (ConsumerRecord<String, DataModel> record : records) {
                            buffer.add(record);
                        }
                        if (buffer.size() >= minBatchSize) {
                            System.out.println("cache full");
                            for(ConsumerRecord<String,DataModel> record:buffer){
                                that.outputQueue.offer(record.value());
                            }
                            consumer.commitSync();
                            buffer.clear();
                        }
                    }
                }
            }
        });

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

    public DataModel pollMessageFromQueue(){
        return this.outputQueue.poll();
    }

    public int getMessageRemainsNum(){
        return this.outputQueue.size();
    }

    public void sendMessage(DataModel dataModel,Socket client){
        //DataModelPreprocessor,可能还有其它处理器
        preprocessor = new DataModelPreprocessor();
        String message = preprocessor.preprocess(dataModel);
        //System.out.println(message);
        try {
            OutputStream os =  client.getOutputStream();
            os.write(message.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
