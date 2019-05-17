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
    public void testW1112222() throws Exception {
        String foo = "public class ClassTest{\n" +
                "    public int favoriteNumber;\n" +
                "    public int someOtherNumber;\n" +
                "    void setFavNumber(int number){\n" +
                "        favoriteNumber = number;\n" +
                "    }\n" +
                "    void guessFavNumber(int number){\n" +
                "        if (favoriteNumber == number){\n" +
                "            println(\"Correct\");\n" +
                "        }\n" +
                "        else{\n" +
                "            println(\"Incorrect\");\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test{\n" +
                "    public int main(){\n" +
                "        println(\"Hello World!\");\n" +
                "        ClassTest foo = new ClassTest;\n" +
                "        foo.setFavNumber(7);\n" +
                "        foo.guessFavNumber(7);\n" +
                "        foo.favoriteNumber = 6;\n" +
                "        foo.guessFavNumber(6);\n" +
                "        foo.someOtherNumber = 5;\n" +
                "        foo.favoriteNumber = foo.someOtherNumber;\n" +
                "        foo.guessFavNumber(5);\n" +
                "    }\n" +
                "}\n";
        goodTest(foo);
    }


}
