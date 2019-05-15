package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

public class TC_UT2 {

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
    public void testBadWhileStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( \"Phosphophyllite\" ){" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }
}
