package sample;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CompilerParser {
    String ParseStmt="";
    TextArea TA;
     CompilerScanner scanner;
     String CodeBody="";
     ArrayList<String> ProgramStatements;
     ArrayList<String> identifiers;
    CompilerParser(CompilerScanner scanner,TextArea ParserTA){
        this.TA=ParserTA;
        this.scanner=scanner;
        ProgramStatements=new ArrayList<>();
        identifiers=new ArrayList<>();
    }
    public void Generate_Cpp_Code() throws Exception {
        String Beginning="#include<iostream>\n" +
                        "using namespace std; \n" +
                        "int main()\n{\n ";
        String allidentifier="int ";
        parse();
        for (int i = 0; i < this.identifiers.size(); i++) {
            allidentifier+=identifiers.get(i);
            if (i==identifiers.size()-1)
                allidentifier+=";\n";
            else allidentifier+=",";
        }
        String Ending= "return 0;}";
        Stage newstage=new Stage();
        TextArea CPPTA=new TextArea();
        CPPTA.setEditable(false);
        newstage.setScene(new Scene(CPPTA));
        CPPTA.appendText(Beginning);
        CPPTA.setMinSize(800,800);
        CPPTA.appendText(allidentifier);
        CPPTA.appendText(this.CodeBody);
        CPPTA.appendText(Ending);
        newstage.setTitle("generated cpp code for your tiny language code");
        newstage.show();
        //todo continue the code generator
    }
    public void parse() throws Exception {
            identifiers.clear();
            CodeBody="";
            scanner.ResetTokenizer();
           // ParseOptions parserstate=ParseOptions.start;
            //if(!scanner.HasMoreTokens()) return;
            //MyToken CurrentToken=scanner.NextToken();
            program();
            this.scanner.ResetTokenizer();

        }
        private void program() throws Exception {
        this.ParseStmt="";
            //get the first token of the program to begin compiling
            scanner.PeekToken();
            stmt_sequence();
         ParseStmt+="program compiled successfully\n";
         this.TA.setText(ParseStmt);
        }
        private void stmt_sequence() throws Exception {
                statement();
                while (scanner.getPrevioustoken().getTokenType() == TokenType.Semicolon)
                {
                    match(TokenType.Semicolon);
                    statement();
                }
                if(scanner.HasMoreTokens()&&scanner.getPrevioustoken().getTokenType()!=TokenType.Reserved_Keyword)
                {
                    throw new Exception("statements are separated only by semicolons "+scanner.getPrevioustoken().getTokenvalue());
                }

        }
        private void statement() throws Exception {
            switch (scanner.getPrevioustoken().getTokenType())
            {
                case Reserved_Keyword:{
                    switch (scanner.getPrevioustoken().getTokenvalue()) {
                        case "if" -> {
                            if_stmt();
                        }
                        case "repeat" -> {
                            repeat_stmt();
                        }
                        case "write" -> {
                            write_stmt();
                        }
                        case "read" -> {
                            read_stmt();
                        }
                        default -> throw new Exception("incorrect statement "+scanner.getPrevioustoken().getTokenvalue());
                    }
                }break;
                case Identifier:
                {
                    if(!hasIdentifier(scanner.getPrevioustoken().getTokenvalue()))
                    {
                        identifiers.add(scanner.getPrevioustoken().getTokenvalue());
                    }
                    assign_stmt();
                }
                    break;
                default: {
                    throw new Exception("incorrect statement typing");
                }
            }
            ParseStmt+="statement compiled successfully\n";
        }
        private void if_stmt() throws Exception {
            match("if");
            generate("if (");
            exp();
            generate(") \n");
           // scanner.PeekToken();
            match("then");
            generate("{\n");
            stmt_sequence();
            generate("}\n");
            if(scanner.getPrevioustoken().getTokenvalue().equals("else"))
            {
                match("else");
                generate("else {\n");
                stmt_sequence();
                generate("}\n");
            }
            match("end");
            ParseStmt+="if statement compiled successfully\n";
        }
        private void repeat_stmt() throws Exception {
        match("repeat");
            generate("do {\n");
        stmt_sequence();
            generate("}\n");
        match("until");
            generate("while (!");
        exp();
            generate(");\n");
            ParseStmt+="repeat statement compiled successfully\n";
        }
        private void assign_stmt() throws Exception {
/*            if(!hasIdentifier(scanner.getPrevioustoken().getTokenvalue()))
            {
                identifiers.add(scanner.getPrevioustoken().getTokenvalue());
            }*/
            generate(scanner.getPrevioustoken().getTokenvalue());
            match(TokenType.Identifier);
            match(TokenType.Assignment_Operator);
            generate("=");
            exp();
            generate(";\n");
            ParseStmt+="assign statement compiled successfully\n";
        }
        private void write_stmt()throws Exception{
            generate("cout<<");
            match("write");
            exp();
            generate("<<endl;");
            ParseStmt+="write statement compiled successfully\n";
        }
        private void read_stmt()throws Exception
        {
            generate("cin>>");
            match("read");
            if(!hasIdentifier(scanner.getPrevioustoken().getTokenvalue()))
            {
                identifiers.add(scanner.getPrevioustoken().getTokenvalue());
            }
            generate(scanner.getPrevioustoken().getTokenvalue());
            match(TokenType.Identifier);
            generate(";\n");
            ParseStmt+="read statement compiled successfully\n";
        }
        private void exp() throws Exception {
            simple_exp();
            switch (scanner.getPrevioustoken().getTokenType())
            {
                case Greater_Than_Operator:
                    generate(">");
                    match(TokenType.Greater_Than_Operator);
                    simple_exp();
                    break;
                case Less_Than_Operator:
                    generate("<");
                    match(TokenType.Less_Than_Operator);
                    simple_exp();
                    break;
                case Equal_Operator:
                    generate("==");
                    match(TokenType.Equal_Operator);
                    simple_exp();
                    break;
                default:
                    break;
            }
            ParseStmt+="exp compiled successfully\n";
        }
        private void simple_exp() throws Exception {
            term();
            while (scanner.getPrevioustoken().getTokenType()==TokenType.Addition_Operator||
                    scanner.getPrevioustoken().getTokenType()==TokenType.Subtraction_Operator)
            {
                switch (scanner.getPrevioustoken().getTokenType())
                {
                    case Addition_Operator:
                        generate("+");
                        match(TokenType.Addition_Operator);
                        term();
                        break;
                    case Subtraction_Operator:
                        generate("-");
                        match(TokenType.Subtraction_Operator);
                        term();
                        break;
                    default:
                        break;
                }
            }
            ParseStmt+="simple exp compiled successfully\n";
        }
        private void term() throws Exception {
            factor();
            while (scanner.getPrevioustoken().getTokenType() == TokenType.Multiplication_Operator ||
                    scanner.getPrevioustoken().getTokenType() == TokenType.Division_Operator) {
                switch (scanner.getPrevioustoken().getTokenType()) {
                    case Multiplication_Operator:
                        generate("*");
                        match(TokenType.Multiplication_Operator);
                        factor();
                        break;
                    case Division_Operator:
                        generate("/");
                        match(TokenType.Division_Operator);
                        factor();
                        break;
                    default:
                        break;
                }
            }
            ParseStmt += "term compiled successfully\n";
        }
        private void factor() throws Exception
        {
           switch (scanner.getPrevioustoken().getTokenType())
           {
               case NUM:
               {
                   generate(scanner.getPrevioustoken().getTokenvalue());
                   match(TokenType.NUM);
               }
               break;
               case Identifier:
               {
                   if(!hasIdentifier(scanner.getPrevioustoken().getTokenvalue()))
                   {
                       throw new Exception(scanner.getPrevioustoken().getTokenvalue()+" is not defined");
                   }
                   generate(scanner.getPrevioustoken().getTokenvalue());
                   match(TokenType.Identifier);
               }
               break;
               case Open_Bracket:
               {
                   generate("(");
                   match(TokenType.Open_Bracket);
                   exp();
                   generate("(");
                   match(TokenType.Closed_Bracket);
               }
               break;
               default:throw new Exception ("not a factor type of token"+scanner.getPrevioustoken().getTokenvalue());
           }
           ParseStmt+="factor compiled successfully\n";
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
    private void generate(String NewEntry)
    {
        this.CodeBody+=NewEntry;
    }
    private boolean hasIdentifier(String identifier)
    {
        for (int i = 0; i < this.identifiers.size(); i++) {
            if(identifiers.get(i).equals(identifier))
                return true;
        }
        return false;
    }
}

//vague enum was to be used if parser was a finite state machine
enum ParseOptions
{
start,Statement,Statement_Sequence,if_statement,repeat_statement,assign_statement,
    read_statement,write_statement,expression,simple_expression
    ,addop,term,mulop,factor;
}
