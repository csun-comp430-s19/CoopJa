package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

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
    public void testGoodIfBlockDeclarations() throws Exception{
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "if(true){" +
                "int i;" +
                "i = 1;" +
                "}" +
                "else {" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadIfBlockDeclarations(){
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "if(true){" +
                "int i;" +
                "}" +
                "else {" +
                "}" +
                "i = 2;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodNestedIfBlockDeclarations() throws Exception{
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "if(true){" +
                "int i;" +
                "if(true){" +
                "i = 2;" +
                "}" +
                "else{" +
                "}" +
                "}" +
                "else {" +
                "}" +
                "i = 2;" +
                "}" +
                "}";
        goodTest(foo);
    }
}
