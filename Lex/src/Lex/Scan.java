package Lex;

import DFA.DFA;
import NFA.NFA;
import NFA.NFaPair;
import ODFA.*;
import Regex.NormalRegex;
import Regex.Shunting_yard;
import Regex.abstractGrammarTree;

import java.io.*;
import java.util.ArrayList;

/**
 * Scan 类
 * 进行状态表的管理还有一系列的初始化操作，包括了获取正则表达式的所有内容
 * 生成对应的状态表
 *
 *
 * @author Mind
 * @version 1.0
 */
public class Scan {
    //一个总的状态图
    private Table Table;
    private Integer[][] table;
    private Integer[] isEndNode;
    private String[] EndMean=new String[100];

    private int startNum;

    public Scan() throws Exception {
         init();

    }


    /**
     * 获取正则表达式的文件，初始化构建一个状态表
     */
    public void init() throws Exception {
        ArrayList<NFaPair> nFaPairs=new ArrayList<>();
        int level=0;
        File file=new File("Rex.txt");
        LineNumberReader reader=new LineNumberReader (new InputStreamReader(new DataInputStream(new FileInputStream(file))));
        String content=reader.readLine();
        //文件头带有文件信息的一个字符去掉
        content=content.substring(1);

        while (content!=null){
            String[] getString=content.split("(\\n|\\t| )+");
            getString[1].replace("\\n","");
            getString[1].replace("\\t","");
            getString[1].replace(" ","");
            nFaPairs.add(addNewRex(getString[0],getString[1],level++));
            content=reader.readLine();
        }
/*
        NFaPair nFaPair1=addNewRex("\\\\|\\||\\(|\\)|\\*|\\+|\\?|(==)|;|=|{|}","special",level++);
        NFaPair nFaPair2=addNewRex("(0|1|2|3|4|5|6|7|8|9)*","num",level++);
        NFaPair nFaPair4=addNewRex("hero|what","hero",level++);
        NFaPair nFaPair3=addNewRex("(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)*","string",level++);

        NFaPair[] nFaPairs={nFaPair1,nFaPair2,nFaPair3,nFaPair4};*/
        NFaPair[] t=new NFaPair[nFaPairs.size()];
        DFA dfa=new DFA(nFaPairs.toArray(t));
        Table=new Table(dfa.getdFaNodes().get(0));
        table=Table.getTable();
        isEndNode=Table.getIsEndNode();
    }


    /**
     * 生成一个对应正则表达式的子NFA图，获得pair
     *
     * @param  rex 中缀正则表达式
     * @param  result 该正则表达式指向的意思
     * @param  endLevel 正则表达式所处的级别，按照写入正则表达式的顺序，优先者等级越高
     * @return 返回生成NFApair结点
     */
    private NFaPair addNewRex(String rex,String result,int endLevel) throws Exception {
        NormalRegex normalRegex=new NormalRegex(rex);
        Shunting_yard shunting_yard=new Shunting_yard(normalRegex.getNodeList(),AllManager.operatorManager);
        abstractGrammarTree abstractGrammarTree=new abstractGrammarTree(shunting_yard.getResult());
        NFA nfa=new NFA(abstractGrammarTree.getHeadNode());
        nfa.SetEndLevel(endLevel);
        if (EndMean[endLevel]==null)
            EndMean[endLevel]=result;
        else
            throw new Exception("have the same level mean");
        return nfa.getResultPair();
    }

    /**
     * 获取一个状态机图的起始位置点的id
     */
    public int getStartNum() {
        return Table.getStartNum();
    }

    /**
     * 判断该结点是否是终结符结点
     *
     * @param  node 状态图中对应的结点id
     * @return 如果是终结符结点返回true，否则返回false
     */
    public boolean isEndNode(int node){
        return isEndNode[node]!=null;
    }

    /**
     * 给定结点和下一个终结符，返回走向下一个结点的id
     *
     * @param  node 当前对应的结点id
     * @param  finalnode 下一个字符
     * @return 如果存在对应字符的下一个结点状态，返回下一个结点状态的id，否则返回-1
     */
    public int Goto(int node,char finalnode){
       if(table[finalnode][node]==null)
           return -1;
       else
           return table[finalnode][node];
    }

    /**
     * 返回结点的终结符代表的正则表达式的含义
     *
     * @param  node 当前对应的结点id
     * @return 如果是一个终结符，返回对应正则表达式的含义，否则抛出异常
     * @throws Exception 当前不是一个终结符结点
     */
    public String endMean(int node) throws Exception {
        if (isEndNode[node]==null)
            throw new Exception("这不是终结符结点");
        return EndMean[isEndNode[node]];
    }
}
