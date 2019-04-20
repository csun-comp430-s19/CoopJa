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
    public  void CodeGenForTest() throws IOException{
        TestCodeGenOutput("public class test{" +
                "public int main(){" +
                "for(int i = 1; i <= 3 ; i = i + 1;){" +
                "println(\"ow\");" +
                "}" +
                "}" +
                "}", "owowow");
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

    @Test
    public void SyntaxFullTest(){
        TestSyntaxCodeGen("public class test{\n" + //Input Starts HERE
                "    public int moduloHack(int x, int n){\n" +
                "        int p;\n" +
                "        int q;\n" +
                "        int m;\n" +
                "        q = x/n;\n" +
                "        p = q*n;\n" +
                "        m = x - p;\n" +
                "        return m;\n" +
                "    }\n" +
                "    \n" +
                "    public int main(){\n" +
                "        for (int i = 0; i <= 45; i = i + 1;){\n" +
                "            if (moduloHack(i, 15) == 0){\n" +
                "                println(\"FizzBuzz\");\n" +
                "            } \n" +
                "            else{\n" +
                "                if (moduloHack(i, 3)){\n" +
                "                    println(\"Fizz\");\n" +
                "                }\n" +
                "                else{\n" +
                "                    if (moduloHack(i ,5)){\n" +
                "                        println(\"Buzz\");\n" +
                "                    }else{}\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return 0;\n" +
                "    }\n" +
                "}"
                ,"#include <stdio.h>\n" + //Output starts HERE
                        "\n" +
                        "struct test{\n" +
                        "};\n" +
                        "int test_moduloHack (struct test* this,int x,int n){\n" +
                        "    int p;\n" +
                        "    int q;\n" +
                        "    int m;\n" +
                        "    q = (x)/(n);\n" +
                        "    p = (q)*(n);\n" +
                        "    m = (x)-(p);\n" +
                        "    return m;\n" +
                        "}\n" +
                        "int test_main (struct test* this){\n" +
                        "    for(int i = 0;(i)<=(45);i = (i)+(1)){\n" +
                        "    if ((test_moduloHack(this, i,15))==(0)){\n" +
                        "    printf(\"%s\\n\", \"FizzBuzz\");\n" +
                        "}\n" +
                        "else{\n" +
                        "    if (test_moduloHack(this, i,3)){\n" +
                        "    printf(\"%s\\n\", \"Fizz\");\n" +
                        "}\n" +
                        "else{\n" +
                        "    if (test_moduloHack(this, i,5)){\n" +
                        "    printf(\"%s\\n\", \"Buzz\");\n" +
                        "}\n" +
                        "else{\n" +
                        "};\n" +
                        "};\n" +
                        "};\n" +
                        "}\n" +
                        ";\n" +
                        "    return 0;\n" +
                        "}\n" +
                        "int main(int argc, char** argv){\n" +
                        "    struct test mainClass = {};\n" +
                        "    return test_main(&mainClass);\n" +
                        "}");
    }

    @Test
    public void SyntaxClassCreationTest(){
        TestSyntaxCodeGen("public class test{}",
                "#include <stdio.h>\n" +
                "\n" +
                "struct test{\n" +
                "};\n");
    }

    @Test
    public void SyntaxMethodCreationTest(){
        TestSyntaxCodeGen("public class test{" +
                "public int Main(){}" +
                "}",
                "#include <stdio.h>\n" +
                "\n" +
                "struct test{\n" +
                "};\n" +
                "int test_Main (struct test* this){\n" +
                "}\n");
    }

    @Test
    public void SyntaxInitializationTest(){
        TestSyntaxCodeGen("public class test{" +
                "public int Main(){" +
                "int i;" +
                "int j = 1;" +
                "}" +
                "}",
                "#include <stdio.h>\n" +
                "\n" +
                "struct test{\n" +
                "};\n" +
                "int test_Main (struct test* this){\n" +
                "    int i;\n" +
                "    int j = 1;\n" +
                "}\n");
    }

    @Test
    public void SyntaxExpressionTest(){
        TestSyntaxCodeGen("public class test{" +
                "public int Main(){" +
                "int i = 2;" +
                "int j = 1;" +
                "int k = 1 + 2;" +
                "k = i + j;" +
                "k = i - j;" +
                "k = i / j;" +
                "k = i * j;" +
                "}" +
                "}",
                "#include <stdio.h>\n" +
                "\n" +
                "struct test{\n" +
                "};\n" +
                "int test_Main (struct test* this){\n" +
                "    int i = 2;\n" +
                "    int j = 1;\n" +
                "    int k = (1)+(2);\n" +
                "    k = (i)+(j);\n" +
                "    k = (i)-(j);\n" +
                "    k = (i)/(j);\n" +
                "    k = (i)*(j);\n" +
                "}\n");
    }

    @Test
    public void SyntaxIfElseTest(){
        TestSyntaxCodeGen("public class test{" +
                "public int Main(){" +
                "if(true){}" +
                "else{}" +
                "}" +
                "}",
                "#include <stdio.h>\n" +
                "\n" +
                "struct test{\n" +
                "};\n" +
                "int test_Main (struct test* this){\n" +
                "    if (1){\n" +
                "}\n" +
                "else{\n" +
                "};\n" +
                "}\n");
    }

    @Test
    public void SyntaxForTest(){
        TestSyntaxCodeGen("public class test{" +
                "public int Main(){" +
                "for(int i = 1; i <= 10 ; i = i + 1;){}" +
                "}" +
                "}",
                "#include <stdio.h>\n" +
                "\n" +
                "struct test{\n" +
                "};\n" +
                "int test_Main (struct test* this){\n" +
                "    for(int i = 1;(i)<=(10);i = (i)+(1)){\n" +
                "}\n" +
                ";\n" +
                "}\n");
    }
    @Test
    public void SyntaxPrintTest(){
        TestSyntaxCodeGen("public class test{" +
                "public int Main(){" +
                "println(\"Rose Windmill\");" +
                "}" +
                "}","#include <stdio.h>\n" +
                "\n" +
                "struct test{\n" +
                "};\n" +
                "int test_Main (struct test* this){\n" +
                "    printf(\"%s\\n\", \"Rose Windmill\");\n" +
                "}\n");
    }
}


