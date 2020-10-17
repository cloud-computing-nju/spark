package javaServer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import config.ServerConfig;
import consumer.MessageConsumer;
import model.DataModel;
import producer.Message;
import producer.MessageProducer;
import producer.Response;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) {
        String topic="bilibili";

        MessageConsumer messageConsumer=new MessageConsumer();
        messageConsumer.subscribeTopics(topic);
        messageConsumer.consumeMessage();
        //todo:开启子线程，不断询问messageConsunmer的队列，然后通过socket发到7000端口

        MessageProducer producer=new MessageProducer();


        try {
            ServerSocket serverSocket=new ServerSocket(ServerConfig.PORT);
            while(true){
                Socket clientSocket=serverSocket.accept();
                //System.out.println("accepted");
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