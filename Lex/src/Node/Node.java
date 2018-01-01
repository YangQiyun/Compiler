package Node;

/**
 * Node 类
 * 构建一颗抽象语法树的结点，包括了左右子树，当点结点的值
 *
 * @author Mind
 * @version 1.0
 */
public abstract class Node {

    public static  enum TYPE{
        CAT,
        OR,
        START,
        CHARACTER,
        BRACKETS
    }

    private Node leftNode=null;
    private Node rightNode=null;
    private TYPE type;
    private String value;
    //用于深度遍历抽象语法树的时候判断用的
    private boolean isVisit=false;

    public boolean isLeftExist(){
        return leftNode==null?false:true;
    }

    public boolean isRightExist(){
        return rightNode==null?false:true;
    }

    public void setLeftNode(Node node){
        leftNode=node;
    }

    public void setRightNode(Node node){
        rightNode=node;
    }

    public void setType(TYPE type){
        this.type=type;
    }

    public Node getLeftNode(){
        return leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public TYPE getType() {
        return type;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isVisit() {
        return isVisit;
    }

    public void setVisit(boolean visit) {
        isVisit = visit;
    }

    @Override
    public abstract String toString();
}
