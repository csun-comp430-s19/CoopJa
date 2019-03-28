package CoopJa;

import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

public class M_Typecheck_UnitTests {
    public void testTypecheck(final String input) {
        try {
            ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
            Input<Token> tokenListInput = new TokenParserInput(tokenList);
            MainParser parsers = new MainParser(); //create MainParser object
            PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
            System.out.println();
            MExpressionTypeChecker typeChecker = new MExpressionTypeChecker(fooTester);
            typeChecker.typeCheck(); //call typechecker with parsed program obj
        } catch (Exception e) {
            System.err.println("Error detected properly");
            System.err.println(e);
        }
    }

    @Test
    public void testAll() {
        String foo = "public class foo2{" +
                "public string foo3 = 1 + \"string thingy\";" +
                "public string foo966;" +
                "public int foo8 = 1;" +
                "public int bar = foo8;" +
                "public boolean foofi = true | 1 < 2;" +
                "}";
        testTypecheck(foo);
    }

    @Test
    public void testBadIntAssignment() {
        String foo = "public class foo2{" +
                "public int foo3 = \"string thingy\";" +
                "}";
        testTypecheck(foo);
    }

    @Test
    public void testBadBoolean() {
        String foo = "public class foo2{" +
                "public boolean foo3;" +
                "public boolean = foo3 + 1" +
                "}";
        testTypecheck(foo);
    }

    @Test
    public void testGoodBoolean() {
        String foo = "public class foo2{" +
                "public boolean foo= false | true;" +
                "}";
        testTypecheck(foo);
    }

    @Test
    public void testGoodComplexBoolean() {
        String foo = "public class foo2{" +
                "public boolean foofi = true | 1 < 2;" +
                "}";
        testTypecheck(foo);
    }

    @Test
    public void testGoodStringIntConcat() {
        String foo = "public class foo2{" +
                "public string foo3 = \"string thingy\" + 1;" +
                "}";
        testTypecheck(foo);
    }
}
