package LR1;

import com.sun.corba.se.impl.oa.toa.TOA;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

/**
 *用过的测试用例
 S:L=R
 S:R
 L:*R
 L:i
 R:L

 S:CC
 C:cC|d

 S:Aa|bAc|Bc|bBa
 A:d
 B:d

 S:iSeS|iS|S;S|a
 */
/**
 * LR1 类
 *  实现LR1进行分析
 *  主要过程：读取文法--求出非终结符的FIRST--构造出集合GOTO图--构造ACTION和GOTO表--分析器
 *
 * @author Mind
 * @version 1.0
 */
public class LR1 {
    //所有的产生式集合
    private List<Production> productions;
    //求出的FIRSRT集合
    HashMap<String,ArrayList<Token>> FIRST;
    //生成一个hashMap用于常数时间定位到指定非终结符的production的位置
    HashMap<String,Integer[]> mapProduction=new HashMap<>();
    //Table项集族的GOTO图
    Integer[][] gotoTable=new Integer[100][50];
    //所有的token，不包括自己创建的S'
    HashMap<String,Token> allTokens;
    //ACTION表
    Integer[][] Table_Action;
    //GOTO表
    Integer[][] Table_Goto;
    //所有的集合存储
    ArrayList<CLOSURE> allClosure;
    //构造action和goto表的时候的偏移量,构造函数有说明
    public static final int SPECIAL=10;

    /**
     * 初始化LR1，进行构建分析表，测试用例
     *
     * @param  productions 文法的产生式集合
     * @param  allTokens 文法的产生式中所有的唯一的token
     * @throws Exception 根据逻辑需要抛出的错误，方便以后的定位修改
     */
    public LR1(List<Production> productions,HashMap<String,Token> allTokens) throws Exception {
        this.productions=productions;
        this.allTokens=allTokens;
        FIRST();
        ALLI();
        createTable();
        Token id=new Token("c",true);
        id.setId(allTokens.get("c").getId());
        Token add=new Token("d",true);
        add.setId(allTokens.get("d").getId());
        Token mutil=new Token("C",true);
        mutil.setId(allTokens.get("C").getId());
        ArrayList<Token> mtest=new ArrayList<>();
        mtest.add(id);
        mtest.add(add);
        mtest.add(add);
        mtest.add(Token.END_OF_FILE);
        analyse(mtest);


    }

    /**
     * 内部类：CLOSURE
     * 表示一个状态集合，保存有该集合内的所有产生式
     */
    private static class CLOSURE{
        private static int sign=0;
        //该闭包的编号
        public int num;
        public List<Production> productions;

        public CLOSURE(){
            productions=new ArrayList<>();
        }

        public CLOSURE(Production start){
            productions=new ArrayList<>();
            productions.add(start);
        }

        //判断两个集合是否相等，判断的依据是所有原生的产生式，就是dot不在1的产生式是否相同
        @Override
        public boolean equals(Object obj) {
            if (obj.getClass()!=CLOSURE.class)
                return false;
            CLOSURE another=(CLOSURE)obj;
            //只判断dot不为1的production是否相等
            ArrayList<Production> thisDot=new ArrayList<>();
            for (Production thisProduction:productions){
                if (thisProduction.getDot()!=1)
                    thisDot.add(thisProduction);
            }
            ArrayList<Production> anotherDot=new ArrayList<>();
            for (Production production:another.productions){
                if (production.getDot()!=-1)
                    anotherDot.add(production);
            }
            if (thisDot.size()!=anotherDot.size())
                return false;
            Out:
            for (Production thisP:thisDot){
                for (Production anoP:anotherDot){
                    if (thisP.equals(anoP)){
                        continue Out;
                    }
                }
                return false;
            }
            return true;
        }

        public static void addSign(){
            ++sign;
        }

        public void refreshNum(){
            num=sign;
        }
    }

    /**
     *构造LR（1）项集族C'={I0,I1,...,In}
     */
    private void ALLI() throws Exception {
        //构造一个新的产生式头部进行管理
        Production newStart=new Production(new Token("StartProduction",false));
        newStart.add(productions.get(0).get(0));
        newStart.getPredictor().add(Token.END_OF_FILE);

        allClosure=new ArrayList<>();
        Queue<CLOSURE> settleQueue=new LinkedTransferQueue<>();
        CLOSURE firstClosure=new CLOSURE(newStart);
        firstClosure.refreshNum();
        CLOSURE.addSign();
        settleQueue.add(firstClosure);
        allClosure.add(firstClosure);

        /*
        *项集族的生成，主要思路是通过一个队列进行广度优先处理
        *大致过程：
        * 处理当前集合，进行内部扩展
        *  处理集合的外部扩展，如果是新的状态加入到处理队列
        *  直到所有的集合都处理过
        * */
         while (!settleQueue.isEmpty()){
             CLOSURE currentClosure=settleQueue.poll();
             NextProduction:
             for (int i=0;i<currentClosure.productions.size();++i){
                 List<Token> predictor=new ArrayList<>();
                 Production currentProduction=currentClosure.productions.get(i);
                 int currentDot=currentProduction.getDot();

                 if(currentDot!=currentProduction.getSize()){
                     //判断dot的下一个字符是否为终结符,是终结符则什么都不处理
                     if (!currentProduction.get(currentDot).isTerminal()){
                         //根据[ A -> α·Bβ, a ],如果存在β
                         if (currentDot<=currentProduction.getSize()-2){
                             Token nextCharactor=currentProduction.get(currentDot+1);
                             //如果β是终结符
                             if (nextCharactor.isTerminal())
                                 predictor.add(nextCharactor);
                             else {//如果β不是终结符，需要求出FIRST(βa)
                                 if (!predictor.addAll(FIRST.get(nextCharactor.getValue())))
                                     throw new Exception("copy error");
                                 //判断该非终结符是否能够推出ε
                                 for (Integer βproduction:mapProduction.get(nextCharactor.getValue())){
                                     if(productions.get(βproduction).get(1).getValue().equals("ε")) {
                                         //如果存在，将a加入到预测符中
                                         if (!predictor.addAll(currentProduction.getPredictor()))
                                             throw new Exception("add predictor error");
                                         break;
                                     }
                                 }

                             }
                         }else {//如果不存在β
                             if (!predictor.addAll(currentProduction.getPredictor()))
                                 throw new Exception("add predictor error");
                         }

                        //将B推出的产生式加入到当前集合中
                        boolean isExistProduction=false;
                         //判断当前currentClosure是否存在有已知的产生式
                         for (Production existProduction:currentClosure.productions){
                             //如果存在该产生式了
                             if (existProduction.getDot()==1&&existProduction.get(0)==currentProduction.get(currentDot)){
                                 isExistProduction=true;
                                 //判断预测符是否已经存在,将剩余预测符加入到产生式中
                                 for (Token newPredictor:predictor){
                                     if (!existProduction.getPredictor().contains(newPredictor))
                                         existProduction.getPredictor().add(newPredictor);
                                 }

                             }
                         }
                         //如果不存在加入则加入新的产生式
                         if (!isExistProduction) {
                             for (Integer Bproduction : mapProduction.get(currentProduction.get(currentDot).getValue())) {
                                 if (productions.get(Bproduction).get(1).equals("ε"))
                                     continue;
                                 Production newProduction = new Production();

                                 newProduction.copyProduction(productions.get(Bproduction));
                                 newProduction.getPredictor().addAll(predictor);
                                 currentClosure.productions.add(newProduction);
                             }
                         }
                     }


                 }
             }

             //进行GOTO操作
             for (Production production:currentClosure.productions){
                 if (production.getDot()!=production.getSize()){
                     if(gotoTable[currentClosure.num][production.get(production.getDot()).getId()]!=null)
                         continue;
                     CLOSURE getClosure=GOTO(currentClosure,production.get(production.getDot()));
                     boolean isExistOneSame=false;
                     for (CLOSURE closure:allClosure) {
                         if (closure.equals(getClosure)) {
                             getClosure=closure;
                             isExistOneSame=true;
                             break;
                         }
                     }
                     //是一个新的状态更新编号
                     if (!isExistOneSame){
                         getClosure.refreshNum();
                         CLOSURE.addSign();
                         allClosure.add(getClosure);
                         settleQueue.add(getClosure);
                     }
                     gotoTable[currentClosure.num][production.get(production.getDot()).getId()]=new Integer(getClosure.num);

                 }

             }//这里
         }
    }


    /**
     * 内部函数，状态间外部拓展
     * 实现思路：
     * 根据提供的下一个字符，进行搜索所有的可拓展的产生式，生成一个新的集合
     * 注意该新的集合并不一定是“新”的，所以回去后会进行判断，如果它是一个全新的
     * 集合，那么会给予它相应的编号，如果它存在了，那么它会自动被GC处理掉
     *
     * @param  I 待处理的集合
     * @param  X 待处理的集合的拓展字符走向
     * @return 返回生成一个新的集合
     */
    private CLOSURE GOTO(CLOSURE I,Token X){
        CLOSURE J=new CLOSURE();
        for (Production production:I.productions){
            if (production.getDot()!=production.getSize()&&production.get(production.getDot())==X){
                Production newProduction=new Production();
                newProduction.copyProduction(production);
                newProduction.setDot(production.getDot()+1);
                newProduction.getPredictor().addAll(production.getPredictor());
                J.productions.add(newProduction);
            }
        }

        return J;
    }

    /**
     * 内部函数，生成FIRST集
     * 实现思路：
     * 根据提供处理队列，如果一个token需要另一个token优先，那么它处理过的所有状态会重置为null
     * 如果一个token的FIRST处理完成会存放在一个map中
     * 如果一个token会产生自循环，那么直接跳过，因为它自身在待处理队列中，等待其他完成
     *
     */
    private void FIRST(){
        HashMap<String,ArrayList<Token>> result=new HashMap<>(productions.size());
        //所有非终结符处理队列
        LinkedList<Token> settleList=new LinkedList<>();


        for (int i=0;i<productions.size();++i){
            Production current=productions.get(i);
            Integer[] oldOne=mapProduction.get(current.get(0).getValue());
            if (oldOne==null){
                mapProduction.put(current.get(0).getValue(),new Integer[]{i});
                //同时初始化结果集
                result.put(current.get(0).getValue(),null);
                settleList.add(current.get(0));
            }
            else{
                Integer[] newOne=new Integer[oldOne.length+1];
                System.arraycopy(oldOne,0,newOne,0,oldOne.length);
                newOne[newOne.length-1]=i;
                mapProduction.put(current.get(0).getValue(),newOne);
            }
        }


        //进行FIRST集的处理
        FIRST:
        while (!settleList.isEmpty()){
            Token currentToken=settleList.peekFirst();

            if (result.get(currentToken.getValue())==null) {
                result.put(currentToken.getValue(),new ArrayList<>());
                for (Integer everyProduction : mapProduction.get(currentToken.getValue())) {
                    //如果产生式的右部的一个字符式终结符
                    Token firstToken = productions.get(everyProduction).get(1);
                    if (firstToken.isTerminal()) {
                        if (!result.get(currentToken.getValue()).contains(firstToken))
                             result.get(currentToken.getValue()).add(firstToken);
                    } else {
                        //如果不是终结符，判断是否处理过,如果是自身，也就是自循环，不处理
                        if (firstToken==currentToken){
                            continue ;
                        }
                        if (result.get(firstToken.getValue()) != null) {
                            for (Token temp : result.get(firstToken.getValue())) {
                                result.get(currentToken.getValue()).add(temp);
                            }
                        } else {//如果没有处理过，优先处理，加入link，同时清除当前token的所有已处理的结果
                            settleList.addFirst(firstToken);
                            result.put(currentToken.getValue(), null);
                            continue FIRST;
                        }
                    }
                }
            } //如果所有都正常处理结束，弹出该token
            settleList.pollFirst();
        }
        //通过测试用例
        /*
        * E : TG
        * G:+TG|ε
        * T:FU
        * U:*FU|ε
        * F:(E)|i
        * */
        FIRST=result;
    }

    /**
     * 内部函数，LR语法分析表的构建
     * 实现思路：
     * 我们约定Sn为正数，rn为负数，并且这两者进行special这个值进行偏移，比如S1记为1+special，n1记为-1-special，acc为0，不做任何处理
     * 还未处理移进归约冲突
     */
    private void createTable(){
       Table_Action=new Integer[allClosure.size()][allTokens.size()*2];
       Table_Goto=new Integer[allClosure.size()][allTokens.size()*2];
       for (int indexOfCourse=0;indexOfCourse<allClosure.size();indexOfCourse++){
           for (Production production:allClosure.get(indexOfCourse).productions){


                   if (production.getDot()==production.getSize()){
                       //[S'->S·，＄]
                       if (production.get(0).getValue().equals("StartProduction"))
                           Table_Action[indexOfCourse][production.getPredictor().get(0).getId()]=0;
                       else {  //[A->α·，a]
                           for (Token predict : production.getPredictor()) {
                               //寻找是原文法中的哪一条语句
                               Integer[] alltag = mapProduction.get(production.get(0).getValue());
                               for (Integer guess : alltag) {
                                   if (productions.get(guess).equalsContent(production)) {
                                       //进行归约
                                       Table_Action[indexOfCourse][predict.getId()] = -guess - SPECIAL;
                                   }
                               }

                           }
                       }
                   }else {
                       //如果后面跟随是终结符[A->α·aβ，b],则就是递进S
                       Token nextCharacter=production.get(production.getDot());
                       if (nextCharacter.isTerminal()){
                           Table_Action[indexOfCourse][nextCharacter.getId()]=gotoTable[indexOfCourse][nextCharacter.getId()]+SPECIAL;
                       }else {//如果后面跟随不是终结符[A->α·Bβ，b],则就是GOTO表
                           Table_Goto[indexOfCourse][nextCharacter.getId()]=gotoTable[indexOfCourse][nextCharacter.getId()];
                       }

                   }

           }
       }
    }

    /**
     * LR分析器
     * 实现思路：
     * 根据计算得到的ACTION和GOTO子表进行计算
     *
     * @param  input 待处理的token序列
     * @return 返回是否能处理成功
     */
    public boolean analyse(List<Token> input){
        Stack<Integer> stack=new Stack<>();
        Stack<Token> symbol=new Stack<>();
        Iterator<Token> iterator=input.iterator();
        //a是下一个符号
        Token a=iterator.next();
        stack.push(0);

        try {
        while (true){
            int s=stack.peek();

                if (Table_Action[s][a.getId()]==null)
                {
                    System.out.println("执行错误实例");
                    return false;
                }else if (Table_Action[s][a.getId()] > 0) {//移入
                    symbol.push(a);
                    stack.push(Table_Action[s][a.getId()] - SPECIAL);
                    a = iterator.next();
                } else if (Table_Action[s][a.getId()] < 0) {//归约

                    for (int i = 0; i < productions.get(-Table_Action[s][a.getId()] - SPECIAL).getSize() - 1; ++i){
                        symbol.pop();
                        stack.pop();
                    }

                    symbol.push(productions.get(-Table_Action[s][a.getId()] - SPECIAL).get(0));
                    int t = stack.peek();
                    stack.push(Table_Goto[t][symbol.peek().getId()]);
                } else if (Table_Action[s][a.getId()] == 0) {
                    break;
                }


        }
        }catch (Exception e){
            System.out.println("分析中执行错误实例");
            return false;
        }
        System.out.println("success");
        return true;
    }
}
