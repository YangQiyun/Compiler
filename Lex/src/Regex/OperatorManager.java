package Regex;


import java.util.HashMap;
import java.util.Map;

/**
 * 操作符的管理
 * 默认等级越高，数字越大，按照1-7
 * 等级值*100+操作数个数为实际的存储值
 *
 * @author Mind
 * @version 1.0
 */

public class OperatorManager {

    private Map<String,Integer> Operator;

    public OperatorManager(){
        Operator=new HashMap<>(10);
    }

    //测试用的构造函数
    public OperatorManager(boolean testTrue){
        this();
        addOperator("|",1,2);
        addOperator(".",2,2);
        addOperator("*",3,1);
        addOperator("+",3,1);
        addOperator("?",3,1);
        addOperator("(",-100,0);
        addOperator(")",-100,0);

    }

    public void addOperator(String operator,int Level,int Op_arg_count){
        Operator.put(operator,Level*100+Op_arg_count);
    }

    public boolean isLeftBigger(String leftOperator,String rightOperator){
        return Operator.get(leftOperator)>Operator.get(rightOperator);
    }

    /**
     * 获取操作符的操作数，是单目还是双目操作数
     *
     * @param operator the operator
     *
     * @return 返回操作数的个数
     */
    public int getOpCount(String operator){
        return  Operator.get(operator)%100;
    }

}
