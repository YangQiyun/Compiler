package NFA;

import Lex.AllManager;
import Node.*;
import mException.NFaException;
import java.util.Stack;

/**
 * NFA 类
 * 运用Thompson根据已有的抽象语法树构建一个完整的NFA
 * 还有优化空间 对于关系闭包的相等判断
 *
 * @author Mind
 * @version 1.0
 */
public class NFA {


    private NFaPair resultPair;


    private Stack<Node> abstractTree=new Stack<>();
    private Stack<NFaPair> pairStack=new Stack<>();

    public NFA(Node headNode) throws Exception {
        getNFA(headNode);
    }

    /**
     * 内部函数，对于一颗抽象语法树构建出完整的NFA图，结果是得到NFApair，它包括了start和end
     * 实现思路：
     * 深度优先遍历抽象语法树，当某一结点的孩纸（如果存在）都构成了NFApair，构建自身的NFApair
     * 当某一结点自身没有孩纸，理论上是字符，那么直接构建NFApair
     * 需要两个堆栈进行维护，abstractTree是进行维护深度优先遍历
     * pairStack维护构建完成的NFApair
     * 正常结束的结果是：abstarctTree栈为空，pairStack栈只剩下最后的一个结果pair
     *
     * @param  headNode 抽象语法树的结点
     * @throws NFaException 根据逻辑需要抛出的错误，方便以后的定位修改
     */
    private void  getNFA(Node headNode) throws NFaException{

        abstractTree.push(headNode);
        Node regexNode;
        while (!abstractTree.isEmpty()){
            regexNode=abstractTree.peek();
            if(regexNode.isLeftExist()){//左孩纸是否存在
                if(!regexNode.getLeftNode().isVisit()) {//判断左孩纸是否构成NFA
                    abstractTree.push(regexNode.getLeftNode());
                    continue;
                }else {//左孩纸已构成NFA,判断右孩纸是否存在
                    if(regexNode.isRightExist()){//右孩纸存在
                        if(!regexNode.getRightNode().isVisit()){//右孩纸未构成NFA结点
                            abstractTree.push(regexNode.getRightNode());
                            continue;
                        }
                        else {//左右孩纸都是NFA，可以构造自身成为NFA结点
                            //判断当前是什么，生成NFA结点(需要的结点从nfa栈中获取)，并且设regexNode为visited，将NFA压栈,将regexNode弹出
                            pairStack.push(createNFApair(regexNode));
                            regexNode.setVisit(true);
                            abstractTree.pop();
                        }
                    }else {//左孩纸已构成NFA，但无右孩纸
                        //当前只能是单目运算符，如果不是则报错，生成NFA结点(需要的结点从nfa栈中获取)，并且设regexNode为visited，将NFA压栈,将regexNode弹出
                        if(!(regexNode instanceof ClassifierNode))
                            throw new NFaException("不是要求的单目运算符");
                        pairStack.push(createNFApair(regexNode));
                        regexNode.setVisit(true);
                        abstractTree.pop();
                    }
                }
            }else {//左孩纸不存在，自身直接构造成NFA结点
                //判断当前是什么，生成NFA结点，并且设regexNode为visited，将NFA压栈,将regexNode弹出
                pairStack.push(createNFApair(regexNode));
                regexNode.setVisit(true);
                abstractTree.pop();
            }
        }

        if(pairStack.size()==1)
            resultPair=pairStack.pop();
        else
            throw new NFaException("构造NFA错误");
    }

    /**
     * 获取生成的最终结果
     *
     * @return 返回最终NFApair结点
     */
    public NFaPair getResultPair() {
        return resultPair;
    }


    /**
     * 内部函数，生成NFApair结点，
     * 实现思路：
     * 判断抽象语法树的结点进行对应类型的pair生成
     * 如果是字符结点，直接创建新的pair结点
     * 如果是运算符结点，根据需要从pairStack中拿出结点进行拼接和构成
     * 具体的拼接思路看文档
     *
     * @param  node 抽象语法树的结点
     * @return 返回生成NFApair结点
     * @throws NFaException 根据逻辑需要抛出的错误，方便以后的定位修改
     */
    private NFaPair createNFApair(Node node) throws NFaException {
        NFaPair newPair=new NFaPair();
        if(node instanceof CharacterNode){
            newPair.getStart().add(node.getValue(),newPair.getEnd());
        }
        else if(node instanceof ClassifierNode){
            if (pairStack.isEmpty())
                throw new NFaException("单目运算符前无可用pair");
            NFaPair frontPair=pairStack.pop();
            if(((ClassifierNode) node).getClassType()== ClassifierNode.CTYPE.ADD){
                newPair.getStart().addEpsilon(frontPair.getStart());
                frontPair.getEnd().addEpsilon(frontPair.getStart());
                frontPair.getEnd().addEpsilon(newPair.getEnd());
            }
            if(((ClassifierNode) node).getClassType()== ClassifierNode.CTYPE.START){
                newPair.getStart().addEpsilon(frontPair.getStart());
                frontPair.getEnd().addEpsilon(frontPair.getStart());
                frontPair.getEnd().addEpsilon(newPair.getEnd());
                newPair.getStart().addEpsilon(newPair.getEnd());
            }
            if(((ClassifierNode) node).getClassType()== ClassifierNode.CTYPE.QUESTION){
                newPair.getStart().addEpsilon(frontPair.getStart());
                frontPair.getEnd().addEpsilon(newPair.getEnd());
                newPair.getStart().addEpsilon(newPair.getEnd());
            }
        }
        else if(node instanceof OrNode){
            NFaPair OnePair=pairStack.pop(),TwoPair=pairStack.pop();
            newPair.getStart().addEpsilon(OnePair.getStart());
            newPair.getStart().addEpsilon(TwoPair.getStart());
            OnePair.getEnd().addEpsilon(newPair.getEnd());
            TwoPair.getEnd().addEpsilon(newPair.getEnd());
        }
        else if(node instanceof CatNode){
            NFaPair rightPair=pairStack.pop(),leftPair=pairStack.pop();
            AllManager.nFaNodeManager.deleteNfaNode(newPair.getEnd());
            AllManager.nFaNodeManager.deleteNfaNode(newPair.getStart());
            newPair.setStart(leftPair.getStart());
            newPair.setEnd(rightPair.getEnd());
            leftPair.getEnd().addEpsilon(rightPair.getStart());
        }
        else
            throw new NFaException("no this stituation");
        return  newPair;
    }


    //设置当前NFA的endlevel等级
    public void SetEndLevel(int level){
        this.resultPair.setEndLevel(level);
    }
}
