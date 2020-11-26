package producer;

import model.DataModel;

/**
 * 消息类
 */
public class Message {
    private String topic=null;
    private DataModel value=null;
    public Message(){}

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public DataModel getValue() {
        return value;
    }

    public void setValue(DataModel value) {
        this.value = value;
    }
}
