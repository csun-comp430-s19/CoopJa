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

    private static final String MAIN_END = "system(\"pause\");\n" + ///XXXXXXXXXXwill clean up later
            "\treturn 0;\n" +
                    "}";

    private static StringBuilder finalOutputProgramString;// = new StringBuilder(programHeader);

    public C_CodeGenTest() {
        programHeader = new StringBuilder("#include <stdio.h>\n#include <stdlib.h>\n#include <string.h>"); ////XXXXXXXXXXXXXXxwill clean up later
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
        final String loc = "C:\\Users\\NSA\\Documents\\GitHub\\CoopJa"; //location of our project on your computer !!_NOTICE_!!
        String UPGRADEloc = loc + "\\tcc"; //where tcc compiler is in our proj folder
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
        dothis += fileloc + "\\" + file.getName();
        Runtime rt = Runtime.getRuntime();
        COMMAND_1 = "cmd.exe /c cd \"" + UPGRADEloc + "\" & start cmd.exe /k \"" + dothis + "";
        Process PROCESS_1 = rt.exec(COMMAND_1); //execute the tcc compiler and compile the given file
        //NOTE: output file is currently set to default location with tcc.exe XXXXXXX
        //NOTE: currently output file is not executed or read XXXXXX
        ////RUNFILE(PROCESS_1, UPGRADEloc, file);
    } // end COMPILER()

    public static void RUNFILE(Process inputProcess, String UPGRADEloc, File file) throws IOException {
        String[] split = file.getName().split("\\.");
        System.out.println(split[0]);
        String two = UPGRADEloc + "\\" + split[0] + ".exe"; // + "\" & start cmd.exe /k \"" + dothis + ""
        System.out.println(two);
        String three = "cmd.exe /c cd \"" + UPGRADEloc + "\" & start cmd.exe /k \"" + two;
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