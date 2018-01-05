package NFA;

import java.util.ArrayList;
import java.util.List;

/**
 * NFaNode 类
 * 运用Thompson算法中，NFA的一个普通圆点以及和它的出边
 * 类似于  O---a--->
 * 分别用arraylist存储下来，edge和nfaNodes一一对应
 *
 * 其中对于字符处理的原则是:
 * epsilon ε==-1  其他的字符对应相应的ascii码值
 *
 * @author Mind
 * @version 1.0
 */
public class NFaNode {

    //直到需要使用时才申请结点避免占空间
    private List<Integer> edge;
    private List<NFaNode> nfaNodes;
    //全球唯一编号，方便调试，请见NFApair的print函数
    private int identification;
    //编号索引
    private static int index=0;

    //调试临时变量
    public boolean isVisit=false;

    public NFaNode(){
        identification=index++;
    }

    /**
     * 为当前nfaNode添加指向边
     *
     * @param  edge 边上字符
     * @param  nfaNode 边指向的结点
     * @return 返回生成NFApair结点
     */
    public void add(String edge, NFaNode nfaNode){
        if(this.edge==null){
            this.edge=new ArrayList<>(2);
            nfaNodes=new ArrayList<>(2);
        }


        this.edge.add(Integer.valueOf(edge.charAt(0)));
        nfaNodes.add(nfaNode);
    }

    /**
     * 为当前nfaNode添加指向边，边上的字符是ε
     *
     * @param  nFaNode 边指向的结点
     * @return 返回生成NFApair结点
     */
    public void addEpsilon(NFaNode nFaNode){
        if(this.edge==null){
            this.edge=new ArrayList<>(2);
            nfaNodes=new ArrayList<>(2);
        }
        this.edge.add(-1);
        nfaNodes.add(nFaNode);
    }

    public int edgeSize(){
        return edge==null?0:edge.size();
    }

    public List<Integer> getEdge() {
        return edge;
    }

    public List<NFaNode> getNfaNodes() {
        return nfaNodes;
    }


    public int getIdentification() {
        return identification;
    }

    /**
     * 清除结点的所有状态
     */
    public void clearState(){
        if(edge!=null)
        edge.clear();
        if(nfaNodes!=null)
        nfaNodes.clear();
    }
}
