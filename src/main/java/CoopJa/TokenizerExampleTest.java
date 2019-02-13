package main.java.CoopJa;

import java.util.ArrayList;
import java.util.Arrays;

public class TokenizerExampleTest {
    public static boolean testExample(){
        String foo = "public int foo(int x)";
        String foo2 = "int x;";
        String foo3 = "foo++ + 2;";
        String foo4 = "foo >= 5;";
        String complicated = "public static void main(String args[]){ println(\"Hello World!\"); }";
        // Example we'll be working with
        String mainExample = "9*3 + foo77 + (12)";

        // Let's tokenize mainExample
        // You'll need a Token ArrayList, we'll call it tokenList
        ArrayList<Token> tokenList = Token.tokenize(mainExample);

        // Let's create an array of what we exact the output to be, which is a Number, an asterisk, a number, a plus,
        // A variablename, a plus, a left paren, a number, and a right paren
        // This can be a little tedious
        Token.TokenType[] expectedTypes = {Token.TokenType.NUMBER, Token.TokenType.SYMBOL_ASTERISK, Token.TokenType.NUMBER,
                Token.TokenType.SYMBOL_PLUS, Token.TokenType.VARIABLENAME, Token.TokenType.SYMBOL_PLUS,
                Token.TokenType.SYMBOL_LEFTPAREN, Token.TokenType.NUMBER, Token.TokenType.SYMBOL_RIGHTPAREN};

        // Now let's get an array of types from the input
        Token.TokenType[] receivedTypes = Token.extractTokenTypes(tokenList);

        // And compare the list
        // In junit, you would use assertArrayEquals, but we'll use Arrays.equal, and print the result to the terminal
        return Arrays.equals(expectedTypes, receivedTypes);
    }

    public static void main(String[] args){
        if (testExample()){
            System.out.printf("Arrays match\n");
        }
        else{
            System.out.printf("Arrays do not match\n");
        }
    }
}
