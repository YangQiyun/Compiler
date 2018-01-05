package DFA;


import java.util.ArrayList;
import java.util.List;


/**
 * DFaNode 类
 * 运用子集构造法算法中，NFA的一个普通圆点以及和它的出边
 * 类似于  O---a--->
 * 分别用arraylist存储下来，edge和dfaNodes一一对应
 *
 *
 *
 * @author Mind
 * @version 1.0
 */
public class DFaNode {

    //直到需要使用时才申请结点避免占空间
    private List<Integer> edge;
    private List<DFaNode> dfaNodes;
    //全球唯一编号
    private int identification;
    //编号索引
    private static int index=0;

    public boolean isVisit=false;
    public boolean isIncludeEnd=false;
    //结束结点的等级，等级相当于书写正则表达式的行号，越前则等级越高，所以数值越小等级越大
    private int endLevel=Integer.MAX_VALUE;

    public DFaNode(){
        identification=index++;
    }

    /**
     * 为当前nfaNode添加指向边
     *
     * @param  edge 边上字符
     * @param  dfaNode 边指向的结点
     * @return 返回生成DFApair结点
     */
    public void add(char edge, DFaNode dfaNode){
        if(this.edge==null){
            this.edge=new ArrayList<>(2);
            dfaNodes=new ArrayList<>(2);
        }


        this.edge.add((int)(edge));
        dfaNodes.add(dfaNode);
    }

    public List<Integer> getEdge() {
        return edge;
    }

    public List<DFaNode> getDfaNodes() {
        return dfaNodes;
    }

    public int getIdentification() {
        return identification;
    }

    public int getEndLevel() {
        return endLevel;
    }

    public void setEndLevel(int endLevel) {
        this.endLevel = endLevel;
    }

    /**
     * 清除结点的所有状态
     */
    public void clearState(){
        if(edge!=null)
            edge.clear();
        if(dfaNodes!=null)
            dfaNodes.clear();
    }

}
