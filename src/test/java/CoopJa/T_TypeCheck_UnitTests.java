package CoopJa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.Arrays;

public class T_TypeCheck_UnitTests
{

    public void testTypeChecker(final String input) {
        try {
            ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
            Input<Token> tokenListInput = new TokenParserInput(tokenList);
            MainParser parsers = new MainParser(); //create MainParser object
            PProgram testProgTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
            System.out.println();
            N_Typecheck_Test.TypecheckMain(testProgTester); //call typechecker with parsed program obj
        } catch (Exception e) {
            System.err.println("Error detected properly");
            System.err.println(e);
        }
    }
    
    @Test
    public void testMethodDoubleDecker() {
        String testProg = "public "+
            "class testProg{"+
              "public int testInt = method(0);"+
            "}";
        testTypeChecker(testProg);
    }
    
    @Test
    public void testMethod() {
        String testProg = "public "+
            "class testProg{"+
              "public int testInt = method(0);"+
            "}";
      
        testTypeChecker(testProg);
    }

    @Test
    public void testMethodSignatureType() {
        String testProg = "public class testProg {"+
                            "public int main(int a, int b){"+
                              "return 4;"+
                            "}"+
                            "public boolean b = True;"+
                            "public int testInt = main(4,b);"+
                          "}";                        
        testTypeChecker(testProg);
    }

    @Test
    public void testMethodSignature() {
        String testProg = "public class testProg {"+
                            "public int main(int a){"+
                              "return 4;"+
                            "}"+
                            "public int testInt = main();"+
                          "}";                        
        testTypeChecker(testProg);
    }
}