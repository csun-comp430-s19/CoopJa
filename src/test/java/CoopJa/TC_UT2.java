package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

//INFO: this is another class where Typechecker Unit Tests can be created
//      all unit tests that were here have been moved to the PROPER unit test class "Typechecker_UnitTests.java"
//Why have duplicate classes? -- It was used when multiple people at once were bug checking the Typechecker
////////////////////////

public class TC_UT2 { //temp second unit test class so no overwriting (NSA)
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

    //put tests here

    @Test
    public void testGoodAutoLarge() throws Exception {
        String foo = "public class parent {" +
                "public int returnint(){" + //same method
                "return 0;" +
                "}" +
                "}" +
                "public class child extends parent {" +
                "public int returnint() {" + //same method
                "return 50;" +
                "}" +
                "}" +
                "public class one {" +
                "public int main() {" +
                "parent Parent1 = new parent;" +
                "printf(\"%d\", Parent1.returnint());" +
                "child Child1 = new child;" +
                "printf(\"%d\", Child1.returnint());" +
                "}" +
                "}";
        goodTest(foo);
    }
}
