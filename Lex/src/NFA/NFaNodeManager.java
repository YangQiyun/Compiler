package NFA;

import mException.NFaException;

import java.util.Stack;

/**
 * 用于对普通NFA连接结点的申请和管理，定义了最大的管理结点数，申请新的结点
 * 时优先从使用过的结点中获取，再从未使用过的结点中获取
 *
 * @author Mind
 * @version 1.0
 */

public class NFaNodeManager {

    private int NFA_MAX=256;
    private NFaNode[] newNfaArray=null;
    private Stack<NFaNode> oldNFaStack=null;
    //下一个分配的全新的结点的索引
    private int pos=NFA_MAX-1;


    public NFaNodeManager() throws Exception {
        newNfaArray=new NFaNode[NFA_MAX];
        for(int i=0;i<NFA_MAX;++i)
            newNfaArray[i]=new NFaNode();

        oldNFaStack=new Stack<>();

        if(newNfaArray==null||oldNFaStack==null)
            throw new NFaException("lack of memory");
    }

    /**
     * 在结点管理中获取一个新的结点，如果栈中有旧的就先用旧的，没有就从数组中获取
     * 如果数组中不够了，再扩展两倍的数组，申请一批新的结点
     *
     * @return 返回新的NFaNode结点
     */
    public NFaNode newNfaNode() throws NFaException {

         if(pos==0){
             NFA_MAX=NFA_MAX << 1;
             NFA_MAX=NFA_MAX>Integer.MAX_VALUE?Integer.MAX_VALUE:NFA_MAX;
             newNfaArray=new NFaNode[NFA_MAX];
             if(newNfaArray==null)
                 throw new NFaException("lack of memory");
             for (int i=0;i<NFA_MAX;++i)
                 newNfaArray[i]=new NFaNode();
             pos=NFA_MAX-1;
         }

        NFaNode nFaNode;

        if(!oldNFaStack.isEmpty()){
            nFaNode=oldNFaStack.pop();
            nFaNode.clearState();
        }else{
            nFaNode=newNfaArray[pos--];
        }
        return nFaNode;
    }

    /**
     * 不需要的结点放到专门存用过的可以再利用的堆栈中
     *
     * @param  nFaNode 不需要的结点
     */
    public void deleteNfaNode(NFaNode nFaNode){
        oldNFaStack.push(nFaNode);
    }
}
