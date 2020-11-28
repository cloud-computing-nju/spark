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
import org.apache.spark.api.java.JavaSparkContext;
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
    
    public GraphStreamingProcess(){

    }

    void process() throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setAppName("mysqlJdbc").setMaster("local[2]");
        // spark应用的上下对象
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        JavaRDD<String> lines = sc.textFile("E:/test.txt");
        JavaStreamingContext jsc=this.getContext();
        final List<String[]> graphList=new ArrayList<String[]>();

        final DBHelper helper=new DBHelper();
        helper.saveNode("node3");

        lines.foreachAsync(new VoidFunction<String>() {
            @Override
            public void call(String s) throws Exception {
                String[] split = s.trim().split(" ");
                graphList.add(split);
            }
        });

        System.out.println(graphList.size());

        for(String[] ss:graphList){
            for(String s:ss){
                helper.saveNode(s);
            }
        }

        for(String[] ss:graphList){
            for(int i=0;i<ss.length-1;i+=1){
                for(int j=i+1;j<ss.length;j+=1){
                    helper.saveEdge(ss[i],ss[j],1);
                }
            }
        }

        //接下来对顶点进行染色
        float BORDER=3;
        List<GraphItem> gis=helper.selectAll();
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
                    helper.updateNodeGroup(n,"group1");
                }
            }
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
