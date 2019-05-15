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

    @Test
    public void testBadReDeclareVar() {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "int foo1 = 1;" +
                "}" +
                "}";
        Exception myException = Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
        myException.printStackTrace();
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
        Exception myException = Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
        myException.printStackTrace();
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
        Exception myException = Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
        myException.printStackTrace();
    }

    @Test
    public void testDontAllowAssignmentExceptInMethod1() {
        String foo = "public class one {" +
                "int i = 0;" +
                "}";
        Exception myException = Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
        myException.printStackTrace();
    }

    @Test
    public void testDontAllowAssignmentExceptInMethod2() throws Exception {
        String foo = "public class one {" +
                "int i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}";
        testNewTypeChecker(foo);
    }
}
