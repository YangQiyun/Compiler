package ODFA;

import DFA.DFaNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

public class Table {

    private DFaNode headNode;
    //使用Integer是为了当不存在的时候默认是没有内存占用的，以便节省空间
    public Integer[][] table=new Integer[128][500];
    public Integer[] isEndNode=new Integer[500];

    public Table(DFaNode headNode){
        this.headNode=headNode;
        init();
    }

    //初始化表
    private void init() {
        Queue<DFaNode> queue=new LinkedTransferQueue<>();
        headNode.isVisit=true;

        queue.add(headNode);
        while (!queue.isEmpty()){
            DFaNode dFaNode=queue.poll();
            if (dFaNode.isIncludeEnd)
                isEndNode[dFaNode.getIdentification()]=dFaNode.getEndLevel();
            if (dFaNode.getEdge()!=null)
            for (int i=0;i<dFaNode.getEdge().size();++i){
                table[dFaNode.getEdge().get(i)][dFaNode.getIdentification()]=dFaNode.getDfaNodes().get(i).getIdentification();
                if(!dFaNode.getDfaNodes().get(i).isVisit){
                    queue.add(dFaNode.getDfaNodes().get(i));
                    dFaNode.getDfaNodes().get(i).isVisit=true;
                }
            }
        }
    }

    public Integer[][] getTable() {
        return table;
    }

    public Integer[] getIsEndNode() {
        return isEndNode;
    }

    public  int getStartNum(){
        return  headNode.getIdentification();
    }
}
