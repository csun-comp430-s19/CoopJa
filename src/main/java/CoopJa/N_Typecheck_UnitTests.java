package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.Arrays;

public class N_Typecheck_UnitTests {

    public void testTypecheck(final String input) {
        try {
            ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
            Input<Token> tokenListInput = new TokenParserInput(tokenList);
            MainParser parsers = new MainParser(); //create MainParser object
            PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
            System.out.println();
            N_Typecheck_Test.TypecheckMain(fooTester); //call typechecker with parsed program obj
        } catch (Exception e) {
            System.err.println("Error detected properly");
            System.err.println(e);
        }
    }

    @Test
    public void testRegularPass() {
        String foo = "public class foo{public int foo4 = 0;}" + //example string to be parsed
            "public class foo6 extends foo{public int foo4 = 1;}" +
            "public class foo2{" +
            //"public int foo3 = 0;" + //duplicate var able to be detected, not inside methods yet
            "public int foo3 = 0;" +
            "public int main(){" +
            "foo.foo4(); " +
            "foo3 = (1 + 9)*5;" +
            "for (int i = 0; i < 9; i = i+1;){" +
            "foo = foo + 5;" +
            "}" +
            "if (1 == 1){" +
            "int i = 0;" +
            "}" +
            "else{" +
            "int i = 1;" +
            "}" +
            "int i = 2;" +
            "return;" +
            "}" +
            "}";
        testTypecheck(foo);
    }

    @Test
    public void testImplicitExtends() { //CHANGE LATER
        String foo = "public class foo extends foo2{public int foo4 = 0;}" +
                "public class foo2 {int fooGood = 0;}";
        testTypecheck(foo);
    }

    @Test
    public void testProperExtends() {
        String foo = "public class foo {public int foo4 = 0;}" +
                "public class foo2 extends foo {int fooGood = 0;}";
        testTypecheck(foo);
    }

    @Test
    public void testBadExtends() {
        String foo = "public class foo extends foo3 {public int foo4 = 0;}" +
                "public class foo2 extends foo {int fooGood = 0;}";
        testTypecheck(foo);
    }

    @Test
    public void testDuplicateVar() {
        String foo = "public class foo{public int foo4 = 0;}" + //example string to be parsed
                "public class foo6 extends foo{public int foo4 = 1;}" +
                "public class foo2{" +
                "public int foo3 = 0;" + //duplicate var able to be detected, not inside methods yet
                "public int foo3 = 0;" +
                "public int main(){" +
                "foo.foo4(); " +
                "foo3 = (1 + 9)*5;" +
                "for (int i = 0; i < 9; i = i+1;){" +
                "foo = foo + 5;" +
                "}" +
                "if (1 == 1){" +
                "int i = 0;" +
                "}" +
                "else{" +
                "int i = 1;" +
                "}" +
                "int i = 2;" +
                "return;" +
                "}" +
                "}";
        testTypecheck(foo);
    }

    @Test
    public void testNameCollision() {
        String foo = "public class MainClass {public int foo = 0; public int foo = 0;}";
        testTypecheck(foo);
    }

    @Test
    public void testValidJavaConvention() { //this is valid Java convention, may or may not change, valid now
        String foo = "public class foo {public int foo = 0;}";
        testTypecheck(foo);
    }

    @Test
    public void testClassVarCollision() {
        String foo = "public class foo {" +
                "int foo = 0;" +
                "int foo = 0; }";
        testTypecheck(foo);
    }

    }