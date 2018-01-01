package Lex;

import NFA.NFaNodeManager;
import Regex.OperatorManager;

/**
 * AllManager 类
 * 管理所有的结点调用，和初始化配置
 *
 * @author Mind
 * @version 1.0
 */
public class AllManager {

    public static NFaNodeManager nFaNodeManager;
    public static OperatorManager operatorManager=new OperatorManager(true);

    static {
        try {
            nFaNodeManager = new NFaNodeManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
