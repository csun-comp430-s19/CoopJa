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

    @Test
    public void testBasicClass() {
        String program = "public class one {" +
                "int foo = 0;" +
                "public void main() {" +
                "int mainint1 = 0;" +
                "} }";

        ArrayList<String> ExpectedObjects = new ArrayList<String>();

        //class1
        ExpectedObjects.add("PClassDeclaration");
        //variables for class1
        ExpectedObjects.add("PVariableDeclaration");
        //funct1 for class1
        ExpectedObjects.add("PStatementFunctionDeclaration");
        //statements inside funct1
        ExpectedObjects.add("PVariableDeclaration");
        //at the end of class1
        ExpectedObjects.add("-");

        testParser(program, ExpectedObjects);
    }

    @Test
    public void testReturn() {
        String program = "public class one {" +
                "public int main() {" +
                "int yyy = 0;" +
                "return yyy;" +
                "}" +
                "}";

        ArrayList<String> ExpectedObjects = new ArrayList<String>();

        //class1
        ExpectedObjects.add("PClassDeclaration"); //required for class
        //funct1 for class1
        ExpectedObjects.add("PStatementFunctionDeclaration"); //required for any method dec
        //statements inside funct1
        ExpectedObjects.add("PVariableDeclaration");
        ExpectedObjects.add("PStatementReturn");
        //at the end of class1
        ExpectedObjects.add("-"); //required at end of class

        testParser(program, ExpectedObjects);
    }

    @Test
    public void testPrintStmt() {
        String program = "public class one {" +
                "public void main() {" +
                "println(\"Hello World\");" +
                "} }";

        ArrayList<String> ExpectedObjects = new ArrayList<String>();

        //class1
        ExpectedObjects.add("PClassDeclaration");
        //funct1 for class1
        ExpectedObjects.add("PStatementFunctionDeclaration");
        //statements inside funct1
        ExpectedObjects.add("PStatementPrintln");
        //at the end of class1
        ExpectedObjects.add("-");

        testParser(program, ExpectedObjects);
    }

    @Test
    public void testVarAss() {
        String program = "public class one {" +
                "public void main() {" +
                "var1 = 1;" +
                "var2 = 2;" +
                "var3 = 3;" +
                "}" +
                "}";

        ArrayList<String> ExpectedObjects = new ArrayList<String>();

        //class1
        ExpectedObjects.add("PClassDeclaration");
        //funct1 for class1
        ExpectedObjects.add("PStatementFunctionDeclaration");
        //statements inside funct1
        ExpectedObjects.add("PVariableAssignment");
        ExpectedObjects.add("PVariableAssignment");
        ExpectedObjects.add("PVariableAssignment");
        //at the end of class1
        ExpectedObjects.add("-"); //required at end of class

        testParser(program, ExpectedObjects);
    }

    @Test
    public void testVarious1() {
        String program = "public class one {" +
                "public void main() {" +
                "break;" +
                "run();" +
                "temp.run();" +
                "}" +
                "}";

        ArrayList<String> ExpectedObjects = new ArrayList<String>();

        //class1
        ExpectedObjects.add("PClassDeclaration");
        //funct1 for class1
        ExpectedObjects.add("PStatementFunctionDeclaration");
        //statements inside funct1
        ExpectedObjects.add("PStatementBreak");
        ExpectedObjects.add("PStatementFunctionCall");
        ExpectedObjects.add("PIdentifierReference");
        //at the end of class1
        ExpectedObjects.add("-"); //required at end of class

        testParser(program, ExpectedObjects);
    }

    @Test
    public void testCodeControl() {
        String program = "public class one {" +
                "public void main() {" +
                "if (1 == 1) {" +
                "break;" +
                "} else {" +
                "break;" +
                "}" +
                "for (int i = 0; i < 5; i = i+1;) {" +
                "printf(\"%d \", i);" +
                "}" +
                "while(1 == 1) {" +
                "break;" +
                "}" +
                "}" +
                "}";

        ArrayList<String> ExpectedObjects = new ArrayList<String>();

        //class1
        ExpectedObjects.add("PClassDeclaration");
        //funct1 for class1
        ExpectedObjects.add("PStatementFunctionDeclaration");
        //statements inside funct1
        ExpectedObjects.add("PStatementIfStatement");
        ExpectedObjects.add("PStatementForStatement");
        ExpectedObjects.add("PStatementWhileStatement");
        //at the end of class1
        ExpectedObjects.add("-"); //required at end of class

        testParser(program, ExpectedObjects);
    }

}