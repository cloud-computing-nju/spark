package streaming;

import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * 数据流处理抽象类
 */
public abstract class StreamingProcess {
    private JavaReceiverInputDStream<String> stream;
    JavaStreamingContext context;

    public JavaReceiverInputDStream<String> getStream() {
        return stream;
    }

    public void setStream(JavaReceiverInputDStream<String> stream) {
        this.stream = stream;
    }

    public JavaStreamingContext getContext() {
        return context;
    }

    public void setContext(JavaStreamingContext context) {
        this.context = context;
    }

    abstract void process() throws InterruptedException;

}
