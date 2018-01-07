package Lex;

import mException.ParseException;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Lexer 类
 * lex进行词法分析的主程序
 *
 * @author Mind
 * @version 1.0
 */
public class Lexer {

    private ArrayList<Token> queue = new ArrayList<Token>();
    private LineNumberReader reader;
    private boolean hasMore;
    //对于状态表的统一管理
    private Scan scan;

    public Lexer() throws Exception {
        reader=new LineNumberReader(new LexReader());
        scan=new Scan();
        hasMore = true;
    }
    //词法获取得到一个单词token，存储的token的list中的拿出token，直到到达文件的结尾
    public Token read() throws ParseException {
        if (fillQueue(0))
            return queue.remove(0);
        else
            return Token.EOF;
    }
    public Token peek(int i) throws ParseException {
        if (fillQueue(i))
            return queue.get(i);
        else
            return Token.EOF;
    }
    //通过hasMore标记的进行访问 readline
    private boolean fillQueue(int i) throws ParseException {
        while (i >= queue.size())
            if (hasMore)
                readLine();
            else
                return false;
        return true;
    }

    //通过bufferReader读取一行后然后进行正则表达式解析
    protected void readLine() throws ParseException {
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new ParseException(e);
        }
        if (line == null) {
            hasMore = false;
            queue.add(Token.EOF);
            return;
        }

        //当前扫描的行数
        int lineNo = reader.getLineNumber();
        //行内字符的序号
        int indexLine=0;
        //单词起始位置
        int start = 0;
        //当前扫描位置
        int current=0;
        int endPos = line.length();
        while (current < endPos) {

            int content=line.charAt(current);
            while (content==32||content==9){
                ++indexLine;
                content=line.charAt(++current);
            }



            //单词找到
            start=current;
            /* 从这个单词的开始位置，直至走到第一个终结结点 */
            int node=scan.getStartNum();
            do{
                node=scan.Goto(node,line.charAt(current++));
                ++indexLine;


                if(node == -1){
                    throw new ParseException("bad token at line " + lineNo+"start position is "+start+"content is"+(char)content);
                }
            }while (current<endPos&&!scan.isEndNode(node));

            if(node!=-1 && !scan.isEndNode(node)){
                throw new ParseException("bad token at line " + lineNo+"start position is "+start+"content is"+(char)content);
            }

            int mark=-1;
            if (scan.isEndNode(node)){
                mark=node;
                while (current<endPos){
                    node=scan.Goto(node,line.charAt(current++));
                    if (node==-1)
                        break;
                    if (scan.isEndNode(node))
                    mark=node;
                    indexLine++;
                }

                current=indexLine;
                //此时mark就是了
                try {
                    String text=new String(line.toCharArray(),start,current-start);
                    queue.add(new Token(lineNo,scan.endMean(mark),text));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }



        }

       // queue.add(new Token(lineNo,null, Token.EOL));
    }

    public static void main(String args[]){
        try {
            int a;
            Lexer lexer=new Lexer();
            for (Token t; (t = lexer.read()) != Token.EOF; )

            System.out.println("原词: "+t.getText()+"  endlevel: "+t.getValue()+" line "+t.getLineNumber());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
