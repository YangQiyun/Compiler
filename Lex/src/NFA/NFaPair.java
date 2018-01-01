package NFA;

import Lex.AllManager;
import mException.NFaException;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * 代表了一个NFA状态机，它包括了start和end
 *
 * @author Mind
 * @version 1.0
 */
public class NFaPair {

    private NFaNode start;
    private NFaNode end;

    public NFaPair() throws NFaException {
        setStart(AllManager.nFaNodeManager.newNfaNode());
        setEnd(AllManager.nFaNodeManager.newNfaNode());
    }

    public NFaNode getStart() {
        return start;
    }

    public void setStart(NFaNode start) {
        this.start = start;
    }

    public NFaNode getEnd() {
        return end;
    }

    public void setEnd(NFaNode end) {
        this.end = end;
    }



    /**
     * 测试用的函数，广度优先打印出NFA的连接情况，如：
     * [ 253 --￿--252 ]
     * [ 251 --b--250 ]
     * [ 248 --￿--246 ]
     * [ 255 --a--254 ]
     * [ 252 --￿--246 ]
     *
     */
    public void print() {
        Queue<NFaNode> queue=new LinkedTransferQueue<>();
        start.isVisit=true;
        queue.add(start);
        NFaNode node;
        while (!queue.isEmpty()){
            node=queue.poll();
            if(node.edgeSize()!=0){
                for (int i=0;i<node.edgeSize();i++) {
                    System.out.println("[ " + node.getIdentification() + " --" + (char)(int)node.getEdge().get(i)+"--"+node.getNfaNodes().get(i).getIdentification()+" ]");
                    if(!node.getNfaNodes().get(i).isVisit){
                        queue.add(node.getNfaNodes().get(i));
                        node.getNfaNodes().get(i).isVisit=true;
                    }

                }
            }
        }
    }
}
