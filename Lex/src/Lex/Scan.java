package Lex;

import DFA.DFaNode;
import ODFA.*;

public class Scan {
    Table Table;
    Integer[][] table;
    Integer[] isEndNode;
    public Scan(DFaNode head){
         Table=new Table(head);
      table=Table.getTable();
         isEndNode=Table.getIsEndNode();


    }

    public void setValue(String value){
       ;
        Integer Step=Table.getStartNum();

        int index=0;
        while (index<value.length()){
            Step=table[value.charAt(index)][Step];
            if (Step==null)
                System.out.println("error");
            else{
                if (isEndNode[Step]!=null)
                    System.out.println("终结符"+isEndNode[Step]);

            }
            index++;
        }
    }
}
