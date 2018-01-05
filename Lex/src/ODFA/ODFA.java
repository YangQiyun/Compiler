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
 * 实验算法：

 1，  对于DFA的字母表M，把M划分成终态集和非终态集，令P=M。

 2，  对于P中的一个集合I，寻找I每一个元素K，找到K从边a对应的节点，加入集合I1，若I1是P中某个集合的子集，跳至步骤3，若不是，步骤4.

 3，  寻找P中下一个集合，执行步骤2，若所有集合均是子集，则步骤5.[

 4，  将I1划分成P中某几个集合子集的形式，将I1划分后的集合加入P，并删除I。执行步骤3

 5，  用P中的每一集合的第一个元素代替集合，形成新的DFA。
 */


public class ODFA {

    private List<DFaNode> DFaNodes;
    private List<Integer> finalityArray;

    public ODFA(List<DFaNode> oldDFaNode, List<Integer> finalityArray) throws DFaException {
        this.DFaNodes=oldDFaNode;
        for (int i=0;i<oldDFaNode.size();++i){
            //为了测试打印使用
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

        //判断param的结点内所有点都在本nodeset中
        private boolean isInclude(NodeSet nodeSet){
            if (nodeSet.set.size()>set.size())
                return false;

            Iterator<DFaNode> iterator=nodeSet.set.iterator();
            while (iterator.hasNext()) {
                if(!set.contains(iterator.next()))
                    return false;
            }

            return true;
        }
    }

    private void optimizeDFA() throws DFaException {
        List<NodeSet> allSet=new ArrayList<>();
        //首先将旧的队列，分成两个集合，一个是终结符另一个是不包括终结符的集合
        NodeSet finalSet=new NodeSet(),nofinalSet=new NodeSet();
        for(DFaNode contentNode:DFaNodes)
            if (!contentNode.isIncludeEnd)
                nofinalSet.addNode(contentNode);
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
                NodeSet oneEdgeSet=new NodeSet();

                //因为是临时变量，让GC自动回收就够了,用来处理没有对应边时，指向该点，使得结果集是一个全新不存在的
                DFaNode tempNode=new DFaNode();
                FindInext:
                while (k.hasNext()){
                    DFaNode currentNode=k.next();
                    //是否存在这条边
                    boolean isHaveThisEdge=false;

                    for(int i=0;i<currentNode.getEdge().size();++i){
                        if(currentNode.getEdge().get(i).equals(finalityArray.get(finalEdge))) {
                            oneEdgeSet.addNode(currentNode.getDfaNodes().get(i));
                            //因为每一个结点的一条有效终结符边只能有一个结果，当找到了就直接开始下一个结点的寻找
                            isHaveThisEdge=true;
                            continue FindInext;
                        }
                    }


                    if (!isHaveThisEdge)
                        oneEdgeSet.addNode(tempNode);

                }

                //判断该NodeSet是否存在在一个完整结点中
                boolean isIncluded=false;
                for (NodeSet nodeSet:allSet)
                    if (nodeSet.isInclude(oneEdgeSet)) {
                        isIncluded = true;
                        break ;
                    }

                //如果该nodeSet全都是构造新的结点则跳过
                int num=0;
                for (DFaNode dFaNode:oneEdgeSet.set){
                    if (dFaNode==tempNode)
                        num++;
                }
                if (num==oneEdgeSet.set.size())
                    break FindISetNextEdge;


                /*
                FindIsIncluded:
                for (DFaNode dFaNode:oneEdgeSet.set){
                    //假设只有一个无边结点
                    if (dFaNode==tempNode){
                        isIncluded=-2;
                        break FindIsIncluded;
                    }
                    for (int i=0;i<allSet.size();++i) {
                        if (allSet.get(i).set.contains(dFaNode)) {
                            if (isIncluded == -1)
                                isIncluded = i;
                            else if (isIncluded != i) {
                                isIncluded = -2;
                                break FindIsIncluded;
                            }
                        }
                    }
                }
                //如果全都是无该边则不用拆分
                int num=0;
                for (DFaNode dFaNode:oneEdgeSet.set){
                    if (dFaNode==tempNode)
                        num++;
                }
                if (num==oneEdgeSet.set.size())
                    break FindISetNextEdge;

                */

                if (isIncluded)
                    continue FindISetNextEdge;
                else {//需要将这个NodeSet进行不同集合划分成几个集合的形式
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

        //非终结符部分优化完成
        for(DFaNode contentNode:DFaNodes)
            if (!contentNode.isIncludeEnd){
            NodeSet nodeSet=new NodeSet();
            nodeSet.set.add(contentNode);
            allSet.add(nodeSet);
            }

        //对于每一个集合，只保留第一个dfaNode,注意此时应该重置集合内边的关系
        for (NodeSet currentSet:allSet){
            //只有一个就跳过
            if(currentSet.set.size()==1)
                continue;
            Iterator<DFaNode> iterator=currentSet.set.iterator();
            DFaNode represent=iterator.next();
            //将代表点的指向集合内的其他点的全指向自己
            for (int i=0;i<represent.getDfaNodes().size();++i)
                if(currentSet.set.contains(represent.getDfaNodes().get(i)))
                    represent.getDfaNodes().set(i,represent);
            //将指向集合内其他点的node指向自己
            for (DFaNode anotherNode:DFaNodes){
                for (int i=0;i<anotherNode.getDfaNodes().size();++i)
                    if(currentSet.set.contains(anotherNode.getDfaNodes().get(i)))
                        anotherNode.getDfaNodes().set(i,represent);
            }
            //删除保留结点除外的结点
            while (iterator.hasNext()){
                DFaNode deleteNode=iterator.next();
                //同时更新当前endlevel，如果集合中存在end等级更高的替换当前代表结点的等级
                if (deleteNode.getEndLevel()<represent.getEndLevel())
                    represent.setEndLevel(deleteNode.getEndLevel());
                DFaNodes.remove(deleteNode);
                AllManager.dFaNodeManager.deleteNfaNode(deleteNode);
            }

        }
    }


    //测试打印使用
    public void print(){
        DFaNode head=DFaNodes.get(0);
        Queue<DFaNode> queue=new LinkedTransferQueue<>();
        head.isVisit=true;

        queue.add(head);
        while (!queue.isEmpty()){
            DFaNode dFaNode=queue.poll();
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
