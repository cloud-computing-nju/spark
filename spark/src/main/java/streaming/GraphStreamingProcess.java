package streaming;

import config.SparkConfig;
import entity.GraphItem;
import mapper.DBHelper;
import mapper.GraphMapper;
import model.Graph;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class GraphStreamingProcess extends StreamingProcess implements Serializable {

    private static SparkConf initConf(){
        return new SparkConf().setMaster("local[2]").setAppName(SparkConfig.APP_NAME);
    }

    private static JavaStreamingContext initStreamingContext(SparkConf conf){
        return new JavaStreamingContext(conf, Durations.seconds(SparkConfig.INTERVAL_SEC));
    }

    private static JavaReceiverInputDStream<String> initDStream(JavaStreamingContext jsc){
        return jsc.socketTextStream(SparkConfig.MASTER_HOSTNAME,SparkConfig.MASTER_SPARK_GRAPH_PORT);
    }

    public GraphStreamingProcess(){
        JavaStreamingContext context=initStreamingContext(initConf());
        this.setContext(context);
        this.setStream(initDStream(context));
    }

    void process() throws InterruptedException {
        final JavaReceiverInputDStream<String> stream=this.getStream();
        JavaStreamingContext jsc=this.getContext();


        stream.foreachRDD(new VoidFunction<JavaRDD<String>>() {
            @Override
            public void call(JavaRDD<String> stringJavaRDD) throws Exception {
                stringJavaRDD.foreach(new VoidFunction<String>() {
                    @Override
                    public void call(String s) throws Exception {
                        DBHelper helper=new DBHelper();
                        String[] split = s.trim().split(" ");

                        for(String n:split){
                            helper.saveNode(n);
                        }

                        for(int i=0;i<split.length-1;i+=1){
                            for(int j=i+1;j<split.length;j+=1){
                                helper.saveEdge(split[i],split[j],1);
                            }
                        }

                        //接下来对顶点进行染色
                        float BORDER=3;
                        int NODE_BORDER=100;
                        List<GraphItem> gis=helper.selectAll();
                        List<String> nodeNames=new ArrayList<String>();
                        for(GraphItem gi:gis){
                            if(gi.isNode()&&!nodeNames.contains(gi.getName())){
                                nodeNames.add(gi.getName());
                            }
                        }
                        if(nodeNames.size()>=NODE_BORDER){
                            Graph graph=new Graph(nodeNames.size());
                            String[] nodeNamesStr=new String[nodeNames.size()];
                            for(int i=0;i<nodeNames.size();i+=1){
                                nodeNamesStr[i]=nodeNames.get(i);
                            }
                            graph.nodesName=nodeNamesStr;

                            for(GraphItem gi:gis){
                                if(!gi.isNode()){
                                    graph.addEdge(nodeNames.indexOf(gi.getSource()),nodeNames.indexOf(gi.getTarget()),gi.getValue());
                                }
                            }

                            graph.unify();

                            int[] ins=new int[split.length];
                            for(int i=0;i<ins.length;i+=1){
                                ins[i]=nodeNames.indexOf(split[i]);
                            }
                            float result=graph.findMinSubGraphCoverNodes(ins);
                            if(result<BORDER){
                                //需要染色
                                for(String n:split){
                                    helper.updateNodeGroup(n,"group1");
                                }
                            }

                        }

                    }
                });

            }
        });


        jsc.start();
        jsc.awaitTerminationOrTimeout(600*1000L);
        jsc.stop();

    }


    public static void main(String[] args) {
        try {
            new GraphStreamingProcess().process();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
