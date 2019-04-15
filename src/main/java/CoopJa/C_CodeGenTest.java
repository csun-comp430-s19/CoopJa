package CoopJa;

import java.io.*;
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
    
    private static StringBuilder structSectionStrBuilder;

    private static StringBuilder functionSectionStrBuilder;// = new StringBuilder();

    private static final String MAIN_START =
                    "\n" +
                    "int main(int argc, char **argv)\n" +
                    "{\n";

    private static StringBuilder programContentStrBuilder;

    private static final String MAIN_END = "" + ///XXXXXXXXXXwill clean up later
                    "\treturn 0;\n" +
                    "}";

    private static StringBuilder finalOutputProgramStrBuilder;// = new StringBuilder(programHeader);

    public C_CodeGenTest() 
    {
        programHeader = new StringBuilder("#include <stdio.h>\n");
                              // "#include <stdio.h>\n"+
                              // "#include <stdlib.h>\n"+
                              // "#include <string.h>"); ////XXXXXXXXXXXXXXxwill clean up later

        structSectionStrBuilder = new StringBuilder();
        functionSectionStrBuilder = new StringBuilder("\n");//newline separates functions from main().
        //This var will hold the string of the program content going inside main().
        programContentStrBuilder = new StringBuilder();
        finalOutputProgramStrBuilder = new StringBuilder();
    }

    public static void main(String[] args) throws IOException 
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
        //codeGenerator.generateCode(printLnExpression);
        codeGenerator.generateCode(programExpression);

    }// main()

    //(!) Will make accept a PProgram & will return String, for now just prints.
    public void generateCode(final PProgram pProgram) //throws IOException 
    {
        //Get reference to pProgram
        PProgram inputProgram = pProgram;
        try 
        {
            //Go through the class declarations.
            for(PClassDeclaration classDeclaration : inputProgram.classDeclarationList)
            {   
                //generate the code for the class Declarations.
                generateCode(classDeclaration, 0);
            } //(!) Delete unneeded lines after this in this method.
            //Add / append the program content until it's all processed.
            //Can be in a loop if needed.
            //_(!)programContentStrBuilder.insert(0, getIndents(printLnExpression.getScope()));
          /*Actually, since we are diving into the code linearly, the scope will be directly known by
            this program.
          */
            //Don't have any info on scope, I believe, so don't know how many \t to prepend.

            //Add the necessary header files.
            // if(printLnExpression.includeRequired())
            // {
            // if( ! (includeExists(programHeader, printLnExpression.includeRequired())))
            // programHeader.append(printLnExpression.includeRequired());
            // }

            //System.out.println(printLnExpression.generateString());
            //System.out.println("Class: "+fooTester2.getClass());

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        //Append the program headers
        finalOutputProgramStrBuilder.append(programHeader.toString());
        //Append structs to the program
        finalOutputProgramStrBuilder.append(structSectionStrBuilder.toString());
        //Append any functions.
        finalOutputProgramStrBuilder.append(functionSectionStrBuilder.toString());
        //Append the main start
        finalOutputProgramStrBuilder.append(MAIN_START); //not a stringBuilder.

        //Append the program content to the programHeader at this point.
        finalOutputProgramStrBuilder.append(programContentStrBuilder.toString());
        //Separate the program content from the return statement. 
        //_ Can be added in programContentStrBuilder building? (?)
        finalOutputProgramStrBuilder.append("\n");

        //Append the program Footer to the finalOutputProgramStrBuilder.
        finalOutputProgramStrBuilder.append(MAIN_END);

        System.out.println(finalOutputProgramStrBuilder);

        //Send the program to be written to a file in the current dir.
        String outputFileName = "HelloWorld.c";

        //will make better solution later XXXXXXX
        //this converts the string to an arraylist of strings, for writing purposes need platform's line separator.
        //ArrayList<String> progLines = StringtoArrayList(finalOutputProgramStrBuilder.toString());
        //writeCompleteFile(progLines, new File(""), outputFileName); //write output to a file

    }//end generateCode PProgram
    
    public static void generateCode(final PClassDeclaration classDeclaration, final int scopeLevel)
    {
        //scopeLevel is not used since classDeclaration should be in the 0th scope, but here for consistency.
        /*This class is responsible for making the complete struct & its members.*/
        /*(!)(?) How will class extension be handled? 
          One possibility is just adding the same members from the class that's being extended.
        */
        
        StringBuilder structStrBuilder = new StringBuilder(
                                          "typedef struct"+ "\n" + /*classDeclaration.identifier.getTokenString()*/
                                          "{\n");
        //Now we add any variable & function declarations it might have.
        for(PDeclaration pDeclaration : classDeclaration.declarationList)
        {   
            //Increment the scope level to tab it.
            //generateCode will return a string of the pDeclaration
            /*(i)Functions should be added in 3 places: 
                inside struct they're declared,
                function area above main(),
                in program content inside main() assigned to function() * of struct (member).
                The identifier in struct takes precedence, just as identifier of struct instance
                takes precedence over struct name.
                //Constructor will be treated differently. It will just be an initial assignment in main().
            */
            if(pDeclaration instanceof PVariableDeclaration)
            {                
                generateCode((PVariableDeclaration)pDeclaration, scopeLevel+1, structStrBuilder);                
            }
            if(pDeclaration instanceof PStatementFunctionDeclaration)
            {      
                generateCode((PStatementFunctionDeclaration)pDeclaration, scopeLevel+1, structStrBuilder);
            }
        }//end adding struct member declarations.

        //Add the name of the struct so we can declare like: MyClass classInstance;
        structStrBuilder.append("} " + classDeclaration.identifier.getTokenString() + ";\n");
        //Add the entire struct to the struct section.
        structSectionStrBuilder.append(structStrBuilder.toString());
    }
    
    //This method adds to the class' static members for function section &, but it adds t
    public static void generateCode(final PStatementFunctionDeclaration functionDeclaration, final int scopeLevel, StringBuilder structStrBuilder)
    {
      /* This method's job is to generate the code for the struct's methods*/
      //(!)(!) Check if it's a constructor, so we need the name of the class, else in previous callback
      //_ we can check before calling this function, and call another function instead.
      StringBuilder functionSectionBuilder = new StringBuilder();
      StringBuilder functionInMainBuilder = new StringBuilder();
      // StringBuilder functionPtrInStructBuilder = new StringBuilder();
      
      
      /*Begin add function to struct as function pointer*/
      addTabs(structStrBuilder, scopeLevel);
      //Make it a function pointer.
      structStrBuilder.append(functionDeclaration.returnType.getTokenString() +
                                " (*" + functionDeclaration.identifier.getTokenString() + ")");
                                // "(");
      //Add parameters list.
      structStrBuilder.append("(");
      //for(PVariableDeclaration parameterDeclaration : functionDeclaration.variableDeclarations)
      for(int i = 0; i < functionDeclaration.variableDeclarations.size() ; i++)
      {
          generateCodeParameter(structStrBuilder, functionDeclaration.variableDeclarations.get(i));
          if(i+1 < functionDeclaration.variableDeclarations.size())
              structStrBuilder.append(", ");
      }
      structStrBuilder.append(");\n");
      /*End add function pointer to struct.*/
      
      /*Begin add function to function section*/
      //(!)(!) Try keeping both the member & declared function w/ the same name, see if there's a conflict.
      functionSectionBuilder.append(functionDeclaration.returnType.getTokenString() +
                                    functionDeclaration.identifier.getTokenString());
      //Add parameters list. 
      functionSectionBuilder.append("(");
      //for(PVariableDeclaration parameterDeclaration : functionDeclaration.variableDeclarations)
      for(int i = 0; i < functionDeclaration.variableDeclarations.size() ; i++)
      {
          generateCodeParameter(functionSectionBuilder, functionDeclaration.variableDeclarations.get(i));
          if(i+1 < functionDeclaration.variableDeclarations.size())
              functionSectionBuilder.append(", ");
      }
      functionSectionBuilder.append(")\n");
      //Build the body of the function.
      functionSectionBuilder.append("{\n");
      //Add the statements in the body.

      
      //Add the return if any.
      //Close the body of the function.
      functionSectionBuilder.append("}\n");
      //Add it to the static function
      functionSectionStrBuilder.append(functionSectionBuilder.toString());
      /*End add function to function section*/

      /*Begin add function in main()*/
      /*End add function in main()*/
      
    } // generateCode for functionDeclarations.
    
    public static void generateCode(final PVariableDeclaration variableDeclaration, final int scopeLevel, StringBuilder structStrBuilder)
    {
      /* This method's job is to generate the code for the structs methods & variables. or branch*/
    }// generateCode for varDeclarations.

    public static void generateCodeParameter(StringBuilder structStrBuilder, final PVariableDeclaration parameterDeclaration)
    {
        structStrBuilder.append(parameterDeclaration.variableType.getTokenString() + " " + parameterDeclaration.identifier.getTokenString());
    }//code generation parameters.

    public static void addTabs(StringBuilder strB, final int scopeLevel)
    {
        for(int i = 0; i < scopeLevel; i++)
        {
            strB.append("\t");
        }
    }//addTabs
/********************************************************************************************/
    //(!) Will make accept a PProgram & will return String, for now just prints.
    public void generateCode(PStatementPrintln printLnExpression) throws IOException 
    {
        try {
            //Add / append the program content until it's all processed.
            //Can be in a loop if needed.
            this.programContentStrBuilder.insert(0, "\t");
            //_(!)programContentStrBuilder.insert(0, getIndents(printLnExpression.getScope()));
          /*Actually, since we are diving into the code linearly, the scope will be directly known by
            this program.
          */

            //Don't have any info on scope, I believe, so don't know how many \t to prepend.
            this.programContentStrBuilder.append(printLnExpression.generateString());


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
        finalOutputProgramStrBuilder.append(programHeader.toString());
        //Append structs to the program
        finalOutputProgramStrBuilder.append(structSectionStrBuilder.toString());
        
        //Append any functions.
        finalOutputProgramStrBuilder.append(functionSectionStrBuilder.toString());
        //Append the main start
        finalOutputProgramStrBuilder.append(MAIN_START); //not a stringBuilder.

        //Append the program content to the programHeader at this point.
        finalOutputProgramStrBuilder.append(programContentStrBuilder.toString());
        //Separate the program content from the return statement. 
        //_ Can be added in programContentStrBuilder building? (?)
        finalOutputProgramStrBuilder.append("\n");

        //Append the program Footer to the finalOutputProgramStrBuilder.
        finalOutputProgramStrBuilder.append(MAIN_END);

        System.out.println(finalOutputProgramStrBuilder);

        //(!) Stopped copying to other method(PProgram ) from here.
        //Send the program to be written to a file in the current dir.
        String tempfilename = "HelloWorld.c";


        //will make better solution later XXXXXXX
        //this converts the string to an arraylist of strings, for writing purposes
        ArrayList<String> progLines = StringtoArrayList(finalOutputProgramStrBuilder.toString());

        writeCompleteFile(progLines, new File(""), tempfilename); //write output to a file

    }//end generateCode

    public void writeCompleteFile(final ArrayList<String> programString, final File dir, final String fileZname) throws IOException {
        //dir is the directory of the file including a slash at the end
        //filename is the name of the file plus extension

        File fileout;
        System.out.println();
        if (dir.length() != 0) { //if dir is not blank
            System.out.println("dir not blank");
            if (!dir.exists()) { //if dir doesnt exist
                System.out.println("creating dir");
                dir.mkdir(); //create directory
            } else {
                System.out.println("dir exists");
            }
            fileout = new File(dir, fileZname); //create file out of name + dir
        } else {
            System.out.println("dir is blank");
            fileout = new File(fileZname); //create file from name
        }

        fileout.createNewFile();
        FileWriter filewrite = new FileWriter(fileout);

        try {
            for (int i = 0; i < programString.size(); i++) { //for every newline
                filewrite.write(programString.get(i)); //write the line
                filewrite.write(System.getProperty("line.separator")); //write a newline char
                filewrite.flush();
            }
            System.out.println("file has been outputed");
        } catch (Exception e) {
            System.err.println(e);
            //throw error?
        }

        COMPILER(fileout); //////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    } //end writeCompleteFile

    public static ArrayList<String> StringtoArrayList(String input) { //input a string, output will be an arraylist separating all new lines in the string
        ArrayList<String> out = new ArrayList<>();
        String output[] = input.split("\n"); //split by character

        for(String temp : output) {
            out.add(temp);
        }
        return out;
    }

    public static void COMPILER(File file) throws IOException {
        final String loc = ""; //location of our project on your computer !!_NOTICE_!!
        String UPGRADEloc = loc + "tcc"; //where tcc compiler is in our proj folder
        String fileloc;
        String dothis = "tcc "; //command to compile the file, NOT execute, must be done separately
        final String COMMAND_1;
        System.out.println();
        System.out.println(file.getPath());
        System.out.println(file.getName());
        if (file.getPath().equals(file.getName())) { //location of the file is in root dir?
            fileloc = loc; //save the location of the file if not specified (proj dir by default)
        } else {
            fileloc = file.getPath();
        }
        dothis += fileloc + "..\\" + file.getName();
        Runtime rt = Runtime.getRuntime();
        //COMMAND_1 = "cmd.exe /c cd \"" + UPGRADEloc + "\" & start cmd.exe /k \"" + dothis + "";
        //COMMAND_1 = "cmd.exe /k print hello";
        COMMAND_1 = "tcc\\tcc.exe HelloWorld.c";
        Process PROCESS_1 = rt.exec(COMMAND_1); //execute the tcc compiler and compile the given file
        try {
            PROCESS_1.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //NOTE: output file is currently set to default location with tcc.exe XXXXXXX
        //NOTE: currently output file is not executed or read XXXXXX
        RUNFILE(PROCESS_1, UPGRADEloc, file);
    } // end COMPILER()

    public static void RUNFILE(Process inputProcess, String UPGRADEloc, File file) throws IOException {
        String[] split = file.getName().split("\\.");
        System.out.println(split[0]);
        String two = split[0] + ".exe"; // + "\" & start cmd.exe /k \"" + dothis + ""
        System.out.println(two);
        //String three = "cmd.exe /c cd \"" + UPGRADEloc + "\" & start cmd.exe /c \"" + two;
        String three = "HelloWorld.exe";
        Runtime rt = Runtime.getRuntime();
        Process newprocess = rt.exec(three);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(newprocess.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(newprocess.getErrorStream()));

// read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

// read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

}