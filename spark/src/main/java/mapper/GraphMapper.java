package mapper;

import entity.GraphItem;

import java.util.List;

public interface GraphMapper {
    public boolean hasNode(String nodeName);
    public void insertNode(String nodeName);
    public boolean hasEdge(String source,String target);
    public void insertEdge(String source,String target,float value);//添加一条边
    public void setEdge(String source,String target,float value);//设置边的权重
    public void updateEdge(String source,String target,float value);//更新边的权重
    public List<GraphItem> selectAll();
}
