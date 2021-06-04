package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class CompilerScanner {
    int linenumber=1;
    private MyToken previoustoken;
    ArrayList<MyToken> Tokens;
    Character[] Special_Characters;
    String [] Reserved_KeyWords={"if","then","else","end","repeat","until","read","write"};
    int startindx=0;
    String code;
    public MyToken getPrevioustoken()
    {
        return this.previoustoken;
    }
    CompilerScanner(String CodeText)
    {

        this.Special_Characters=new Character[]{'*','/','+','-','=','<',';','(',')','>','–'};
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
                    int startline=linenumber;
                    int parantheses=1;
                    while (startindx<code.length()&&parantheses!=0)
                    {
                        if(code.charAt(startindx)=='\n')
                            linenumber++;
                        else if(code.charAt(startindx)=='{')
                            parantheses++;
                        else if(code.charAt(startindx)=='}')
                            parantheses--;
                        //if(startindx+1==code.length())break;
                        startindx++;
                        //tokenvalue+=code.charAt(startindx++);
                    }
                   if((startindx==code.length())&&parantheses!=0)
                    {
                        throw new Exception("comment not closed from line "+startline +" till line "+linenumber);
                    }

                   // startindx++;
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
                    else//if the character found is unacceptable and un-tokenizable by the scanner
                    {
                        throw new Exception("untokenizable character "+code.charAt(startindx)+" at line "+linenumber);
                    }
                }
                //todo fix the : special symbol
                break;
            }
        }
        previoustoken=new MyToken(tokenType,tokenvalue);
        if(previoustoken.getTokenType()!=TokenType.Empty)
        {
            Tokens.add(previoustoken);
        }
        return previoustoken;
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
        this.Tokens.clear();
    }
    public void PeekToken() throws Exception//peeks to next token if it can still tokenize
    {
        if(this.HasMoreTokens())
            this.NextToken();
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

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getTokenvalue() {
        return tokenvalue;
    }

    public void PrintToken()
    {
       if(tokenType==TokenType.Empty||this==null)return;
       else System.out.println("TokenType is : "+ this.tokenType.toString()+" TokenValue is : "+ this.tokenvalue);
    }
    @Override
    public String toString()
    {
        if(this!=null)
        return this.tokenType.toString()+"       "+this.tokenvalue+"\n";
        return "";
    }
}
enum DFA_States
{
    Start,End,In_Assign,In_Id,In_Num,Other,In_Comment;
}
