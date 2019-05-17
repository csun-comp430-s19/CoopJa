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

    //put tests here


    @Test
    public void testGoodExtendsReplaceParent() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public void main() {" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadExtendsReplaceParent() {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public int main() {" + //child main() has different return type
                "return 0;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodReplaceParentMethod() throws Exception {
        String foo = "public class onehundredfiftyseven {" +
                "auto i;" +
                "public void mainREALLL() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class twohundred extends onehundredfiftyseven {" +
                "int testing;" +
                "public void mainREALLL() {" +
                "testing = 99;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testLargeReplaceParent() throws Exception {
        String foo = "public class one {" +
                "public void main1() {" +
                "}" +
                "public void main2() {" +
                "}" +
                "public void main3() {" +
                "}" +
                "public void main4() {" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public void main1() {" +
                "}" +
                "public void main2() {" +
                "}" +
                "public void main3() {" +
                "}" +
                "public void main4() {" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testReplaceMultipleClasses() throws Exception {
        String foo = "public class one {" +
                "public void main1() {" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public void main1() {" +
                "}" +
                "}" +
                "public class parent {" +
                "public void coolMethod() {" +
                "int i = 0;" +
                "}" +
                "}" +
                "public class child extends parent {" +
                "public void coolMethod() {" +
                "int j = 0;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testIntegerIdentifierIssue() throws Exception{
        String foo = "public class ClassTest{" +
                "public int assignable;" +
                "}" +
                "public class Test{" +
                "public int main(){" +
                "ClassTest foo = new ClassTest;" +
                "foo.assignable = 2;" +
                "if(foo.assignable == 2){" +
                "println(\"Success!\");" +
                "}" +
                "else{" +
                "println(\"Failure!\");" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }
}
