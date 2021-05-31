package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class CompilerScanner {
    int linenumber=1;
    ArrayList<MyToken> Tokens;
    Character[] Special_Characters;
    String [] Reserved_KeyWords={"if","then","else","end","repeat","until","read","write"};
    int startindx=0;
    String code;
    CompilerScanner(String CodeText)
    {
        this.Special_Characters=new Character[]{'*','/','+','-','=','<',';','(',')',':','>','–'};
      //  ArrayList<Character>specialcharacters=new ArrayList()
        this.code=CodeText;
        Tokens=new ArrayList<MyToken>();
    }
    MyToken NextToken() throws Exception {
        String tokenvalue="";
        TokenType tokenType=TokenType.Empty;
        DFA_States currentstate=DFA_States.Start;
        while (startindx!= this.code.length()&&currentstate!=DFA_States.End)
        {
           // System.out.println("scanning from "+ startindx);
            switch (currentstate){
                case Start:
                {
                    if (IsAlphabeticChar(code.charAt(startindx))) {
                        currentstate = DFA_States.In_Id;
                    } else if ((this.code.charAt(startindx) >= '0' && this.code.charAt(startindx) <= '9')) {
                        currentstate = DFA_States.In_Num;
                    } else if (this.code.charAt(startindx) == ':') {
                        currentstate = DFA_States.In_Assign;
                    } else if (this.code.charAt(startindx) == '{') {
                        startindx++;
                        currentstate = DFA_States.In_Comment;
                    }
                    else if(this.code.charAt(startindx)=='\n'||this.code.charAt(startindx)==' '){
                        if (this.code.charAt(startindx)=='\n')
                            linenumber++;
                        startindx++;
                        currentstate=DFA_States.Start;
                    }
                    else currentstate = DFA_States.Other;
                }
                break;
                case In_Comment:
                {
                    while (code.charAt(startindx)!='}'&&startindx<code.length())
                    {
                        startindx++;
                        //tokenvalue+=code.charAt(startindx++);
                    }
                    startindx++;
                   // tokenType=TokenType.Comment;
                    currentstate=DFA_States.Start;
                }
                break;

                case In_Num:
                {
                    while(startindx<code.length())
                {
                    if(code.charAt(startindx)>='0'&&code.charAt(startindx)<='9')
                    {
                        tokenvalue+=code.charAt(startindx++);
                    }
                    else break;

                }
                    tokenType=TokenType.NUM;
                    currentstate=DFA_States.End;
                }
                break;

                case In_Id:
                {

                    while(startindx<code.length())
                    {
                        if(IsAlphabeticChar(code.charAt(startindx)))
                        tokenvalue+=code.charAt(startindx++);
                        else break;
                    }
                    if(IsReservedWord(tokenvalue.toLowerCase(Locale.ROOT)))
                    {
                        tokenType=TokenType.Reserved_Keyword;
                    }else
                    {
                        tokenType=TokenType.Identifier;
                    }
                    currentstate=DFA_States.End;
                }
                break;
                case Other:
                {
                    if (Arrays.asList(Special_Characters).contains(code.charAt(startindx))) {
                        tokenType = switch (code.charAt(startindx)) {
                            case '>' -> TokenType.Greater_Than_Operator;
                            case '<' -> TokenType.Less_Than_Operator;
                            case '+' -> TokenType.Addition_Operator;
                            case '-', '–' -> TokenType.Subtraction_Operator;
                            case '*' -> TokenType.Multiplication_Operator;
                            case '/' -> TokenType.Division_Operator;
                            case '(' -> TokenType.Open_Bracket;
                            case ')' -> TokenType.Closed_Bracket;
                            case '=' -> TokenType.Equal_Operator;
                            case ';' -> TokenType.Semicolon;
                            default -> tokenType;
                        };

                            tokenvalue += code.charAt(startindx);
                            currentstate = DFA_States.End;
                            startindx++;
                    }
                    else//if the character found is unacceptable and un-tokenizable by the scanner
                    {
                        throw new Exception("untokenizable character "+code.charAt(startindx)+" at line "+linenumber);
                    }
                }
                break;
                case In_Assign:
                {
                    if (startindx+1<code.length()&&code.charAt(startindx+1)=='=')
                    {
                        startindx+=2;
                        tokenType=TokenType.Assignment_Operator;
                        tokenvalue=":=";
                        currentstate=DFA_States.End;
                    }
                    else currentstate=DFA_States.Other;
                }
                //todo fix the : special symbol
                break;
            }
        }
        return new MyToken(tokenType,tokenvalue);
    }
   public boolean HasMoreTokens()
    {
        return startindx!=code.length();
    }
    private boolean IsAlphabeticChar(char character)
    {
        if((character>='a'&&character<='z')||(character>='A'&&character<='Z'))
            return true;
        else return false;
    }
    private boolean IsReservedWord(String word)
    {
        return Arrays.asList(Reserved_KeyWords).contains(word);
    }
    public void ResetTokenizer(){
        this.startindx=0;
        this.linenumber=1;
    }

}
enum TokenType
{
    NUM,Reserved_Keyword,Identifier,Comment,Empty,Assignment_Operator,Addition_Operator,Subtraction_Operator
    ,Division_Operator,Multiplication_Operator,Equal_Operator,Greater_Than_Operator,Less_Than_Operator,Open_Bracket,Closed_Bracket,Semicolon;
}
class MyToken
{
   private TokenType tokenType;
    private String tokenvalue;
    MyToken(TokenType tokenType,String tokenvalue)
    {
         this.tokenType=tokenType;
         this.tokenvalue=tokenvalue;
    }
    public void PrintToken()
    {
       if(tokenType==TokenType.Empty||this==null)return;
       else System.out.println("TokenType is : "+ this.tokenType.toString()+" TokenValue is : "+ this.tokenvalue);
    }
}
enum DFA_States
{
    Start,End,In_Assign,In_Id,In_Num,Other,In_Comment;
}