package streaming;

import config.SparkConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Serializable;
import scala.Tuple2;
import socket.GoEasyServer;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 业务:词云
 * 标签流处理实现类
 */
public class TagStreamingProcess extends StreamingProcess implements Serializable {

    private static SparkConf initConf(){
        return new SparkConf().setMaster("local[2]").setAppName(SparkConfig.APP_NAME);
    }

    private static JavaStreamingContext initStreamingContext(SparkConf conf){
        return new JavaStreamingContext(conf, Durations.seconds(SparkConfig.INTERVAL_SEC));
    }

    private static JavaReceiverInputDStream<String> initDStream(JavaStreamingContext jsc){
        return jsc.socketTextStream(SparkConfig.MASTER_HOSTNAME,SparkConfig.MASTER_SPARK_WC_PORT);
    }

    public TagStreamingProcess(){
        JavaStreamingContext context=initStreamingContext(initConf());
        this.setContext(context);
        this.setStream(initDStream(context));
    }


    void process() throws InterruptedException {
        GoEasyServer goEasyServer = new GoEasyServer();
        JavaReceiverInputDStream<String> stream=this.getStream();
        JavaStreamingContext jsc=this.getContext();

        JavaDStream<String> tags=stream.flatMap(new FlatMapFunction<String, String>() {
            public Iterator<String> call(String s) throws Exception {
                //System.out.println(s);
                String[] split = s.trim().split(" ");
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

        final String[] result = {""};
        entries.map(new Function<Tuple2<String, Integer>, Object>() {
            public Object call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
                if(result[0].length()!=0){result[0]+=" ";}
                result[0] +=stringIntegerTuple2._1+":"+stringIntegerTuple2._2;
                return stringIntegerTuple2;
            }
        });

        entries.print();
        //socket客户端向服务端发送数据
        String msg = "send message to vue";
        System.out.println(msg);
        //goEasy发送数据
        goEasyServer.sendMessage(result[0]);
        jsc.start();
        jsc.awaitTerminationOrTimeout(20*1000L);
    }
}
