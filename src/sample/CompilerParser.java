package sample;

import java.util.ArrayList;

public class CompilerParser {
    CompilerScanner scanner;
    ArrayList<String> program;
    CompilerParser(CompilerScanner scanner){
        this.scanner=scanner;
        program=new ArrayList<String>();
    }
    public void parse() throws Exception {
            ParseOptions parserstate=ParseOptions.start;
            if(!scanner.HasMoreTokens()) return;
            MyToken CurrentToken=scanner.NextToken();
            switch (CurrentToken.getTokenType())
            {
                case Reserved_Keyword:
                {
                    if (CurrentToken.getTokenvalue()=="if")
                    {
                        parserstate=ParseOptions.if_statement;
                    }
                    else if (CurrentToken.getTokenvalue()=="then")
                    {

                    }
                    else if (CurrentToken.getTokenvalue()=="else")
                    {

                    }
                }

            }

        }

}
enum ParseOptions
{
start,Statement,Statement_Sequence,if_statement,repeat_statement,assign_statement,
    read_statement,write_statement,expression,simple_expression
    ,addop,term,mulop,factor;
}
