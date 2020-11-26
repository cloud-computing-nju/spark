package socket;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;


@ServerEndpoint(value = "/websocket")
public class SocketServer {

    private Session session = null;
    List<String> remains = new ArrayList<String>();

    //连接时执行
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("open");
    }

    public void sendMsg(String msg) {
        if (session == null) remains.add(msg);
        else {
            for (String s : remains) {
                this.session.getAsyncRemote().sendText(s);
            }
            remains.clear();
            this.session.getAsyncRemote().sendText(msg);
        }
    }


    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        System.out.println("init server");
        while (true) {
            socketServer.sendMsg("cat:1 dog:2");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
