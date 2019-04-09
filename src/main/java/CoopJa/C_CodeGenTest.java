package CoopJa;

import java.io.*;
import java.util.ArrayList;
import java.lang.StringBuilder;


public class C_CodeGenTest {
    /*(!) Can include stdio.h by default or
      can add it if there's a println statement,
      in that case not making it final.
    */
    private static StringBuilder programHeader;// = 
    //"#include <stdio.h>\n"; //(i)unimplemented)Should be empty, bc will add header files dynamically as needed.

    //(i)(unimplemented)There should be a newline between the headers & the functions.

    private static StringBuilder functionsStrBuilder;// = new StringBuilder();

    private static final String MAIN_START =
            "\n" +
                    "int main(int argc, char **argv)\n" +
                    "{\n";

    private static StringBuilder programContentString;

    private static final String MAIN_END =
            "\treturn 0;\n" +
                    "}";

    private static StringBuilder finalOutputProgramString;// = new StringBuilder(programHeader);

    public C_CodeGenTest() {
        programHeader = new StringBuilder("#include <stdio.h>\n#include <stdlib.h>\n#include <string.h>");
        //This var will hold the string of the program content going inside main().
        programContentString = new StringBuilder();
        functionsStrBuilder = new StringBuilder("\n");//newline separates functions from main().
        finalOutputProgramString = new StringBuilder();
    }

    public static void main(String[] args) throws IOException {

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
    public void generateCode(PStatementPrintln printLnExpression) throws IOException {
        try {
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

        } catch (CodeGenException e) {
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
        String tempfilename = "HelloWorld.c";


        //will make better solution later XXXXXXX
        //this converts the string to an arraylist of strings, for writing purposes
        ArrayList<String> progLines = StringtoArrayList(finalOutputProgramString.toString());

        writeCompleteFile(progLines, new File(""), tempfilename); //write output to a file

    }//end generateCode

    public void writeCompleteFile(final ArrayList<String> programString, final File dir, final String fileZname) throws IOException {
        //dir is the directory of the file including a slash at the end
        //filename is the name of the file plus extension

        File fileout;
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
} //end writeCompleteFile

    public static ArrayList<String> StringtoArrayList(String input) { //input a string, output will be an arraylist separating all new lines in the string
        ArrayList<String> out = new ArrayList<>();
        String output[] = input.split("\n"); //split by character

        for(String temp : output) {
            out.add(temp);
        }
        return out;
    }
}