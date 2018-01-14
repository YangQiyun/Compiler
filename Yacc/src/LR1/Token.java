package LR1;

/**
 * 每一个文法对应的符号
 *
 * @author Mind
 * @version 1.0
 */
public class Token {
    public static Token END_OF_FILE=new Token("＄",true);
    //初始化认为每一个符号都是终结符
    private boolean isTerminal=true;
    private String value;
    private int id=0;
    private static int indexOfid=1;

    public Token(String value,boolean isTerminal) {
        this.value=value;
        this.isTerminal=isTerminal;
        id=indexOfid++;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public void setTerminal(boolean terminal) {
        isTerminal = terminal;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
