package socket;

import config.GoEasyConfig;
import io.goeasy.GoEasy;

import java.io.Serializable;

public class GoEasyServer implements Serializable {
    public void sendMessage(String msg){
        GoEasy goEasy = new GoEasy( GoEasyConfig.REGION_HOST, GoEasyConfig.APP_KEY);
        goEasy.publish(GoEasyConfig.CHANNEL, msg);
    }

}
