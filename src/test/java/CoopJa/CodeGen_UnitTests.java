package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.io.*;
import java.util.ArrayList;

public class CodeGen_UnitTests {

    public String ParseToProgramString(String inputString){
        ArrayList<Token> tokenList = Token.tokenize(inputString);
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser();

        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow();
        try {
            String programString = fooTester.generateProgramString();
            System.out.printf( "%s","\n" + programString + "\n");
            return programString;
        } catch (CodeGenException e) {
            e.printStackTrace();
            return "Failed To Generate Code";
        }
    }

    public void TestCodeGenOutput(String codeString, String expectedOutput) throws IOException{
        String cCodeOutput =  CompileAndRuncCode(codeString);
        Assertions.assertTrue(cCodeOutput.equals(expectedOutput), "Output and Input Differ" +
                "\nExpected: \n" + expectedOutput + "\nGot: \n" + cCodeOutput);
    }
    //similar to generateCFile in J_CodeGen but takes in the CoopJa code instead of the raw c code.
    public String CompileAndRuncCode(String codeString) throws IOException{
        String cFile = ParseToProgramString(codeString);
        String fileName = "UnitTestFile.c";
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
        J_CodeGen_ExpressionTest.COMPILER(fileName);
        return J_CodeGen_ExpressionTest.RUNFILE("UnitTestFile.exe");
    }

    @Test
    public void CodeGenPrintStringTest() throws IOException{
        TestCodeGenOutput("public class test{" +
                "public int main(){" +
                "println(\"Rose Windmill\");" +
                "}" +
                "}","Rose Windmill");
    }

    @Test
    public void CodeGenIfElseTest() throws IOException{
        TestCodeGenOutput("public class test{" +
                "public int main(){" +
                "int i = 1;" +
                "if(i == 1){" +
                "println(\"test \");" +
                "}" +
                "else{}" +
                "int j = 2;" +
                "if(j == 1){}" +
                "else{" +
                "println(\"passed\");" +
                "}" +
                "}" +
                "}","test passed");
    }
    @Test
    public void CodeGenWhileTest() throws IOException{
        TestCodeGenOutput("public class test{" +
                "public int main(){" +
                "int i = 1;" +
                "while(i < 4){" +
                "println(\"hey\");" +
                "i = i + 1;" +
                "}" +
                "}" +
                "}","heyheyhey");
    }
    @Test
    public void CodeGenForTest() throws IOException{
        TestCodeGenOutput("public class test{" +
                "public int main(){" +
                "for(int i = 1; i <= 3 ; i = i + 1;){" +
                "println(\"ow\");" +
                "}" +
                "}" +
                "}", "owowow");
    }
    @Test
    public void CodeGenClassTestFull() throws IOException{
        //from PProgram deletion pending
        TestCodeGenOutput("public class ClassTest{\n" +
                "    public int favoriteNumber;\n" +
                "    public int someOtherNumber;\n" +
                "    void setFavNumber(int number){\n" +
                "        favoriteNumber = number;\n" +
                "    }\n" +
                "    void guessFavNumber(int number){\n" +
                "        if (favoriteNumber == number){\n" +
                "            println(\"Correct\");\n" +
                "        }\n" +
                "        else{\n" +
                "            println(\"Incorrect\");\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test{\n" +
                "    public int main(){\n" +
                "        println(\"Hello World!\");\n" +
                "        ClassTest foo = new ClassTest;\n" +
                "        foo.setFavNumber(7);\n" +
                "        foo.guessFavNumber(7);\n" +
                "        foo.favoriteNumber = 6;\n" +
                "        foo.guessFavNumber(6);\n" +
                "        foo.someOtherNumber = 5;\n" +
                "        foo.favoriteNumber = foo.someOtherNumber;\n" +
                "        foo.guessFavNumber(5);\n" +
                "    }\n" +
                "}\n", "Hello World!CorrectCorrectCorrect");
    }

    @Test
    public void CodeGenClassSetter() throws IOException{
        TestCodeGenOutput(
                "public class ClassTest{" +
                    "public int niceInt;" +
                    "void setNiceInt(int number){" +
                        "niceInt = number;" +
                    "}" +
                "}" +
                "public class Test{" +
                    "public int main(){" +
                        "ClassTest foo = new ClassTest;" +
                        "foo.setNiceInt(1);" +
                        "if(foo.niceInt == 1){" +
                            "println(\"Success!\");" +
                        "}" +
                        "else{" +
                            "println(\"Failure!\");" +
                        "}" +
                    "}" +
                "}", "Success!");
    }

    @Test
    public void CodeGenFunctionCall() throws IOException{
        TestCodeGenOutput(
                "public class ClassTest{" +
                    "int AddTwo(int number){" +
                        "return number + 2;" +
                    "}" +
                "}" +
                "public class Test{" +
                    "public int main(){" +
                        "ClassTest foo = new ClassTest;" +
                        "if(foo.AddTwo(1) == 3){" +
                            "println(\"Success!\");" +
                        "}" +
                        "else{" +
                            "println(\"Failure!\");" +
                        "}" +
                    "}" +
                "}", "Success!");
    }

    //************************Test expressions*******************************

    public void TestExpressionCodeGen(final String inputExpr, final String inputName, final String real) {
        J_CodeGen_ExpressionTest.generateCFile(inputExpr, inputName + ".c");
        try {
            J_CodeGen_ExpressionTest.COMPILER( inputName + ".c");
            String output = J_CodeGen_ExpressionTest.RUNFILE(inputName + ".exe");
            System.out.println(output);
            Assertions.assertEquals(real, output, "!!_BAD_!! " + "output: " + output + " real: " + real);
        } catch (IOException e) {
            Assertions.assertEquals(real, "IOException");
        }
    }

    @Test
    public void testExpr() throws IOException, CodeGenException {
        TestExpressionCodeGen("(1) + (2)", "ExpressionTest", "3");
    }

    @Test
    public void testLastBadinput() throws IOException, CodeGenException {
        TestExpressionCodeGen("wrong", "ExpressionTest", "IOException");
    }

    @Test
    public void testExprMore1() throws IOException, CodeGenException {
        TestExpressionCodeGen("2 + 16 / 4 * 3 + 1 - 6", "ExpressionTest", "9");
    }

    @Test
    public void testExprMore2() throws IOException, CodeGenException {
        TestExpressionCodeGen("63 / 1 / 3 / 7 / 1", "ExpressionTest", "3");
    }

    @Test
    public void testExprMore3() throws IOException, CodeGenException {
        TestExpressionCodeGen("256 / 2 * 3 -50 / 5 + 26", "ExpressionTest", "400");
    }

    @Test
    public void testExprMore4() throws IOException, CodeGenException {
        TestExpressionCodeGen("9 + 7 - 20", "ExpressionTest", "-4");
    }

    @Test
    public void testExprMore5() throws IOException, CodeGenException {
        TestExpressionCodeGen("( 8 * 7 - 1 ) / 5", "ExpressionTest", "11");
    }

    @Test
    public void testExprMore6() throws IOException, CodeGenException {
        TestExpressionCodeGen("38 / 3 * 2 - 5 / 2", "ExpressionTest", "22");
    }

    //*************************************** Unit tests for Syntax **********************************
    public void TestSyntaxCodeGen(String input, String expectedOutPut){
        String givenOutput = ParseToProgramString(input);
        Assertions.assertTrue(givenOutput.equals(expectedOutPut), "Output and Input Differ" +
                "\nExpected: \n" + expectedOutPut + "\nGot: \n" + givenOutput);
    }
    //removed syntax tests due to uselessness
}


