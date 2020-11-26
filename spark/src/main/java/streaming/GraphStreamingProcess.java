package streaming;

import db.DbHelper;
import model.Graph;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class GraphStreamingProcess extends StreamingProcess{

    public static int LIMITS=20;
    List<String[]> accumulation= new ArrayList<String[]>();
    Set<String> names=new HashSet<String>();
    int graphIndex=0;

    void process() throws InterruptedException {
        JavaReceiverInputDStream<String> stream=this.getStream();
        JavaStreamingContext jsc=this.getContext();

        final JavaDStream<String> tags=stream.flatMap(new FlatMapFunction<String, String>() {
            public Iterator<String> call(String s) throws Exception {
                String[] split = s.trim().split(" ");
                names.add(s);
                accumulation.add(split);
                return Arrays.asList(split).iterator();
            }
        });
        if(names.size()>=LIMITS){
            //处理已经积累的边
            final Graph graph=new Graph(names.size());
            final List<String> name = new ArrayList<String>(names);
            for(String n:name){graph.setNodeName(n,name.indexOf(n));}

            for(String[] ps:accumulation){
                for(int i=0;i<ps.length-1;i+=1){
                    for(int j=i+1;j<ps.length;j+=1){
                        graph.addEdge(name.indexOf(ps[i]),name.indexOf(ps[j]),1);
                    }
                }
            }
            graph.unify();

            int minIndex=-1;
            float minValue= Float.MAX_VALUE;
            int pointer=0;
            for(String[] ps:accumulation){
                int[] nodeIndex=new int[ps.length];
                for(int i=0;i<nodeIndex.length;i+=1){
                    nodeIndex[i]=name.indexOf(ps[i]);
                }
                float value=graph.findMinSubGraphCoverNodes(nodeIndex);
                if(value<minValue){
                    minIndex=pointer;
                    minValue=value;
                }
            }

            saveGraph(graph,graphIndex,accumulation.get(minIndex));
            names.clear();
            accumulation.clear();
        }

        jsc.start();
        jsc.awaitTerminationOrTimeout(20*1000L);
    }

    private void saveGraph(Graph g,int index,String[] colors){
        String[] names=g.nodesName;
        List<String> c=new ArrayList<String>(Arrays.asList(colors));

        for(String n:names){
            if(c.contains(n)){
                Connection conn = null;
                try {
                    conn = DbHelper.getConnection();
                    String sql="INSERT INTO nodes VALUES (%s,%s,%s)";
                    String[] params=new String[]{};
                    DbHelper.callProc(conn,String.format(sql,"",n,"group"+(c.contains(n)?"1":"0")),params);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

        float[][] m=g.getMatrix();
        int size=m.length;

        for(int i=0;i<size-1;i+=1){
            String name1=names[i];
            for(int j=i+1;j<size;j+=1){
                String name2=names[j];
                if(m[i][j]>0){
                    try {
                        Connection conn=DbHelper.getConnection();
                        String sql="insert into links VALUES(%s,%s,%s)";
                        String[] params=new String[]{};
                        DbHelper.callProc(conn,String.format(sql,name1,name2, m[i][j]),params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Connection conn= null;
        try {
            conn=DbHelper.getConnection();
            String sql="INSERT INTO links VALUES (%s,%s,%s)";
            String[] params=new String[]{};
            DbHelper.callProc(conn,String.format(sql,"1","1","2"),params);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
