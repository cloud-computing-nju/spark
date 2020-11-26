package model;

import org.apache.xerces.impl.xs.SchemaSymbols;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public float[][] matrix;
    public String[] nodesName=null;

    public Graph(int nodeNum) {
        assert nodeNum >= 1;
        this.matrix = new float[nodeNum][nodeNum];
        this.nodesName=new String[nodeNum];
        fill(-1,matrix);
    }

    public Graph(){}

    public Graph(float[][] matrix){
        assert matrix.length==matrix[0].length;
        this.matrix=matrix;
    }

    public float[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(float[][] matrix) {
        this.matrix = matrix;
    }

    /**
     * add edge value
     * @param node1
     * @param node2
     * @param value
     * @return
     */
    public void addEdge(int node1, int node2, float value) {
        assert node1 > 0 && node1 < matrix.length;
        assert node2 > 0 && node2 < matrix[0].length;
        assert value > 0;

        if (matrix[node1][node2] > 0) {
            matrix[node1][node2]+=value;
            matrix[node2][node1]+=value;
        } else {
            matrix[node1][node2] = matrix[node2][node1]= value;
        }
    }

    public void setEdge(int node1, int node2, float value) {
        assert node1 > 0 && node1 < matrix.length;
        assert node2 > 0 && node2 < matrix[0].length;
        assert value > 0;

        matrix[node1][node2] = matrix[node2][node1]= value;

    }

    /**
     * remove an edge from graph
     * @param node1
     * @param node2
     */
    public void deleteEdge(int node1, int node2) {
        assert node1 > 0 && node1 < matrix.length;
        assert node2 > 0 && node2 < matrix[0].length;
        matrix[node1][node2] = matrix[node2][node1]=-1;
    }

    public void deleteEdges(int ...nodes){
        assert nodes.length>=2;
        int size=nodes.length;
        for(int i=0;i<size-1;i++){
            for(int j=i+1;j<size;j++){
                deleteEdge(nodes[i],nodes[j]);
            }
        }
    }

    public void setNodeName(String name,int index){
        if(this.nodesName==null) {
            System.out.println("not initialize");
        } else{
            assert index<this.nodesName.length;
            nodesName[index]=name;
        }
    }

    /**
     * unify data
     */
    public void unify(){
        float biggest=0;
        for(float[] a:matrix){
            for(float b:a){
                if(b>biggest)
                    biggest=b;
            }
        }
        if(biggest>0) {
            for(float[] a:matrix){
                for(float b:a){
                    if(b>0)
                        b=biggest/b;
                }
            }
        }

    }

    /**
     * find all nodes adjacent to node
     * @param node
     * @return
     */
    public List<Integer> findNeighbours(int node) {
        List<Integer> neighbours = new ArrayList<Integer>();
        for (int i = 0; i < matrix[0].length; i++) {
            if (matrix[node][i] > 0) {
                neighbours.add(i);
            }
        }
        return neighbours;
    }


    /**
     * calculate the shortest distance between two nodes
     * @param node1
     * @param node2
     * @return
     */
    public float minDistance(final int node1,final int node2) {
        if(node1==node2){return 0;}

        //到其他顶点的距离
        float[] distanceVector = new float[matrix.length];
        System.arraycopy(matrix[node1], 0, distanceVector, 0, matrix.length);

        //被包含在已知路径中的顶点
        final List<Integer> knownDots = new ArrayList<Integer>() {{
            this.add(node1);
        }};

        //尝试更新距离的顶点
        int edgeDot = node1;

        int counter = 1;
        while (counter < this.matrix.length) {
            float shortest = -1;
            int nodeSelected = -1;
            for(int i=0;i<distanceVector.length;i++){
                if(distanceVector[i]>0 && !knownDots.contains(i)){
                    if(shortest<0 || shortest>distanceVector[i]){
                        shortest=distanceVector[i];
                        nodeSelected=i;
                    }
                }
            }
            if(nodeSelected<0){break;}

            knownDots.add(nodeSelected);
            distanceVector[nodeSelected] = shortest;
            if (nodeSelected == node2) break;

            //更新distanceVectors
            List<Integer> neighbours = findNeighbours(nodeSelected);
            for (int neighbour : neighbours) {
                if (!knownDots.contains(neighbour)) {
                    float distance = distanceVector[nodeSelected] + matrix[nodeSelected][neighbour];
                    if (distanceVector[neighbour] < 0 || distanceVector[neighbour] > distance) {
                        distanceVector[neighbour] = distance;
                    }
                }
            }

            counter += 1;
        }
        return distanceVector[node2];
    }

    /**
     * find a spanning tree which has the smallest sum of edge weight
     * @return
     */
    public float getMinSpanningTreeSum() {
        float[][] matrix=this.matrix;

        int size=matrix.length;
        float[] lowcost = new float[size];
        int[] nervex = new int[size];
        //选中0号节点
        nervex[0] = -1;
        //记录最小生成树边的权值
        List<Float> edge = new ArrayList<Float>();
        //初始化
        for (int i = 1; i < size; i++) {
            lowcost[i] = matrix[0][i];
            nervex[i] = 0;
        }
        for (int i = 1; i < size; i++) {
            float min = 1000000000;
            int v = 0;
            for (int j = 1; j < size; j++) {
                if (nervex[j] != -1 && lowcost[j] < min && lowcost[j]>0) {
                    v = j;
                    min = lowcost[j];
                }
            }
            if (v != 0) {
                edge.add(lowcost[v]);
                nervex[v] = -1;
                //更新数组
                for (int k = 1; k < size; k++) {
                    if (nervex[k] != -1 && matrix[v][k] < lowcost[k]) {
                        lowcost[k] = matrix[v][k];
                        nervex[k] = v;
                    }
                }
            }
        }
        float res = 0;
        for (Float aFloat : edge) {
            res += aFloat;
        }
        return res;
    }

    /**
     * find a subgraph that covers the given nodes also has the smallest sum of path weight
     * @param nodes
     * @return
     */
    public float findMinSubGraphCoverNodes(int ...nodes){
        //复制原矩阵
        float[][] matrixCpy=new float[this.matrix.length][this.matrix.length];
        for(int i=0;i<this.matrix.length;i++){
            matrixCpy[i]=this.matrix[i].clone();
        }
        Graph graphCpy=new Graph();
        graphCpy.setMatrix(matrixCpy);

        graphCpy.deleteEdges(nodes);

        //从原矩阵中提取子图矩阵
        float[][] subMatrix=new float[nodes.length][nodes.length];
        //不经过子图内部顶点，子图两点间最短距离构成的矩阵
        float[][] subMatrix1=new float[nodes.length][nodes.length];
        //将两个矩阵合并为最小值矩阵
        float[][] subMatrix2=new float[nodes.length][nodes.length];
        //fill(-1,sub_matrix1);

        for(int i=0;i<subMatrix1.length;i++){
            for(int j=i;j<subMatrix1.length;j++){
                subMatrix[i][j]=subMatrix[j][i]=this.matrix[nodes[i]][nodes[j]];
                subMatrix1[i][j]=subMatrix1[j][i]=graphCpy.minDistance(nodes[i],nodes[j]);
                subMatrix2[i][j]=subMatrix2[j][i]= Math.min(subMatrix[i][j], subMatrix1[i][j]);
            }
        }

        return new Graph(subMatrix2).getMinSpanningTreeSum();
    }

    private static void fill(float value,float[][] matrix){
        int size1=matrix.length;
        int size2=matrix[0].length;
        for(int i=0;i<size1-1;i++){
            for(int j=i+1;j<size2;j++){
               matrix[i][j]=value;
            }
        }
    }

    public static void main(String[] args) {
        Graph graph=new Graph(5);
        graph.addEdge(0,1,3);
        graph.addEdge(0,2,3);
        graph.addEdge(1,2,1);
        graph.addEdge(0,3,1);
        graph.addEdge(1,3,1);
        graph.addEdge(0,4,1);
        graph.addEdge(2,4,1);
        System.out.println(graph.findMinSubGraphCoverNodes(0,1,2));

    }

}
