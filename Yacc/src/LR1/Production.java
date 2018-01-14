package LR1;


import java.util.ArrayList;
import java.util.List;

/**
 * 产生式的文法，每一个产生式的构造
 *
 * @author Mind
 * @version 1.0
 */
public class Production {

    //表示当前的点的位置，默认初始化是在1，当dot等于size的时候表示走到了结尾
    private int dot=1;
    //预测符的list维护队列
    private ArrayList<Token> predictor;
    private List<Token> mContent;

    //创建一个新的产生式的时候必须优先确定好第一个非终结符，即左边的符号
    public Production(Token nonTerminalToken){
        mContent=new ArrayList<>();
        predictor=new ArrayList<>();
        mContent.add(nonTerminalToken);
    }

    public Production(){
        mContent=new ArrayList<>();
        predictor=new ArrayList<>();
    }

    public boolean add(Token token){
        return mContent.add(token);
    }

    public Token get(int index){
        return mContent.get(index);
    }

    public int getSize(){
        return mContent.size();
    }

    public ArrayList<Token> getPredictor(){
        return predictor;
    }

    /**
     * 将点向后移一位，如果已经到达了末尾则返回false
     * 正常移动返回true
     *
     *  @return 未到末尾则返回true，已经到达末尾无法再移动返回false
     */
    public boolean moveDotOneSept(){
        if (dot==mContent.size())
            return false;
        else
            ++dot;
        return true;
    }

    public void setDot(int dot) {
        this.dot = dot;
    }

    public int getDot() {
        return dot;
    }
    //除了终结符,产生式内容复制
    public void copyProduction(Production src){
        this.mContent.clear();
        this.mContent.addAll(src.mContent);
    }

    //所有内容都相等，包括了产生式，预测符，dot
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass()!=Production.class)
            return false;
        Production another=(Production)obj;
        if (mContent.size()!=another.getSize())
            return false;
        if (predictor.size()!=another.predictor.size())
            return false;
        if (dot!=another.getDot())
            return false;
        for (int i=0;i<mContent.size();++i){
            if (mContent.get(i)!=another.mContent.get(i))
                return false;
        }
        for (int i=0;i<predictor.size();++i){
            if (predictor.get(i)!=another.predictor.get(i))
                return false;
        }
        return true;
    }

    //产生式内容是否相等，不包括预测符
    public boolean equalsContent(Production another){
        if (another.mContent.size()!=mContent.size())
            return false;
        for (int i=0;i<mContent.size();++i){
            if (mContent.get(i)!=another.mContent.get(i))
                return false;
        }
        return true;
    }
}
