package main.java.CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExpressionUnitTests {
    public void assertParses(final String input, Expression expected){
        try{
            final Expression recieved = ExpressionParser.Exp_dealwith(Token.tokenize(input));
            Assertions.assertEquals(recieved, expected,
                    "Expression Parser failed, got: " + recieved + " \n expected : " + expected );
        } catch (Exception e){
            Assertions.assertTrue(
                    expected == null,
                    ("Unexpected Parser failure for input \"" + input + "\": " + e.getMessage()));
        }
    }

    @Test
    public void testParserSingleDigitInteger(){
        assertParses("0", new NumExpression(new Token("0")));
    }

    @Test
    public void testParserSingleVariable(){
        assertParses("x", new VariableExpression(new Token("x")));
    }

    @Test
    public void testParser3TokenExpression(){
        assertParses("1+2", new ArithmeticExpression(
                new NumExpression(new Token("1")),
                new Token("+"),
                new NumExpression(new Token("2"))
                                                            ));
    }

    @Test
    public void testParserArithmeticPrecedence(){
        assertParses("1-2/3", new ArithmeticExpression(
                new NumExpression(new Token("1")),
                new Token("-"),
                new ArithmeticExpression(
                        new NumExpression(new Token("2")),
                        new Token("/"),
                        new NumExpression(new Token("3"))
                )
        ));
    }

    @Test
    public void testParserArithmeticPrecedenceWithParens(){
        assertParses("(1-2)/3", new ArithmeticExpression(
                new ArithmeticExpression(
                        new NumExpression(new Token("1")),
                        new Token("-"),
                        new NumExpression(new Token("2"))
                ),
                new Token("/"),
                new NumExpression(new Token("3"))
        ));
    }
}
