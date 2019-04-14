package CoopJa;

import java.io.*;
import java.util.ArrayList;

public class J_CodeGen_ExpressionTest {

    public static void COMPILER(String fileName) throws IOException {
        // Delete existing exe
        File exeFile = new File(fileName.substring(0, fileName.lastIndexOf('.')) + ".exe");
        if (exeFile.exists()){
            exeFile.delete();
        }
        Runtime rt = Runtime.getRuntime();
        String COMMAND_1 = "tcc\\tcc.exe " + fileName;
        Process PROCESS_1 = rt.exec(COMMAND_1); //execute the tcc compiler and compile the given file
        try {
            PROCESS_1.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } // end COMPILER()

    public static String RUNFILE(String filename) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process newprocess = rt.exec(filename);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(newprocess.getInputStream()));

        // read the output from the command
        String s;
        s = stdInput.readLine();
        return s;
    }

    public static void generateCFile(String expression, String fileName){
        String cFile = "#include <stdio.h>\n" +
                "\n" +
                "int main(){\n" +
                "    int expressionTest = " + expression + ";\n" +
                "    printf(\"%d\\n\", expressionTest);\n" +
                "    return 0;\n" +
                "}";
        try {
            File outputFile = new File(fileName);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            else{
                outputFile.delete();
                outputFile.createNewFile();
            }

            Writer writer = new FileWriter(outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(cFile);
            bufferedWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        String testExpression = "10 + 6 /3 * 4 + (4/2) - 2 * 2";
        ArrayList<Token> fooToken = Token.tokenize(testExpression);
        MainParser parsers = new MainParser();
        PExpression fooTester2 = parsers.expressionLargeParser.parse(new TokenParserInput(fooToken)).getOrThrow();
        String testOutputExpression = null;
        try {
            testOutputExpression = fooTester2.generateString(null, null);
        } catch (CodeGenException e) {
            e.printStackTrace();
        }

        generateCFile(testOutputExpression, "ExpressionTest.c");
        try {
            COMPILER("ExpressionTest.c");
            String output = RUNFILE("ExpressionTest.exe");
            if (output.equals("16")){
                System.out.println("Good");
            }
            else{
                System.out.println("Bad");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}