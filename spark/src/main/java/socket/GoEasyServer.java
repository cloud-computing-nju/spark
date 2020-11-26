package socket;

import io.goeasy.GoEasy;

public class GoEasyServer {
    public void sendMessage(String msg){
        GoEasy goEasy = new GoEasy( "https://rest-hangzhou.goeasy.io", "BC-1e05a5514aef4b7d8b0a8fca4a0d04db");
        goEasy.publish("my_channel", "cat:1 dog:2");
    }

    public static void main(String[] args) {
        GoEasyServer goEasyServer = new GoEasyServer();
        goEasyServer.sendMessage("11");
    }

}
