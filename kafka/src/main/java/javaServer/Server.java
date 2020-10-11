package javaServer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import config.ServerConfig;
import model.DataModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) {
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
//                JsonReader jsonReader=new JsonReader(new StringReader(requestData));
//                jsonReader.setLenient(true);
                //System.out.println(requestData);

                Gson gson=new Gson();
                DataModel dataModel=gson.fromJson(requestData,DataModel.class);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}