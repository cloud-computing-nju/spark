package streaming;

import config.SparkConfig;
import entity.GraphItem;
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
        final List<String[]> graphList=new ArrayList<String[]>();


        stream.foreachRDD(new VoidFunction<JavaRDD<String>>() {
            @Override
            public void call(JavaRDD<String> stringJavaRDD) throws Exception {
                stringJavaRDD.foreach(new VoidFunction<String>() {
                    @Override
                    public void call(String s) throws Exception {
                        GraphMapper graphMapper=getMapper();
                        String[] split = s.trim().split(" ");
                        graphList.add(split);

                        for(String n:split){
                            saveNode(n,graphMapper);
                        }

                        for(int i=0;i<split.length-1;i+=1){
                            for(int j=i+1;j<split.length;j+=1){
                                saveEdge(split[i],split[j],1,graphMapper);
                            }
                        }

                    }
                });

            }
        });


        jsc.start();
        jsc.awaitTerminationOrTimeout(600*1000L);
        jsc.stop();

        //接下来对顶点进行染色
        float BORDER=3;
        GraphMapper graphMapper=getMapper();
        List<GraphItem> gis=graphMapper.selectAll();
        List<String> nodeNames=new ArrayList<String>();
        for(GraphItem gi:gis){
            if(gi.isNode()&&!nodeNames.contains(gi.getName())){
                nodeNames.add(gi.getName());
            }
        }
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

        for(String[] ns:graphList){
            int[] ins=new int[ns.length];
            for(int i=0;i<ins.length;i+=1){
                ins[i]=nodeNames.indexOf(ns[i]);
            }
            float result=graph.findMinSubGraphCoverNodes(ins);
            if(result<BORDER){
                //需要染色
                for(String n:ns){
                    graphMapper.updateNodeGroup(n,"group1");
                }
            }
        }

    }

    private void saveNode(String s,GraphMapper graphMapper){
        if(!graphMapper.hasNode(s))
            graphMapper.insertNode(s);
    }

    private GraphMapper getMapper(){
        String resource = "MapperConfig.xml";
        InputStream inputStream = null;

        {
            try {
                inputStream = Resources.getResourceAsStream(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //1、创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //2、获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //3、获取mapper
        GraphMapper graphMapper = sqlSession.getMapper(GraphMapper.class);
        return graphMapper;

    }

    private void saveEdge(String start,String end,float weight,GraphMapper graphMapper){
        if(graphMapper.hasEdge(start,end)){
            graphMapper.updateEdge(start,end,weight);
        }else if(graphMapper.hasEdge(end,start)){
            graphMapper.updateEdge(end,start,weight);
        }else{
            graphMapper.insertEdge(start,end,weight);
        }
    }

    public static void main(String[] args) {
        try {
            new GraphStreamingProcess().process();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
