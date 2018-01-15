package ODFA;

import DFA.DFaNode;
import Lex.AllManager;
import NFA.NFaNode;
import Node.Node;
import mException.DFaException;
import mException.NFaException;


import java.util.*;
import java.util.concurrent.LinkedTransferQueue;


/**
 * DFA的优化：
 * 弱等价类优化
 *
 * 遇到的问题主要有
 * 第一点：对于终结符是不能进行优化的，不同的终结符结果状态即使最后能够合并在一起
 * 需要拆开，因为其实它们不是一个终结的状态，当状态机走到那个结点并不知道这是代表哪一个状态
 * 所以一开始对所有的终结符都拆开了，不进行等价类优化
 * 第二点：是对于不存在该边的话也是属于一个新的集合，不是走回自身
 * 第三点：是注意重构新的DFA图
 *
 * 实验算法：
 * 1.对于DFA的字母表M，把M划分成终态集和非终态集，令P=M
 * 2.对于P中的一个集合I，寻找I每一个元素K，找到K从边a对应的节点，加入集合I1，若I1是P中某个集合的子集，跳至步骤3，若不是，步骤4.
 * 3.寻找P中下一个集合，执行步骤2，若所有集合均是子集，则步骤5.
 * 4.将I1划分成P中某几个集合子集的形式，将I1划分后的集合加入P，并删除I。执行步骤3
 * 5.用P中的每一集合的第一个元素代替集合，形成新的DFA。
 *
 * @author Mind
 * @version 1.0
 */
public class ODFA {
    //未优化DFA的图
    private List<DFaNode> DFaNodes;
    //所有的终结符边
    private List<Integer> finalityArray;

    public ODFA(List<DFaNode> oldDFaNode, List<Integer> finalityArray) throws DFaException {
        this.DFaNodes=oldDFaNode;
        for (int i=0;i<oldDFaNode.size();++i){
            //为了测试打印，同时为table的构建初始化使用
            oldDFaNode.get(i).isVisit=false;
        }
        this.finalityArray=finalityArray;
        optimizeDFA();
    }

    /**
     * 内部类，表达一个Node的集合
     */
    private class NodeSet{
        Set<DFaNode> set=new HashSet<>(10);

        private void addNode(DFaNode dFaNode){
           set.add(dFaNode);

        }

        //判断nodeSet是否完全相等
        private boolean isEqual(NodeSet nodeSet){
            if (nodeSet.set.size()!=set.size())
                return false;

            Iterator<DFaNode> iterator=nodeSet.set.iterator();
            while (iterator.hasNext()) {
                if(!set.contains(iterator.next()))
                    return false;
            }

            return true;
        }
    }

    /**
     * DFA的优化具体过程
     */
    private void optimizeDFA() throws DFaException {
        List<NodeSet> allSet=new ArrayList<>();
        //首先将旧的队列，分成两个集合，一个是终结符另一个是不包括终结符的集合
        NodeSet finalSet=new NodeSet(),nofinalSet=new NodeSet();
        for(DFaNode contentNode:DFaNodes) {
            if (!contentNode.isIncludeEnd)
                nofinalSet.addNode(contentNode);
            else {
                NodeSet nodeSet=new NodeSet();
                nodeSet.set.add(contentNode);
                allSet.add(nodeSet);
            }
        }
        //这里将所有终结符拆分,等到非终结符部分优化完成再加入，是因为优化结果处理没有考虑明白，详细请见龙书116的例3.41处理问题方式
        allSet.add(nofinalSet);

        //标记是否所有的nodeset都是无法再分割的了,当该值等于所有子集数时表示完成
        int pos=0;
        Out:
        while (pos!=allSet.size()){
            //每次处理一个子集，完成度加一，但是如果这个子集分裂出多个子集，则重置完成度，因为此时需要重新审定
            //对于一个子集的判断需要判断所有的终结符的边
            if (allSet.get(pos).set.size()==1){
                pos++;
                continue;
            }

            FindISetNextEdge:
            for(int finalEdge=0;finalEdge<finalityArray.size();++finalEdge){
                Iterator<DFaNode> k=allSet.get(pos).set.iterator();

                //第一步，找到某一终结符边的下一个集合，无该边也不加入
                NodeSet oneEdgeSet=new NodeSet();
                FindInext:
                while (k.hasNext()){
                    DFaNode currentNode=k.next();
                    for(int i=0;i<currentNode.getEdge().size();++i){
                        if(currentNode.getEdge().get(i).equals(finalityArray.get(finalEdge))) {
                            oneEdgeSet.addNode(currentNode.getDfaNodes().get(i));
                            //因为每一个结点的一条有效终结符边只能有一个结果，当找到了就直接开始下一个结点的寻找
                            continue FindInext;
                        }
                    }
                }

                //第二步，判断该终结符边得到的集合是否是已存在的集合
                boolean isIncluded=false;
                for (NodeSet setInAll:allSet){
                    if (setInAll.isEqual(oneEdgeSet)){
                        isIncluded=true;
                    }
                }

                //第三步，如果是需要分离的集合，进行集合的拆解，然后加入原来的总集合，进行一步一回头
                if (isIncluded)
                    continue FindISetNextEdge;
                else {//需要将这个NodeSet进行不同集合划分成几个集合的形式,处理完成后进行下一个集合的判断，不再进行下一个终结符边的操作
                    NodeSet[] nodeSets=new NodeSet[allSet.size()+1];
                    for (DFaNode I:allSet.get(pos).set){
                        boolean isHaveThisEdge=false;
                        FindwitchSet:
                        for(int i=0;i<I.getEdge().size();++i){
                            if(I.getEdge().get(i).equals(finalityArray.get(finalEdge))) {
                                isHaveThisEdge=true;
                                //table定位
                                for(int indexOfSet=0;indexOfSet<allSet.size();++indexOfSet){
                                    if(allSet.get(indexOfSet).set.contains(I.getDfaNodes().get(i))){
                                        if(nodeSets[indexOfSet]==null)
                                            nodeSets[indexOfSet]=new NodeSet();
                                        nodeSets[indexOfSet].set.add(I);
                                        break FindwitchSet;
                                    }
                                }
                            }
                        }
                        //不存在该边则加入新的集合
                        if (!isHaveThisEdge){
                            if (nodeSets[allSet.size()]==null)
                                nodeSets[allSet.size()]=new NodeSet();
                            nodeSets[allSet.size()].set.add(I);
                        }

                    }
                    for (NodeSet validSet:nodeSets)
                        if(validSet!=null)
                            allSet.add(validSet);
                    //删除当前处理的集合，因为它已经被拆分了
                    allSet.remove(pos);
                    //重置当前处理完成度
                    pos=0;
                    continue Out;
                }
            }
            pos++;
        }


        //对于每一个集合，只保留第一个dfaNode,注意此时应该重置集合内边的关系
        for (NodeSet currentSet:allSet){
            Iterator<DFaNode> iterator=currentSet.set.iterator();
            DFaNode represent=iterator.next();
            if (represent.getDfaNodes()!=null)
            for (int i=0;i<represent.getDfaNodes().size();++i){
                DFaNode nextNode=represent.getDfaNodes().get(i);
                for (NodeSet whichSet:allSet){
                    if (whichSet.set.contains(nextNode)){
                        represent.getDfaNodes().set(i,whichSet.set.iterator().next());
                        break;
                    }
                }
            }
        }
        //删除保留结点除外的结点，进行结点的回收
        for (NodeSet currentSet:allSet) {
            Iterator<DFaNode> iterator = currentSet.set.iterator();
            DFaNode represent=iterator.next();
            if (represent.getDfaNodes()!=null)
            while (iterator.hasNext()) {
                DFaNode deleteNode = iterator.next();
                //同时更新当前endlevel，如果集合中存在end等级更高的替换当前代表结点的等级
                //if (deleteNode.getEndLevel() < represent.getEndLevel())
                //    represent.setEndLevel(deleteNode.getEndLevel());
                DFaNodes.remove(deleteNode);
                AllManager.dFaNodeManager.deleteNfaNode(deleteNode);
            }
        }
    }

    public DFaNode getHead(){
        return DFaNodes.get(0);
    }

}
