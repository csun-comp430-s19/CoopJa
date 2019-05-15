package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

public class TC_UT2 { //temp second unit test class so no overwriting (NSA)
    public void testNewTypeChecker(String input) throws TypeCheckerException, Exception{
        ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser(); //create MainParser object
        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
        System.out.println();
        Typechecker.TypecheckMain(fooTester); //call typechecker with parsed program obj
    }

    public void goodTest (String foo) throws Exception {
        testNewTypeChecker(foo);
    }
    public void badTest (String foo){
        Exception myException = Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
        myException.printStackTrace();
    }

    @Test
    public void testBadReDeclareVar() {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "int foo1 = 1;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadScope() {
        String foo = "public class one {" +
                "public void main(int one) {" +
                "one = 1;" +
                "}" +
                "" +
                "public void main2(){" +
                "one = 1;" + //var not declared
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testVarScope() throws Exception {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "foo1 = 1;" +
                "}" +
                "}";
        testNewTypeChecker(foo);
    }

    @Test
    public void testDontAllowReDeclare() {
        String foo = "public class one {" +
                "public void main(int one) {" +
                "}" +
                "" +
                "int main;" +
                "}";
        badTest(foo);
    }

    @Test
    public void testDontAllowAssignmentExceptInMethod1() {
        String foo = "public class one {" +
                "int i = 0;" +
                "}";
        badTest(foo);
    }

    @Test
    public void testDontAllowAssignmentExceptInMethod2() throws Exception {
        String foo = "public class one {" +
                "int i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadForwardRefExtends() {
        String foo = "public class one extends two {" + //two is not known yet
                "int foo1;" +
                "}" +
                "public class two {" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadDefinitionString() { //bad way to define a string
        String foo = "public class one {" +
                "public string one;" + //it thinks "string" is an identifier of a class, that hasnt been defined -> fail
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodDefinitionString() throws Exception { //good way to define a string
        String foo = "public class one {" +
                "public String one;" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testExtends() throws Exception { //not yet
        String foo = "public class one {" +
                "int foo1;" +
                "}" +
                "public class two extends one {" +
                "public void main() {" +
                "foo1 = 0;" +
                "}" +
                "}";
        goodTest(foo);
    }
}
