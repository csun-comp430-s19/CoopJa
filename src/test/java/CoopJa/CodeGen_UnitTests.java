package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CodeGen_UnitTests {

    public void testCodeGen(final String inputExpr, final String inputName, final String real) {
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
        testCodeGen("(1) + (2)", "ExpressionTest", "3");
    }

    @Test
    public void testLastBadinput() throws IOException, CodeGenException {
        testCodeGen("wrong", "ExpressionTest", "IOException");
    }

    @Test
    public void testExprMore1() throws IOException, CodeGenException {
        testCodeGen("2 + 16 / 4 * 3 + 1 - 6", "ExpressionTest", "9");
    }

    @Test
    public void testExprMore2() throws IOException, CodeGenException {
        testCodeGen("63 / 1 / 3 / 7 / 1", "ExpressionTest", "3");
    }

    @Test
    public void testExprMore3() throws IOException, CodeGenException {
        testCodeGen("256 / 2 * 3 -50 / 5 + 26", "ExpressionTest", "400");
    }

    @Test
    public void testExprMore4() throws IOException, CodeGenException {
        testCodeGen("9 + 7 - 20", "ExpressionTest", "-4");
    }

    @Test
    public void testExprMore5() throws IOException, CodeGenException {
        testCodeGen("( 8 * 7 - 1 ) / 5", "ExpressionTest", "11");
    }

    @Test
    public void testExprMore6() throws IOException, CodeGenException {
        testCodeGen("38 / 3 * 2 - 5 / 2", "ExpressionTest", "22");
    }

    // Unit tests for New Parser
    public void TestNewCodeGen(String input, String expectedOutPut){
        String givenOutput = ParseToProgramString(input);
        Assertions.assertTrue(givenOutput.equals(expectedOutPut), "Output and Input Differ" +
                "\nExpected: \n" + expectedOutPut + "\nGot: " + givenOutput);
    }

    public String ParseToProgramString(String inputString){
        ArrayList<Token> tokenList = Token.tokenize(inputString);
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser();

        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow();
        try {
            String programString = fooTester.generateProgramString();
            System.out.printf("%s", programString);
            return programString;
        } catch (CodeGenException e) {
            e.printStackTrace();
            return "Failed To Generate Code";
        }
    }

    @Test
    public void NewFullTest(){
        TestNewCodeGen("public class test{\n" +
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
                ,"#include <stdio.h>\n" +
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


}