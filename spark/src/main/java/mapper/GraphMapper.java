package mapper;

import entity.GraphItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GraphMapper {
    public boolean hasNode(@Param("nodeName") String nodeName);
    public void insertNode(@Param("nodeName") String nodeName);
    public void updateNodeGroup(@Param("nodeName")String nodeName,@Param("groupName")String groupName);
    public boolean hasEdge(@Param("source") String source,@Param("target") String target);
    public void insertEdge(@Param("source")String source,@Param("target") String target, @Param("value")float value);//添加一条边
    public void setEdge(@Param("source")String source,@Param("target")String target,@Param("value")float value);//设置边的权重
    public void updateEdge(@Param("source")String source,@Param("target")String target,@Param("value")float value);//更新边的权重
    public List<GraphItem> selectAll();
}
