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

    public static void main(String[] args) {
        //程序入口
        StreamingProcess process = null;
        if(args[0].equals("-t")){
            process=new TagStreamingProcess();
        }else if(args[0].equals("-g")){
            process=new GraphStreamingProcess();
        }else{
            System.out.println("instruction format wrong");
            System.exit(-1);
        }
        try {
            process.process();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
