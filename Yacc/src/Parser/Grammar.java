package Parser;


import LR1.LR1;
import LR1.Production;
import LR1.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 从txt获取文法内容
 *
 * @author Mind
 * @version 1.0
 */
public class Grammar {
    private HashMap<String,Token> allTokens=new HashMap<>();
    private List<Production> productions;

    public Grammar() throws Exception {
        productions=new ArrayList<>(4);
        read();
    }

    /**
     * 读取文法文件，进行解析
     */
    private void read() throws Exception {
        //读取文件
        try {
            File file=new File("Grammar.txt");
            LineNumberReader reader=new LineNumberReader (new InputStreamReader(new DataInputStream(new FileInputStream(file))));
            String content=reader.readLine();
            //文件头带有文件信息的一个字符去掉
            content=content.substring(1);
            //维护产生式队列的索引
            int indexOfproduct=-1;


            while (content!=null){
                content=content.replaceAll("\\s","");
                String[] getString=content.split(":");
                Token TerminalToken=new Token(getString[0],false);
                //产生式的非终结符已经定义过一次了
                Token getIfexist=allTokens.get(getString[0]);
                if (getIfexist!=null) {
                    if (getIfexist.isTerminal())
                        getIfexist.setTerminal(false);
                    TerminalToken=getIfexist;
                }
                else
                    allTokens.put(getString[0],TerminalToken);


                String[] OrItemString=getString[1].split("\\|");

                for (String everyString:OrItemString) {
                    ++indexOfproduct;
                    productions.add(new Production(TerminalToken));
                    //产生式右部
                    for (char everyitem : everyString.toCharArray()) {
                        String value = new Character(everyitem).toString();
                        Token token = allTokens.get(value);
                        if (token == null) {
                            token = new Token(value, true);
                            allTokens.put(value, token);
                        }
                        productions.get(indexOfproduct).add(token);
                    }
                }

                content=reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        try {
            Grammar grammar=new Grammar();
            new LR1(grammar.productions,grammar.allTokens);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
