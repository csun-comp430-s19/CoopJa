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

    @Test
    public void testVar1() throws Exception{
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "int foo1 = 1;" +
                "}" +
                "}";
        Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
    }

    @Test
    public void testGoodWhileStatement() throws Exception{ ////fix upppp
        String foo = "public class one {" +
                "int foo1;" +
                "public void main(int one) {" + //int one
                "foo1 = 1;" +
                "one = 1;" +
                "int foo2 = 0;" +
                //"int foo2 = 9;" +
                "foo2 = 9;" +
                "}" +
                "" +
                "public void main2(int two){" +
                "one = 1;" + //params still global -- declared as main() param, but doesnt fail here
                "}" +
                //"int foo2;" +
                "}";
        Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
    }

    @Test
    public void testBadWhileStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( \"Phosphophyllite\" ){" +
                "}" +
                "}" +
                "}";
        Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
    }
}
