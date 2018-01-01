package Regex;

import Node.*;

import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedTransferQueue;

/**
 * abstractGrammarTree 类
 * 抽象语法树的构建，根据后缀正则表达式进行生成
 *
 * @author Mind
 * @version 1.0
 */
public class abstractGrammarTree {

    private List<Node> nodeList;

    private Node headNode;

    public abstractGrammarTree(List<Node> nodeList) throws Exception {
        this.nodeList=nodeList;
        createTree();
    }

    public void createTree() throws Exception {
        ListIterator<Node> nodeListIterator=nodeList.listIterator();
        Stack<Node> stack=new Stack<>();
        Node currentNode=null,node1=null,node2=null;
        while (nodeListIterator.hasNext()){
            currentNode=nodeListIterator.next();
            if(currentNode instanceof CharacterNode){
               stack.push(currentNode);
            }
            if(currentNode instanceof CatNode||currentNode instanceof OrNode){
                node2=stack.pop();
                node1=stack.pop();
                currentNode.setLeftNode(node1);
                currentNode.setRightNode(node2);
                stack.push(currentNode);
            }
            if(currentNode instanceof ClassifierNode){
                node1=stack.pop();
                currentNode.setLeftNode(node1);
                stack.push(currentNode);
            }
        }

        headNode=stack.pop();
        if(!stack.isEmpty())
            throw new Exception("构造树还有两结点");
    }

    public Node getHeadNode() {
        return headNode;
    }

    public void WidthPrint(){
       //广度遍历语法树进行查看
        Queue<Node> queue=new LinkedTransferQueue<>();
        queue.add(headNode);
        Node node;
        while (!queue.isEmpty()){
            node=queue.poll();
            System.out.println("[ "+node.getValue()+" , "+node.getLeftNode()+" , "+node.getRightNode()+" ]");
            if(node.isLeftExist())
                queue.add(node.getLeftNode());
            if(node.isRightExist())
                queue.add(node.getRightNode());
        }
    }


}
