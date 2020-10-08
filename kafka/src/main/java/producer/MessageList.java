package producer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储发送的message记录，由所有producer共同维护
 */
public class MessageList {

    private static MessageList messageList=null;

    //存储不同topic的message的发送时间
    private Map<String,String> historyMessages=new HashMap<String, String>();

    private MessageList(){}

    public static MessageList getInstance(){
        if(MessageList.messageList==null){
            messageList=new MessageList();
        }
        return MessageList.messageList;
    }

    public int addMessage(String topic){
        long timeStamp=new Date().getTime();
        if(this.historyMessages.containsKey(topic)){
            this.historyMessages.put(topic,this.historyMessages.get(topic).concat(" ").concat(Long.toString(timeStamp)));
        }else{
            this.historyMessages.put(topic,Long.toString(timeStamp));
        }
        return getMessageNum(topic);
    }

    public int getMessageNum(String topic){
        if(!this.historyMessages.containsKey(topic)){
            return 0;
        }
        return this.historyMessages.get(topic).split("").length;
    }

}
