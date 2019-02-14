package main.java.CoopJa;

import java.util.Arrays;
import java.util.ArrayList;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;
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
        assertTokenizes("0", new ArrayList<Token>(Arrays.asList(new Token("0"))));
    }

    @Test
    public void testTokenizeMultiDigitInteger(){
        assertTokenizes("0123", new ArrayList<Token>(Arrays.asList(new Token("0123"))));
    }

    @Test
    public void testTokenizeMultiInteger(){
        assertTokenizes("0 3", new ArrayList<Token>(Arrays.asList(new Token("0"), new Token("3"))));
    }

    @Test
    public void testOldTest(){
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
}
