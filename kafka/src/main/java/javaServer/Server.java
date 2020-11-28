package javaServer;

import com.google.gson.Gson;
import config.ConsumerConfig;
import config.ServerConfig;
import consumer.MessageConsumer;
import model.DataModel;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import producer.Message;
import producer.MessageProducer;
import producer.Response;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @param socket
     * @return
     */
    private static Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }
    public static void main(String[] args) {
        String topic="bilibili";
        MessageConsumer messageConsumer=new MessageConsumer();
        messageConsumer.subscribeTopics(topic);
        messageConsumer.consumeMessage();

        //todo 开启子线程，不断询问messageConsunmer的队列,将消息发给spark
        //word cloud send to spark
        ServerSocket server = null;
        try {
            server=new ServerSocket(ConsumerConfig.SPARK_WC_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ServerSocket s=server;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client=null;
                try {
                    client=s.accept();
                    System.out.println("connect to spark");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true){

                    try {
                        if(client==null||client.isClosed()||isServerClose(client)) {client=s.accept();
                        //System.out.println("connect to spark");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(messageConsumer.getMessageRemainsNum()>0){
                        System.out.println("send");
                        assert client != null;
                        DataModel s=messageConsumer.pollMessageFromQueue();
                        System.out.println(s.toString());
                        messageConsumer.sendMessage(s,client);
                    }
                }
            }
        }).start();


        MessageProducer producer=new MessageProducer();
        try {
            ServerSocket serverSocket=new ServerSocket(ServerConfig.PORT);
            while(true){
                Socket clientSocket=serverSocket.accept();
                if(clientSocket==null||clientSocket.isClosed()){
                    clientSocket=serverSocket.accept();
                }
                StringBuilder requestDataBuilder= new StringBuilder();
                byte[] bytes=new byte[1024];
                int len;
                while((len=clientSocket.getInputStream().read(bytes))!=-1){
                    requestDataBuilder.append(new String(bytes,0,len, StandardCharsets.UTF_8));
                }
                String requestData=requestDataBuilder.toString();

                // gson 解析为java对象
                Gson gson=new Gson();
                DataModel dataModel=gson.fromJson(requestData,DataModel.class);
                System.out.println(dataModel);

                //producer将对象序列化后传给kafka
                Message message=new Message();
                message.setTopic(topic);
                message.setValue(dataModel);
                Response response=producer.sendMessageSync(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}