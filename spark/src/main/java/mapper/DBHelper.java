package mapper;

import entity.GraphItem;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public class DBHelper implements Serializable {
    SqlSession session;
    GraphMapper mapper;
    public DBHelper(){
        String resource = "MapperConfig.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //1、创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //2、获取sqlSession
        this.session=sqlSessionFactory.openSession();
        //3、获取mapper
        this.mapper= this.session.getMapper(GraphMapper.class);
    }


    public void saveEdge(String start,String end,float weight){
        if(this.mapper.hasEdge(start,end)){
            this.mapper.updateEdge(start,end,weight);
        }else if(this.mapper.hasEdge(end,start)){
            this.mapper.updateEdge(end,start,weight);
        }else{
            this.mapper.insertEdge(start,end,weight);
        }
        this.session.commit();
    }

    public void saveNode(String s){
        if(!this.mapper.hasNode(s)){
            this.mapper.insertNode(s);
            this.session.commit();
        }
    }

    public List<GraphItem> selectAll(){
        return this.mapper.selectAll();
    }

    public void updateNodeGroup(String name,String group){
        this.mapper.updateNodeGroup(name,group);
        this.session.commit();
    }

}
