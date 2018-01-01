package Regex;

import Node.*;
import mException.ShuntingException;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;



/**
 * 运用调度场算法将中缀正则表达式转成后缀表达式，去除括号
 *
 * @author Mind
 * @version 1.0
 */

public class Shunting_yard {
    private List<Node> nodeList;
    private OperatorManager operatorManager;
    private List<Node> result;

    public Shunting_yard(List<Node> nodeList,OperatorManager operatorManager) throws ShuntingException {
        this.operatorManager=operatorManager;
        this.nodeList=nodeList;
        result();
    }
    /*
    * https://zh.wikipedia.org/wiki/%E8%B0%83%E5%BA%A6%E5%9C%BA%E7%AE%97%E6%B3%95
    * 算法流程：
    * 当还有记号可以读取时：
    * 读取一个记号。
    * 如果这个记号表示一个字符，那么将其添加到输出队列中。
    * 如果这个记号表示一个操作符，记做o1，那么：
    * 只要存在另一个记为o2的操作符位于栈的顶端，并且
    * 如果o1是左结合性的并且它的运算符优先级要小于或者等于o2的优先级
    * 将o2从栈的顶端弹出并且放入输出队列中（循环直至以上条件不满足为止）；
    * 然后，将o1压入栈的顶端。
    * 如果这个记号是一个左括号，那么就将其压入栈当中。
    * 如果这个记号是一个右括号，那么：
    * 从栈当中不断地弹出操作符并且放入输出队列中，直到栈顶部的元素为左括号为止。
    * 将左括号从栈的顶端弹出，但并不放入输出队列中去。
    * 如果此时位于栈顶端的记号表示一个函数，那么将其弹出并放入输出队列中去。
    * 如果在找到一个左括号之前栈就已经弹出了所有元素，那么就表示在表达式中存在不匹配的括号。
    * 当再没有记号可以读取时：
    * 如果此时在栈当中还有操作符：
    * 如果此时位于栈顶端的操作符是一个括号，那么就表示在表达式中存在不匹配的括号。
    * 将 操作符逐个弹出并放入输出队列中。
    * 退出算法。
    * */
    private void result() throws ShuntingException {
        ListIterator<Node> nodeIterable=nodeList.listIterator();
        Node node;
        Stack<Node> output=new Stack<>();
        Stack<Node> operator=new Stack<>();
        while (nodeIterable.hasNext()){
            node=nodeIterable.next();
            if(node instanceof CharacterNode){
                output.push(node);
            }
            if(node instanceof CatNode||node instanceof ClassifierNode||node instanceof OrNode){
                while (!operator.empty()){
                    //当前所有操作符均为左结合性质
                    Node o2=operator.peek();
                    if (!operatorManager.isLeftBigger(node.getValue(),o2.getValue())){
                        output.push(operator.pop());
                    }
                    else
                        break;
                }
                operator.push(node);
            }
            if(node instanceof LeftBracket){
                operator.push(node);
            }
            if(node instanceof RightBracket){
                Node tempNode;
                while (true){
                    if(operator.empty())
                        throw  new ShuntingException("存在不匹配括号");
                    tempNode=operator.pop();
                    if(tempNode instanceof LeftBracket)
                        break;
                    else
                        output.push(tempNode);
                }
            }
        }

        //没有记号可读的时候
        if(!operator.empty()){
            Node tempNode;
            while (!operator.empty()){
              tempNode=operator.pop();
              if(tempNode instanceof LeftBracket||tempNode instanceof RightBracket)
                  throw new ShuntingException("存在不匹配括号");
              else
                  output.push(tempNode);
            }
        }
        result=new ArrayList<>();
        Stack<Node> tempStack=new Stack<>();
        while (!output.empty())
            tempStack.push(output.pop());
        while (!tempStack.empty())
            result.add(tempStack.pop());

    }

    public List<Node> getResult() {
        return result;
    }

    public void print(){
        ListIterator<Node> nodeListIterator=result.listIterator();
        Node node;
        while (nodeListIterator.hasNext()){
            node=nodeListIterator.next();
            System.out.print(node.getValue());
        }
    }



}
