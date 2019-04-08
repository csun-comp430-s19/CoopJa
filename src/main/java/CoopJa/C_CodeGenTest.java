package CoopJa;

import java.util.ArrayList;
import java.lang.StringBuilder;

public class C_CodeGenTest
{
    /*(!) Can include stdio.h by default or
      can add it if there's a println statement,
      in that case not making it final.
    */
    private static final String programHeader = 
    "#include <stdio.h>\n"+
     "\n"+
     "\n"+
     "int main(int argc, char **argv)\n"+
     "{\n";
    
    private static final String programFooter = "\treturn 0;\n"+
     "}";
     
    private static StringBuilder finalOutputProgramString = new StringBuilder(programHeader);
  
    
    public static void main(String[] args)
    {          
        String programString = "public class example {" +
          "public String cool = \"Cool1\" + \"yeah\";" +
          "public void method1(int one, int two)" +
          "{" +
            "int one = 1;" +
          "}" +
        "}";
        
        
        
        //Get a println statement expression.
        String printLineString = "println(\"Print Me\")";
        ArrayList<Token> printLnTokens = Token.tokenize(printLineString);
        TokenParserInput printLnTokenList = new TokenParserInput(printLnTokens);
        
        MainParser parsers = new MainParser();
        PStatementPrintln printLnExpression = parsers.printlnParser.parse(printLnTokenList).getOrThrow();
        //PExpression fooTester2 = parsers.expressionLargeParser.parse(new TokenParserInput(fooToken)).getOrThrow();
        
        //Parse a program.
        ArrayList<Token> programTokenList = Token.tokenize(programString);
        TokenParserInput programTokenListInput = new TokenParserInput(programTokenList);

        PProgram programExpression = parsers.programParser.parse(programTokenListInput).getOrThrow();
        //println doesn't parse well, so making a literal one.
        PStatementPrintln expression = new PStatementPrintln(new Token("\"Print Me\""));
        
        //This var will hold the string of the 
        StringBuilder programContentString = new StringBuilder();
        
        
        try
        {
          //Add / append the program content until it's all processed.
          //Can be in a loop if needed.
          programContentString.insert(0, "\t");
          //Don't have any info on scope, I believe, so don't know how many \t to prepend.
          programContentString.append(printLnExpression.generateString());          
          //System.out.println(printLnExpression.generateString());
          //System.out.println("Class: "+fooTester2.getClass());
        }
        catch (CodeGenException e) 
        {
          e.printStackTrace();
        }
        
        //Append the program content to the programHeader at this point.
        finalOutputProgramString.append(programContentString.toString());
        finalOutputProgramString.append("\n");
        
        //Append the program Footer to the finalOutputProgramString.
        finalOutputProgramString.append(programFooter);
        
        System.out.println(finalOutputProgramString);
        
        //Send the program to be written to a file in the current dir.
        //writeCompleteFile(finalOutputProgramString.toString(), new File("HelloWorld.c"));
    }
    
    // public void writeCompleteFile(final String programString, final File file) throws IOException {
        // final PrintWriter output =
            // new PrintWriter(new BufferedWriter(new FileWriter(file)));
        // mainEnd(); //(!)(?)
        // try {
                //System.out.printf(programString);
            // }
        // } finally {
            // output.close();
        // }
    // } //end writeCompleteFile    
}