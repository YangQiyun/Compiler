package Regex;


import DFA.DFA;
import Lex.Scan;
import NFA.*;
import ODFA.ODFA;
import mException.ShuntingException;
import Lex.AllManager;
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

        while (pos!=infixRegex.length()){

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

    public static void main(String args[]){
        //(a|b)*abb(a|b)*
        //((c|a)b*)*
        //(a*|b*)*
        //(a|b)*abb
        try {
/*
        NormalRegex normalRegex=new NormalRegex("(a|b)*abb");
        //normalRegex.print();
       // System.out.println();
        Shunting_yard shunting_yard= null;

            shunting_yard = new Shunting_yard(normalRegex.getNodeList(), AllManager.operatorManager);
            shunting_yard.print();
            System.out.println(" tree");
            abstractGrammarTree abstractGrammarTree=new abstractGrammarTree(shunting_yard.getResult());
            abstractGrammarTree.WidthPrint();
            NFA nfa=new NFA(abstractGrammarTree.getHeadNode());
            //nfa.getResultPair().print();
            DFA dfa=new DFA(nfa.getResultPair());
            //dfa.print();
            ODFA odfa=new ODFA(dfa.getdFaNodes(),dfa.getFinalityArray());
            odfa.print();
*/
            //表达式一
            NormalRegex normalRegex1=new NormalRegex("if|(0|1|2|3|4|5|6|7|8|9)*|orp");
            Shunting_yard shunting_yard1=new Shunting_yard(normalRegex1.getNodeList(),AllManager.operatorManager);
            abstractGrammarTree abstractGrammarTree1=new abstractGrammarTree(shunting_yard1.getResult());
            NFA nfa1=new NFA(abstractGrammarTree1.getHeadNode());
            nfa1.SetEndLevel(1);


            //表达式二
            NormalRegex normalRegex2=new NormalRegex("or|abo");
            Shunting_yard shunting_yard2=new Shunting_yard(normalRegex2.getNodeList(),AllManager.operatorManager);
            abstractGrammarTree abstractGrammarTree2=new abstractGrammarTree(shunting_yard2.getResult());
            NFA nfa2=new NFA(abstractGrammarTree2.getHeadNode());
            nfa2.SetEndLevel(2);

            //表达式三
            NormalRegex normalRegex3=new NormalRegex("(a|c|b|f|i|o|r)*f");
            Shunting_yard shunting_yard3=new Shunting_yard(normalRegex3.getNodeList(),AllManager.operatorManager);
            abstractGrammarTree abstractGrammarTree3=new abstractGrammarTree(shunting_yard3.getResult());
            NFA nfa3=new NFA(abstractGrammarTree3.getHeadNode());
            nfa3.SetEndLevel(3);

            NFaPair[] nFaPairs={nfa1.getResultPair(),nfa2.getResultPair(),nfa3.getResultPair()};

            DFA dfa1=new DFA(nFaPairs);
           // dfa1.print();

            //ODFA odfa1=new ODFA(dfa1.getdFaNodes(),dfa1.getFinalityArray());

            Scan scan=new Scan(dfa1.getdFaNodes().get(0));
            scan.setValue("orp");
            //odfa1.print();
        } catch (ShuntingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
