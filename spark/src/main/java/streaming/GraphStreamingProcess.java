package streaming;

import mapper.GraphMapper;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.*;

public class GraphStreamingProcess extends StreamingProcess{

    GraphMapper graphMapper;

    void process() throws InterruptedException {
        final JavaReceiverInputDStream<String> stream=this.getStream();
        JavaStreamingContext jsc=this.getContext();

        final JavaDStream<String> tags=stream.flatMap(new FlatMapFunction<String, String>() {
            public Iterator<String> call(String s) throws Exception {
                String[] split = s.trim().split(" ");
                return Arrays.asList(split).iterator();
            }
        });

        final List<String> nodes=new ArrayList<String>();
        tags.foreachRDD(new VoidFunction<JavaRDD<String>>() {
            @Override
            public void call(JavaRDD<String> stringJavaRDD) throws Exception {
                stringJavaRDD.foreach(new VoidFunction<String>() {
                    @Override
                    public void call(String s) throws Exception {
                        nodes.add(s);
                        saveNode(s);
                        for(String endNode:nodes){
                            saveEdge(s,endNode,1);
                        }
                    }
                });
            }
        });

        jsc.start();
        jsc.awaitTerminationOrTimeout(600*1000L);
        jsc.stop();
    }

    private void saveNode(String s){
        if(!graphMapper.hasNode(s))
            graphMapper.insertNode(s);
    }

    private void saveEdge(String start,String end,float weight){
        if(graphMapper.hasEdge(start,end)){
            graphMapper.updateEdge(start,end,weight);
        }else if(graphMapper.hasEdge(end,start)){
            graphMapper.updateEdge(end,start,weight);
        }else{
            graphMapper.insertEdge(start,end,weight);
        }
    }

    public static void main(String[] args) {

    }
}
