package DFA;


import Lex.AllManager;
import NFA.NFaNode;
import NFA.NFaPair;
import mException.DFaException;
import mException.NFaException;


import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

/**
 * DFA 类
 * 运用子集构造法进行将NFA转换为DFA
 *
 * version 1.0的版本
 * 只是针对一个单一的NFA图，只有一对开始和结束的NFA结点
 *
 * version 1.1的版本
 * 加入了将多个NFA的图利用"|"运算符的规则合成一个拥有一个开始结点和多个结束结点对的NFA图
 * 同时加入了总的table表的遍历信息
 *
 * @author Mind
 * @version 1.1
 */
public class DFA {

    private NFaNode headNode;
    //存储传参进来的各个正则表达式的pair，用来终结符的等级判断
    private NFaPair[] nFaPairArray;
    //当前NFA图中的存在的所有终结符的边
    private List<Integer> finalityArray=new ArrayList<>();

    //DFaNode 和 Closure一一对应
    private List<DFaNode> dFaNodes=new ArrayList<>();
    private List<Closure> closures=new ArrayList<>();

    /**
     *闭包内部类
     * 使用hashset来存储NFA结点得编号，表示一个闭包内得NFA结点
     */
    private class Closure{
        Set<Integer> numOfNode=new HashSet<>();
        ArrayList<NFaNode> nFaNodes=new ArrayList<>();

        boolean isIncludeEnd=false;
        //如果包括了终结符则该属性有效,表示第几个正则表达式
        int endLevel=Integer.MAX_VALUE;

        //判断两闭包是否一致
        public boolean isEqual(Closure anothreClosure){
            if(this.nFaNodes.size()!=anothreClosure.nFaNodes.size())
                return false;
            Iterator<Integer> interator=numOfNode.iterator();
            while (interator.hasNext()){
                if(!anothreClosure.numOfNode.contains(interator.next()))
                    return false;
            }
            return true;
        }
    }

    public DFA(NFaPair[] nFaPairArray) throws DFaException, NFaException {
        this.nFaPairArray=nFaPairArray;
        this.headNode=mergeFaPair(nFaPairArray);
        dfa();
        clearNFA();
    }

    private void dfa() throws DFaException {
        //获取所有的终结符情况
        getFinality(headNode);
        //添加第一个闭包和DFA结点
        Closure closure=new Closure();
        closure.nFaNodes.add(headNode);
        closure.numOfNode.add(headNode.getIdentification());
        closure=getClosure(closure,-1,true);
        closures.add(closure);
        DFaNode dFaNode= AllManager.dFaNodeManager.newNfaNode();
        dFaNodes.add(dFaNode);

        //当前处理的第pos个闭包集
        int pos=0;
        while (pos!=closures.size()) {
            Closure currentClosure=closures.get(pos);
            //遍历所有的终结符边加入所有关系闭包
            for (int i = 0; i < finalityArray.size(); ++i) {
                Closure resultClosure=getClosure(currentClosure,finalityArray.get(i),false);
                //如果是空的直接跳过
                if (resultClosure.numOfNode.size()==0)
                    continue;
                //判断是否存在过相应的闭包
                int isExist=0;
                for(;isExist<closures.size();++isExist){
                    if(closures.get(isExist).isEqual(resultClosure))
                        break;
                }
                //不重复的闭包结果集，加入新的队列中，并且对相应的DFA结点连接上对应的边
                if(isExist==closures.size()){
                    closures.add(resultClosure);
                    DFaNode newDFaNode=AllManager.dFaNodeManager.newNfaNode();
                    newDFaNode.setEndLevel(resultClosure.endLevel);
                    dFaNodes.get(pos).add((char)(int)finalityArray.get(i),newDFaNode);
                    dFaNodes.add(newDFaNode);
                    if (resultClosure.isIncludeEnd)
                        newDFaNode.isIncludeEnd=true;
                }else {//重复的闭包结果集
                    int index=0;
                    boolean need=true;
                    if (dFaNodes.get(pos).getEdge()!=null)
                    for (int edge=0;edge<dFaNodes.get(pos).getEdge().size();edge++){
                        if (dFaNodes.get(pos).getEdge().get(edge)==(char)(int)finalityArray.get(i)){
                            if (dFaNodes.get(pos).getDfaNodes().get(edge)==dFaNodes.get(isExist)){
                                need=false;
                                break;
                            }
                        }
                    }
                    if (need)
                    dFaNodes.get(pos).add((char)(int)finalityArray.get(i),dFaNodes.get(isExist));
                }
            }
            pos++;
        }

    }


    /**
     * 内部函数，寻到闭包集对应的闭包集
     * 实现思路：
     * 根据nfa结点获取闭包,如果有终结符,其他数值对应数值得闭包,返回的闭包集未经过查重判断
     *
     * @param closure 需要处理的闭包集
     * @param edge  对应的边
     * @param forhead 是否是第一个结点，专门处理第一个闭包集，因为需要将自身闭包内容加入到新的闭包中
     * @return 返回找到的闭包集
     * @throws DFaException 根据逻辑需要抛出的错误，方便以后的定位修改
     */
    private Closure getClosure( Closure closure,int edge,boolean forhead) throws DFaException {
        Closure result=new Closure();
        Queue<NFaNode> queue=new LinkedTransferQueue<>();
        if (forhead){
            result.numOfNode.add(headNode.getIdentification());
            result.nFaNodes.add(headNode);
        }

        //先获取终结符边的下一个结点，这一步可以优化进行判断闭包集是否相同,如果确定是新的再进行寻找ε的闭包
        for (NFaNode nFaNode : closure.nFaNodes) {
            if (nFaNode.getEdge()!=null&&nFaNode.getEdge().contains(edge)){
                for(int i=0;i<nFaNode.getEdge().size();++i){
                    if(nFaNode.getEdge().get(i)==edge){
                        NFaNode theNode=nFaNode.getNfaNodes().get(i);
                        queue.add(theNode);
                        //是否包含终结符边，如果存在更新信息，包括了标记还有终结符的等级
                        for (NFaPair pair:nFaPairArray){
                            if (theNode==pair.getEnd()){
                                result.isIncludeEnd=true;
                                result.endLevel=pair.getEndLevel()<result.endLevel?pair.getEndLevel():result.endLevel;
                            }
                        }
                        result.nFaNodes.add(theNode);
                        result.numOfNode.add(theNode.getIdentification());
                    }
                }

            }
        }
        //进行寻找
        NFaNode nFaNode;
        while (!queue.isEmpty()){
            nFaNode=queue.poll();
            if(nFaNode.getEdge()!=null)
            for (int i=0;i<nFaNode.getEdge().size();++i){
                if(nFaNode.getEdge().get(i)==-1){
                    if(!result.numOfNode.contains(nFaNode.getNfaNodes().get(i).getIdentification())) {
                        queue.add(nFaNode.getNfaNodes().get(i));
                        result.numOfNode.add(nFaNode.getNfaNodes().get(i).getIdentification());
                        result.nFaNodes.add(nFaNode.getNfaNodes().get(i));
                        //是否包含终结符边，如果存在更新信息，包括了标记还有终结符的等级
                        for (NFaPair pair:nFaPairArray){
                            if (nFaNode.getNfaNodes().get(i)==pair.getEnd()){
                                result.isIncludeEnd=true;
                                result.endLevel=pair.getEndLevel()<result.endLevel?pair.getEndLevel():result.endLevel;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 内部函数，将NFA的所有结点回收
     *
     */
    private void clearNFA(){
         Set<NFaNode> clearNFA=new HashSet<>();
         clearNFA.add(headNode);
         Queue<NFaNode> queue=new LinkedTransferQueue<>();
         queue.add(headNode);
         NFaNode nFaNode;
         while (!queue.isEmpty()){
             nFaNode=queue.poll();
             if(nFaNode.getNfaNodes()!=null)
             for (NFaNode nextNode:nFaNode.getNfaNodes()){
                 if(!clearNFA.contains(nextNode)){
                     clearNFA.add(nextNode);
                     queue.add(nextNode);
                 }
             }
         }
         Iterator<NFaNode> iterator=clearNFA.iterator();
         while (iterator.hasNext()){
             AllManager.nFaNodeManager.deleteNfaNode(iterator.next());
         }
    }

    /**
     * 内部函数，获取所有的终结符，自动除去-1这个空集元素，广度优先遍历将所有存在得除了空集得边判断该边
     * 是否已经录入到finalInTable中，否则录入
     *
     * @param headNode NFA的头
     * @throws DFaException 根据逻辑需要抛出的错误，方便以后的定位修改
     */
    private void getFinality(NFaNode headNode) throws DFaException {

        if(headNode==null)
            throw new DFaException("this is a null point of NFaPair");

        Queue<NFaNode> queue=new LinkedTransferQueue<>();
        headNode.isVisit=true;
        queue.add(headNode);
        NFaNode node;
        while (!queue.isEmpty()){
            node=queue.poll();
            if(node.edgeSize()!=0){
                for (int i=0;i<node.edgeSize();i++) {
                    int currentEdge=node.getEdge().get(i);
                    if(currentEdge!=-1) {
                            finalityArray.add(currentEdge);
                    }
                    if(!node.getNfaNodes().get(i).isVisit){
                        queue.add(node.getNfaNodes().get(i));
                        node.getNfaNodes().get(i).isVisit=true;
                    }
                }
            }
        }
    }

    public List<DFaNode> getdFaNodes() {
        return dFaNodes;
    }

    public List<Integer> getFinalityArray() {
        return finalityArray;
    }

    private NFaNode mergeFaPair(NFaPair[] NFaPairArray) throws NFaException {
        NFaNode headNode=AllManager.nFaNodeManager.newNfaNode();
        for(NFaPair nFaPair:NFaPairArray){
            headNode.addEpsilon(nFaPair.getStart());
        }
        return headNode;
    }

    //测试打印使用
    public void print(){
        DFaNode head=dFaNodes.get(0);
        Queue<DFaNode> queue=new LinkedTransferQueue<>();
        queue.add(head);
        head.isVisit=true;
        while (!queue.isEmpty()){
            DFaNode dFaNode=queue.poll();
            dFaNode.isVisit=true;
            if (dFaNode.isIncludeEnd)
                System.out.println(dFaNode.getIdentification()+"end");
            for (int i=0;i<dFaNode.getEdge().size();++i){
                System.out.println("[ "+dFaNode.getIdentification()+"----"+(char)(int)dFaNode.getEdge().get(i)+"----"+dFaNode.getDfaNodes().get(i).getIdentification()+"]");
                if(!dFaNode.getDfaNodes().get(i).isVisit){
                    queue.add(dFaNode.getDfaNodes().get(i));
                    dFaNode.getDfaNodes().get(i).isVisit=true;
                }
            }
        }
    }

}
