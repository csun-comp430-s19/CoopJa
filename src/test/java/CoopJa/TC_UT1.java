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
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadNestedIfBlockDeclarations(){
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "if(true){" +
                "int i;" +
                "if(true){" +
                "int i = 2;" +
                "}" +
                "else{" +
                "}" +
                "}" +
                "else {" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodWhileBlockDeclarations() throws Exception{
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "while(true){" +
                "foo1 = 2;" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadWhileBlockDeclarations(){
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "while(true){" +
                "int foo1 = 2;" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodScopeForStatement() throws Exception{
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "i = 3;" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadScopeForStatment(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "}" +
                "}" +
                "}" +
                "}";
        badTest(foo);
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
}
