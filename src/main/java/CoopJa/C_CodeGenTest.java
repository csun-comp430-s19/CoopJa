package CoopJa;

import java.util.ArrayList;

public class C_CodeGenTest
{
    public static void main(String[] args)
    {
        String foo = "true"; //println("1"+"Yes")
        ArrayList<Token> fooToken = Token.tokenize(foo);
        MainParser parsers = new MainParser();
        
        PExpression fooTester2 = parsers.expressionLargeParser.parse(new TokenParserInput(fooToken)).getOrThrow();
        
        //println doesn't parse well, so making a literal one.
        PStatementPrintln expression = new PStatementPrintln(new Token("\"Print Me\""));
        //PStatementPrintln expression = new PStatementPrintln();
        
        try
        {
          
          System.out.println(expression.generateString());
          //System.out.println("Class: "+fooTester2.getClass());
        } 
        catch (CodeGenException e) 
        {
          e.printStackTrace();
        }
    }
}