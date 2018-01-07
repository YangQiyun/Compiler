package Regex;


import Node.*;

import java.util.ArrayList;
import java.util.List;

/**
 * NormalRegex 类
 * 预处理正则表达式，主要包括添加缺失的cat结点，转义字符（只支持转义成字符，不具备功能性的普通字符符号)
 *
 * @author Mind
 * @version 1.0
 */

public class NormalRegex {

    private String infixRegex;



    private List<Node> nodeList;

    public NormalRegex(){}


    /**
     * 构造一个新的NormalRegex类.
     *
     * @param infixRegex  需要进行预处理的中缀形式的正则表达式
     */
    public NormalRegex(String infixRegex){
        this.infixRegex=infixRegex;

        nodeList=new ArrayList<>(10);
        normalization();
    }

    /**
     * 添加缺失的cat结点，转义字符（只支持转义成字符，不具备功能性的普通字符符号)
     * 目前的正则表达式里面符号有 ( ) * + ? | 字符
     * 需要考虑在前面加上cat的符号有 ( 字符
     * 在符号后面考虑加上cat的有 ) * + ? 字符
     *
     */
    private void normalization(){
        int pos=0;
        Character content;
        boolean isCouldCat=false;

        while (pos<infixRegex.length()){

            content=infixRegex.charAt(pos++);

            switch (content){
                case '.':
                    isCouldCat=true;
                    break;
                case ' ':
                    pos++;
                    break;
                case '|':
                    nodeList.add(new OrNode());
                    isCouldCat=false;
                    break;
                case '*':
                    nodeList.add(new ClassifierNode(ClassifierNode.CTYPE.START));
                    isCouldCat=true;
                    break;
                case '+':
                    nodeList.add(new ClassifierNode(ClassifierNode.CTYPE.ADD));
                    isCouldCat=true;
                    break;
                case '?':
                    nodeList.add(new ClassifierNode(ClassifierNode.CTYPE.QUESTION));
                    isCouldCat=true;
                    break;
                case '(':
                    if (isCouldCat)
                        nodeList.add(new CatNode());
                    nodeList.add(new LeftBracket());
                    isCouldCat=false;
                    break;
                case ')':
                    nodeList.add(new RightBracket());
                    isCouldCat=true;
                    break;
                    default://其他的字符,包括了转义字符
                        if(isCouldCat)
                            nodeList.add(new CatNode());
                        if(content=='\\')
                           content=infixRegex.charAt(pos++);
                        nodeList.add(new CharacterNode(content.toString()));
                        isCouldCat=true;
                        break;
            }
        }
    }

    public void print(){
        for(Node node:nodeList)
          System.out.print(node);
    }

    public List<Node> getNodeList() {
        return nodeList;
    }


}
