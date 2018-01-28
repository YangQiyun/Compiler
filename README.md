# compiler #

Lex+Yacc，后者主要是LR1，每个类都有较为详细的注释

## 一.　Lex ##

###  **类图** ###
![类图结构](https://i.imgur.com/dZCyjJc.png)

        1. 实现流程
        　　RE->NFA->DFA->ODFA->LEX
        
        2. 支持正则符号
        　　 * . | ( ) + ?
        
       
### **使用流程** ###

1.在包中Rex.txt添加规则如下，需要格式是UTF-8,英文输入格式

	m.a.i.n|d.o|w.h.i.l.e|c.l.a.s.s|e.n.u.m|p.r.i.v.a.t.e|p.u.b.l.i.c|s.t.a.t.i.c|f.i.n.a.l|v.o.i.d|b.o.o.l.e.a.n|c.h.a.r|i.n.t|l.o.n.g|f.l.o.a.t|d.o.u.b.l.e|r.e.t.u.r.n|i.f|e.l.s.e|f.o.r 		retain
    (_|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z).(0|1|2|3|4|5|6|7|8|9|_|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)*			id
    0|(1|2|3|4|5|6|7|8|9).(0|1|2|3|4|5|6|7|8|9)*			number
    {	bleftBrace
    }	brightBrace
    \(	sleftBrace
    \)	srightBrace
    ;	semicolon
    \\|\||\*|\+|\?|=.=|;|=|-|/|\+.=|-.=|\*.=|/.=|<|>|>.=|<.=|\+.\+|-.-   	operator 

每一种Token占据一行，前半部分表示该Token的正则表达式，后半部分表示Token的意思
例如“{   bleftBrace“表示匹配左花括号时表示bleftBrace

2.运行Lexer类中的main函数，输入的输入框，内容输入

![](https://i.imgur.com/mM5kzVf.png)

3.运行结果如下

	for (int a=1;a<10;++a){
	int b=2;
	}
	原词: for  endlevel: retain line 1
	原词: (  endlevel: sleftBrace line 1
	原词: int  endlevel: retain line 1
	原词: a  endlevel: id line 1
	原词: =  endlevel: operator line 1
	原词: 1  endlevel: number line 1
	原词: ;  endlevel: semicolon line 1
	原词: a  endlevel: id line 1
	原词: <  endlevel: operator line 1
	原词: 10  endlevel: number line 1
	原词: ;  endlevel: semicolon line 1
	原词: ++  endlevel: operator line 1
	原词: a  endlevel: id line 1
	原词: )  endlevel: srightBrace line 1
	原词: {  endlevel: bleftBrace line 1
	原词: int  endlevel: retain line 2
	原词: b  endlevel: id line 2
	原词: =  endlevel: operator line 2
	原词: 2  endlevel: number line 2
	原词: ;  endlevel: semicolon line 2
	原词: }  endlevel: brightBrace line 3

分别展示识别的原词内容，所属Token意思，所在的行数

### **NFA对应的结点转换图原理** ###
![](https://i.imgur.com/9WRYpLu.png)

## 二.　Yacc ##

### **类图** ###
![](https://i.imgur.com/7YEprvn.png)

	主要流程是读取语法文件—LR1图—>ACTION和GOTO子表的构建，LR1分析器的构建

### **使用流程** ###

1.输入Grammar.txt文件文法内容，例如

	S:CC
	C:cC|d

2.添加测试token序列

	Token c=new Token("c",true);
    c.setId(allTokens.get("c").getId());
    Token d=new Token("d",true);
    d.setId(allTokens.get("d").getId());
    ArrayList<Token> mtest=new ArrayList<>();
    mtest.add(c);
    mtest.add(d);
    mtest.add(d);
    mtest.add(Token.END_OF_FILE);
    analyse(mtest);

3.得到分析结果

	C -> d
	C -> cC
	C -> d
	S -> CC
	success
内容分别是归约时候使用的文法句子