package Lex;

/**
 * Token 类
 * 任一正则表达式代表的一个单元
 * 包括了
 * 原词 text
 * 代表的正则含义 value
 * 处在文本的第几行 lineNumber
 *
 * @author Mind
 * @version 1.0
 */
public class Token {

    public static final Token EOF=new Token(-1,"",""); //end of file
    public static final String EOL = "\\n";          // end of line

    private int lineNumber;
    //代表的正则类型
    private String value;
    //原语句
    private String text;

    public Token(int line,String value,String text){
        this.lineNumber=line;
        this.value=value;
        this.text=text;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
