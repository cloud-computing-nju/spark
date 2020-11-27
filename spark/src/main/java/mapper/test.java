package mapper;

import entity.GraphItem;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class test {

    @Test
    public void test() throws IOException {
        String resource = "MapperConfig.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        //1、创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //2、获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //3、获取mapper
        GraphMapper graphMapper = sqlSession.getMapper(GraphMapper.class);
        //4、执行数据库操作，并处理结果集
//        List<GraphItem> k=graphMapper.selectAll();
//        for(int i=0;i<k.size();i++){
//            System.out.println("tableId:"+k.get(i).getTableid());
//            System.out.println("id:"+k.get(i).getId());
//            System.out.println("name:"+k.get(i).getName());
//            System.out.println("imgPath:"+k.get(i).getImgPath());
//            System.out.println("source:"+k.get(i).getSource());
//            System.out.println("target:"+k.get(i).getTarget());
//            System.out.println("type:"+k.get(i).getType());
//            System.out.println("value:"+k.get(i).getValue());
//        }
//        graphMapper.insertNode("b");
//        graphMapper.insertEdge("b", "a",2);
//        graphMapper.setEdge("b","a",4);
//        System.out.println(graphMapper.hasNode("c"));
        System.out.println(graphMapper.hasEdge("a","b"));
        sqlSession.commit();
    }

}

