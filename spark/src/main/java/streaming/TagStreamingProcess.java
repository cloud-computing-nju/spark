package streaming;

import config.SparkConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 业务:词云
 * 标签流处理实现类
 */
public class TagStreamingProcess extends StreamingProcess{
    private static SparkConf initConf(){
        return new SparkConf().setMaster("spark://".concat(SparkConfig.MASTER_HOSTNAME).concat(":").concat(String.valueOf(SparkConfig.MASTER_SPARK_PORT))).setAppName(SparkConfig.APP_NAME);
    }

    private static JavaStreamingContext initStreamingContext(SparkConf conf){
        return new JavaStreamingContext(conf, Durations.seconds(SparkConfig.INTERVAL_SEC));
    }

    private static JavaReceiverInputDStream<String> initDStream(JavaStreamingContext jsc){
        return jsc.socketTextStream(SparkConfig.MASTER_HOSTNAME,SparkConfig.TAG_SOCKET_PORT);
    }

    public TagStreamingProcess(){
        JavaStreamingContext context=initStreamingContext(initConf());
        this.setContext(context);
        this.setStream(initDStream(context));
    }


    void process() throws InterruptedException {
        JavaReceiverInputDStream<String> stream=this.getStream();
        JavaStreamingContext jsc=this.getContext();

        JavaDStream<String> tags=stream.flatMap(new FlatMapFunction<String, String>() {
            public Iterator<String> call(String s) throws Exception {
                String[] split = s.split(" ");
                return Arrays.asList(split).iterator();
            }
        });

        JavaPairDStream<String, Integer> mapToPairs=tags.mapToPair(new PairFunction<String, String, Integer>() {
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String, Integer>(s,1);
            }
        });

        JavaPairDStream<String, Integer> entries=mapToPairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer+integer2;
            }
        });

        System.out.println(entries);

        jsc.start();
        jsc.awaitTermination();
    }

    public static void main(String[] args) {
        TagStreamingProcess process=new TagStreamingProcess();
        try {
            process.process();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
