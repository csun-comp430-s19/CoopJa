package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

//INFO: this is another class where Typechecker Unit Tests can be created
//      all unit tests that were here have been moved to the PROPER unit test class "Typechecker_UnitTests.java"
//Why have duplicate classes? -- It was used when multiple people at once were bug checking the Typechecker
////////////////////////

public class TC_UT1 {
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
    public void testGoodAutoReplace1() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "public void main() {" +
                "auto tempv = 7;" +
                "i = 0;" +
                "j = true;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testSuccessfulMergeParentChild1() throws Exception {
        String foo = "public class one {" +
                "int test1;" +
                "}" +
                "public class two extends one {" +
                "int testing2;" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testSuccessfulMergeParentChild2() throws Exception {
        String foo = "public class one {" +
                //"int test1;" + //proves empty class list works
                "}" +
                "public class two extends one {" +
                //"int testing2;" +
                "}";
        goodTest(foo);
    }
}
