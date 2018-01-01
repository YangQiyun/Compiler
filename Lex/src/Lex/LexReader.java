package Lex;

import javax.swing.*;
import java.io.IOException;
import java.io.Reader;


public class LexReader extends Reader{

    //Reader的缓冲缓冲池
    private char[] buff;
    //输入内容的拷贝副本
    private char[] counterpart;

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if(buff==null){
            buff=showDialog();
            counterpart=new char[buff.length];
            System.arraycopy(buff,0,counterpart,0,buff.length);

        }
        return 0;
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
