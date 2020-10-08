package producer;

/**
 * 消息类
 */
public class Message {
    private String topic=null;
    private String value=null;
    public Message(){}

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
