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

}