package DFA;



import mException.DFaException;

import java.util.Stack;

public class DFaNodeManager {
    private int DFA_MAX =256;
    private DFaNode[] newDfaArray =null;
    private Stack<DFaNode> oldDFaStack =null;
    //下一个分配的全新的结点的索引
    private int pos= DFA_MAX -1;


    public DFaNodeManager() throws Exception {
        newDfaArray =new DFaNode[DFA_MAX];
        for(int i = 0; i< DFA_MAX; ++i)
            newDfaArray[i]=new DFaNode();

        oldDFaStack =new Stack<>();

        if(newDfaArray ==null|| oldDFaStack ==null)
            throw new DFaException("lack of memory");
    }

    /**
     * 在结点管理中获取一个新的结点，如果栈中有旧的就先用旧的，没有就从数组中获取
     * 如果数组中不够了，再扩展两倍的数组，申请一批新的结点
     *
     * @return 返回新的NFaNode结点
     */
    public DFaNode newNfaNode() throws DFaException {

        if(pos==0){
            DFA_MAX = DFA_MAX << 1;
            DFA_MAX = DFA_MAX >Integer.MAX_VALUE?Integer.MAX_VALUE: DFA_MAX;
            newDfaArray =new DFaNode[DFA_MAX];
            if(newDfaArray ==null)
                throw new DFaException("lack of memory");
            for (int i = 0; i< DFA_MAX; ++i)
                newDfaArray[i]=new DFaNode();
            pos= DFA_MAX -1;
        }

        DFaNode dFaNode;

        if(!oldDFaStack.isEmpty()){
            dFaNode= oldDFaStack.pop();
            dFaNode.clearState();
        }else{
            dFaNode= newDfaArray[pos--];
        }
        return dFaNode;
    }

    /**
     * 不需要的结点放到专门存用过的可以再利用的堆栈中
     *
     * @param  dFaNode 不需要的结点
     */
    public void deleteNfaNode(DFaNode dFaNode){
        oldDFaStack.push(dFaNode);
    }
}
