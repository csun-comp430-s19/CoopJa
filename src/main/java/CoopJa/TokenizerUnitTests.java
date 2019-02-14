package main.java.CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
public class TokenizerUnitTests {
    public void assertTokenizes(final String input, final ArrayList<Token> expected){
        final Token tokenizer = new Token(input);
        try{
            final ArrayList<Token> recieved = Token.tokenize(input);
            Assertions.assertEquals(recieved, expected,
                    "Tokenizer Failed, got: " + recieved + " \n expected : " + expected );
        } catch (Exception e){
            Assertions.assertTrue(
                    expected == null,
                    ("Unexpected tokenization failure for input \"" + input + "\": " + e.getMessage()));
        }
    }

    @Test
    public void testTokenizeSingleDigitInteger(){
        assertTokenizes("0", new ArrayList<Token>(Arrays.asList(
                new Token("0"))));
    }

    @Test
    public void testTokenizeIntegerDeclaration(){
        assertTokenizes("int x = 0", new ArrayList<Token>(Arrays.asList(
                new Token("int"),
                new Token("x"),
                new Token("="),
                new Token("0")
        )));
    }

    @Test
    public void testTokenizeMultiDigitInteger(){
        assertTokenizes("0123", new ArrayList<Token>(Arrays.asList(
                new Token("0123"))));
    }

    @Test
    public void testTokenizeUnderscoreVar1(){
        assertTokenizes("_var", new ArrayList<Token>(Arrays.asList(
                new Token("_var"))));
    }

    @Test
    public void testTokenizeUnderscoreVar2(){
        assertTokenizes("v_ar", new ArrayList<Token>(Arrays.asList(
                new Token("v_ar"))));
    }

    @Test
    public void testTokenizeUnderscoreVar3(){
        assertTokenizes("var_", new ArrayList<Token>(Arrays.asList(
                new Token("var_"))));
    }

    @Test
    public void testTokenizeMultiInteger(){
        assertTokenizes("0 3", new ArrayList<Token>(Arrays.asList(
                new Token("0"),
                new Token("3")
        )));
    }

    @Test
    public void testTokenizeMethod(){
        assertTokenizes("method1()", new ArrayList<Token>(Arrays.asList(
                new Token("method1"),
                new Token("("),
                new Token(")")
        )));
    }

    @Test
    public void testTokenizeOperationsWithVariable(){
        assertTokenizes("9*3 + foo77 + (12)", new ArrayList<Token>(Arrays.asList(
                new Token("9"),
                new Token("*"),
                new Token("3"),
                new Token("+"),
                new Token("foo77"),
                new Token("+"),
                new Token("("),
                new Token("12"),
                new Token(")")
        )));
    }

    @Test
    public void testTokenizeVarIncrement(){
        assertTokenizes("foo++ + 2;", new ArrayList<Token>(Arrays.asList(
                new Token("foo"),
                new Token("+"),
                new Token("+"),
                new Token("+"),
                new Token("2"),
                new Token(";")
        )));
    }

    @Test
    public void testTokenizeMethodWithVar(){
        assertTokenizes("public int foo(int x)", new ArrayList<Token>(Arrays.asList(
                new Token("public"),
                new Token("int"),
                new Token("foo"),
                new Token("("),
                new Token("int"),
                new Token("x"),
                new Token(")")
        )));
    }

    @Test
    public void testTokenizeComplicated(){
        assertTokenizes("public static void main(String args[]){ println(\"Hello World!\"); }", new ArrayList<Token>(Arrays.asList(
                new Token("public"),
                new Token("static"),
                new Token("void"),
                new Token("main"),
                new Token("("),
                new Token("String"),
                new Token("args"),
                new Token("["),
                new Token("]"),
                new Token(")"),
                new Token("{"),
                new Token("println"),
                new Token("("),
                new Token("\""),
                new Token("Hello"),
                new Token("World"),
                new Token("!"),
                new Token("\""),
                new Token(")"),
                new Token(";"),
                new Token("}")
        )));
    }

}
