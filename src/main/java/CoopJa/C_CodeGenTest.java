package CoopJa;

import java.util.ArrayList;
import java.lang.StringBuilder;


public class C_CodeGenTest
{
    /*(!) Can include stdio.h by default or
      can add it if there's a println statement,
      in that case not making it final.
    */
    private static StringBuilder programHeader;// = 
    //"#include <stdio.h>\n"; //(i)unimplemented)Should be empty, bc will add header files dynamically as needed.
    
    //(i)(unimplemented)There should be a newline between the headers & the functions.
    
    private static StringBuilder functionsStrBuilder;// = new StringBuilder();
    
    private static final String MAIN_START =
     "\n"+
     "int main(int argc, char **argv)\n"+
     "{\n";
     
    private static StringBuilder programContentString;
    
    private static final String MAIN_END = 
    "\treturn 0;\n"+
     "}";
     
    private static StringBuilder finalOutputProgramString;// = new StringBuilder(programHeader);
    
    public C_CodeGenTest()
    {
      programHeader = new StringBuilder("#include <stdio.h>\n");
      //This var will hold the string of the program content going inside main().
      programContentString = new StringBuilder();      
      functionsStrBuilder = new StringBuilder("\n");//newline separates functions from main().
      finalOutputProgramString = new StringBuilder();            
    }
    
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
        
        //Instantiate class to use the members.
        C_CodeGenTest codeGenerator = new C_CodeGenTest();
        //(!) Will pass PProgram eventually.
        codeGenerator.generateCode(printLnExpression);
        
    }
    
    
    //(!) Will make accept a PProgram & will return String, for now just prints.
    public void generateCode(PStatementPrintln printLnExpression)
    {
        try
        {
          //Add / append the program content until it's all processed.
          //Can be in a loop if needed.
          this.programContentString.insert(0, "\t");
          //_(!)programContentString.insert(0, getIndents(printLnExpression.getScope()));
          /*Actually, since we are diving into the code linearly, the scope will be directly known by
            this program.
          */
          
          //Don't have any info on scope, I believe, so don't know how many \t to prepend.
          this.programContentString.append(printLnExpression.generateString());

          
          //Add the necessary header files.
          // if(printLnExpression.includeRequired())
          // {
            // if( ! (includeExists(programHeader, printLnExpression.includeRequired())))
                // programHeader.append(printLnExpression.includeRequired());
          // }
          
          //System.out.println(printLnExpression.generateString());
          //System.out.println("Class: "+fooTester2.getClass());
          
        }
        catch (CodeGenException e) 
        {
          e.printStackTrace();
        }
        
        //Append the program headers
        finalOutputProgramString.append(programHeader.toString());
        //Append any functions.
        finalOutputProgramString.append(functionsStrBuilder.toString());
        //Append the main start
        finalOutputProgramString.append(MAIN_START); //not a stringBuilder.
        
        //Append the program content to the programHeader at this point.
        finalOutputProgramString.append(programContentString.toString());
        //Separate the program content from the return statement. 
        //_ Can be added in programContentString building? (?)
        finalOutputProgramString.append("\n");
        
        //Append the program Footer to the finalOutputProgramString.
        finalOutputProgramString.append(MAIN_END);
        
        System.out.println(finalOutputProgramString);
        
        //Send the program to be written to a file in the current dir.
        //writeCompleteFile(finalOutputProgramString.toString(), new File("HelloWorld.c"));          
    }//end generateCode
    
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