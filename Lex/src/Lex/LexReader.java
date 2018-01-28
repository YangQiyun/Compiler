package Lex;

import javax.swing.*;
import java.io.IOException;
import java.io.Reader;

/**
 * LexReader 类
 * 继承Reader,主要功能是是为了实现从窗口中获取字符流
 *
 *
 * @author Mind
 * @version 1.0
 */
public class LexReader extends Reader{

    //Reader的缓冲缓冲池
    private char[] buff;
    //输入内容的拷贝副本
    private char[] counterpart;

    private int pos=0;

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if(buff==null){
            counterpart=showDialog();
            if (counterpart==null)
                return -1;
            else {
                System.out.println(counterpart);
                pos=0;
                buff=new char[counterpart.length+1];
                buff[counterpart.length]='\n';
                System.arraycopy(counterpart,0,buff,0,counterpart.length);
            }
        }

        int size=0;
        int length=buff.length;
        while (pos<length&&size<len){
            cbuf[off+size++]=buff[pos++];
        }
        if (pos==length)
            buff=null;

        return size;
    }

    public char[] showDialog() {
        JTextArea area = new JTextArea(20, 40);
        JScrollPane pane = new JScrollPane(area);
        int result = JOptionPane.showOptionDialog(null, pane, "Input",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, null, null);
        if (result == JOptionPane.OK_OPTION)
            return area.getText().toCharArray();
        else
            return null;
    }

    @Override
    public void close() throws IOException {

    }

    public static void main(String args[]) throws IOException {
        LexReader lexReader=new LexReader();
        char[] temp=new char[50];
        lexReader.read(temp,0,20);
    }
}
