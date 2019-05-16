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
        Typechecker tempTypeC = new Typechecker(); //create typechecker object
        tempTypeC.TypecheckMain(fooTester); //call typechecker with pprogram obj
    }

    public void goodTest (String foo) throws Exception {
        testNewTypeChecker(foo);
    }
    public void badTest (String foo){
        Exception myException = Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
        myException.printStackTrace();
    }


    @Test
    public void testGoodAutoLarge() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "auto k;" +
                "auto p;" +
                "auto m;" +
                "auto n;" +
                "public void main() {" +
                "auto localvar = 7;" +
                "i = 0;" +
                "j = true;" +
                "k = \"Hello!\";" +
                "p = 15 / 3;" +
                "m = 1 < 2;" +
                "n = 99;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodAutotoAuto() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "public void main() {" +
                "i = 0;" +
                "j = i;" + //auto j given assignment (what was) auto i
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodAutoMultiMethod() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "public void main2() {" +
                "j = 9;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadAutotoAuto() {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "public void main() {" +
                "i = j;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodAutoMultiClass() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class two {" +
                "auto j;" +
                "public void main() {" +
                "j = 88;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadAutoNotResolvedMulti() {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "auto k;" +
                "public void main() {" +
                "i = 0;" +
                "i = i + 1;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadAutoNotResolved() {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodAutoMultiAssign() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "i = 5;" +
                "i = 7;" +
                "i = 100;" +
                "}" +
                "}";
        goodTest(foo);
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
