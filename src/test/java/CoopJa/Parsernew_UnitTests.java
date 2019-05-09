package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.Arrays;

public class Parsernew_UnitTests {

    public void testParser(final String input, ArrayList<String> expected) {
        try {
            ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
            Input<Token> tokenListInput = new TokenParserInput(tokenList);
            MainParser parsers = new MainParser(); //create MainParser object
            PProgram parserOut = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var

            ArrayList<String> actual = MainParser.ParserObjects(parserOut, 0); //generate ArrayList<String> report of parser object

            for (int i = 0; i < actual.size(); i++) {
                Assertions.assertEquals(actual.get(i), expected.get(i));
            }
        } catch (Exception e) {
            System.err.println("bad");
            System.err.println(e);
        }
    }


    @Test
    public void test1() {
        String program = "public class one {" +
                "int testing = 0;" +
                "public void main(int param1) {" +
                "int cool = 0;" +
                "if (testvar == 1) {" +
                "testvar = 2;" +
                "} else {" +
                "testvar = 3;" +
                "} } }";

        ArrayList<String> ExpectedObjects = new ArrayList<String>();

        //class1
        ExpectedObjects.add("PClassDeclaration"); //required for class
        //variables for class1
        ExpectedObjects.add("PVariableDeclaration"); //required for any vardec
        //funct1 for class1
        ExpectedObjects.add("PStatementFunctionDeclaration"); //required for any method dec
        //parameters for funct1 in class1
        ExpectedObjects.add("PVariableDeclaration");
        //statements inside funct1
        ExpectedObjects.add("PVariableDeclaration");
        ExpectedObjects.add("PStatementIfStatement");
        //at the end of class1
        ExpectedObjects.add("-"); //required at end of class

        //class2
        //...

        testParser(program, ExpectedObjects);
    }

}