package sample;

import java.util.ArrayList;

public class CompilerParser {
    CompilerScanner scanner;
    ArrayList<String> program;
    CompilerParser(CompilerScanner scanner){
        this.scanner=scanner;
        program=new ArrayList<>();
    }
    public void parse() throws Exception {
            scanner.ResetTokenizer();
           // ParseOptions parserstate=ParseOptions.start;
            //if(!scanner.HasMoreTokens()) return;
            //MyToken CurrentToken=scanner.NextToken();
            program();

        }
        private void program() throws Exception {
            //get the first token of the program to begin compiling
            scanner.PeekToken();
            stmt_sequence();
            System.out.println("program compiled successfully");
        }
        private void stmt_sequence() throws Exception {
            statement();

        while (scanner.getPrevioustoken().getTokenType()==TokenType.Semicolon)
            {
                match(TokenType.Semicolon);
                statement();
            }

        }
        private void statement() throws Exception {
            switch (scanner.getPrevioustoken().getTokenType())
            {
                case Reserved_Keyword:{
                    switch (scanner.getPrevioustoken().getTokenvalue()) {
                        case "if" -> {
                            match("if");
                            if_stmt();
                        }
                        case "repeat" -> {
                            match("repeat");
                            repeat_stmt();
                        }
                        case "write" -> {
                            match("write");
                            write_stmt();
                        }
                        case "read" -> {
                            match("read");
                            read_stmt();
                        }
                        default -> throw new Exception("incorrect statement "+scanner.getPrevioustoken().getTokenvalue());
                    }
                }break;
                case Identifier:
                {
                    match(TokenType.Identifier);
                    assign_stmt();
                }
                    break;
                default: {
                    throw new Exception("incorrect statement typing");
                }
            }
            System.out.println("statement compiled successfully");

        }
        private void if_stmt() throws Exception {
            exp();
           // scanner.PeekToken();
            match("then");
            stmt_sequence();
            if(scanner.getPrevioustoken().getTokenvalue().equals("else"))
            {
                match("else");
                stmt_sequence();
            }
            match("end");
            System.out.println("if statement compiled successfully");

        }
        private void repeat_stmt() throws Exception {
        stmt_sequence();
        match("until");
        exp();
            System.out.println("repeat statement compiled successfully");

        }
        private void assign_stmt() throws Exception {
        match(TokenType.Assignment_Operator);
        exp();
            System.out.println("assign statement compiled successfully");

        }
        private void write_stmt()throws Exception{
            exp();
            System.out.println(" write statement compiled successfully");

        }
        private void read_stmt()throws Exception
        {
            match(TokenType.Identifier);
            System.out.println("read statement compiled successfully");
        }
        private void exp() throws Exception {
            simple_exp();
            switch (scanner.getPrevioustoken().getTokenType())
            {
                case Greater_Than_Operator:
                    match(TokenType.Greater_Than_Operator);
                    simple_exp();
                    break;
                case Less_Than_Operator:
                    match(TokenType.Less_Than_Operator);
                    simple_exp();
                    break;
                case Equal_Operator:
                    match(TokenType.Equal_Operator);
                    simple_exp();
                    break;
                default:
                    break;
            }
            System.out.println("exp compiled successfully");
        }
        private void simple_exp() throws Exception {
            term();
            while (scanner.getPrevioustoken().getTokenType()==TokenType.Addition_Operator||
                    scanner.getPrevioustoken().getTokenType()==TokenType.Subtraction_Operator)
            {
                switch (scanner.getPrevioustoken().getTokenType())
                {
                    case Addition_Operator:
                        match(TokenType.Addition_Operator);
                        term();
                        break;
                    case Subtraction_Operator:
                        match(TokenType.Subtraction_Operator);
                        term();
                        break;
                    default:
                        break;
                }
            }
            System.out.println("simple exp compiled successfully");

        }
        private void term() throws Exception
        {
            factor();
            while (scanner.getPrevioustoken().getTokenType()==TokenType.Multiplication_Operator||
                    scanner.getPrevioustoken().getTokenType()==TokenType.Division_Operator)
            {
                switch (scanner.getPrevioustoken().getTokenType())
                {
                    case Multiplication_Operator:
                        match(TokenType.Multiplication_Operator);
                        factor();
                        break;
                    case Division_Operator:
                        match(TokenType.Division_Operator);
                        factor();
                        break;
                    default:
                        break;
                }
            }
            System.out.println("term compiled successfully");
        }
        private void factor() throws Exception
        {
           switch (scanner.getPrevioustoken().getTokenType())
           {
               case NUM:
               {
                   match(TokenType.NUM);
               }
               break;
               case Identifier:
               {
                   match(TokenType.Identifier);
               }
               break;
               case Open_Bracket:
               {
                   match(TokenType.Open_Bracket);
                   exp();
                   match(TokenType.Closed_Bracket);
               }
               break;
           }
            System.out.println("factor compiled successfully");
        }
        private void match(TokenType expected)throws Exception
        {
            if (scanner.getPrevioustoken().getTokenType()!=expected)
                throw new Exception("parsing syntax error expected token of type "+expected.toString()+" and got token of type"+
                        scanner.getPrevioustoken().getTokenType()+" and value"+scanner.getPrevioustoken().getTokenvalue());
            else scanner.PeekToken();
        }
    private void match(String  expected)throws Exception
    {
        if (!scanner.getPrevioustoken().getTokenvalue().equals(expected))
            throw new Exception("parsing syntax error expected token of value "+expected+" and got token of value"+
                    scanner.getPrevioustoken().getTokenvalue());
        else scanner.PeekToken();
    }

}
//vague enum was to be used if parser was a finite state machine
enum ParseOptions
{
start,Statement,Statement_Sequence,if_statement,repeat_statement,assign_statement,
    read_statement,write_statement,expression,simple_expression
    ,addop,term,mulop,factor;
}
