package CoopJa;

import java.util.ArrayList;
import java.util.Arrays;

//NOTE: this .java file is specifically to test our tokenizer (Token.java), it does not do any tokenization

//HOW TO USE:
//1. Either declare or edit a variable at the top of testExample() (like foo, foo2, etc)
//2. Find the line "ArrayList<Token> tokenList = Token.tokenize(foo5);" and replace "foo5" with the variable to test
//3. (Optional) Find the line "Token.TokenType[] expectedTypes = {};" and add the predicted tokens in the curly braces preceeded by "Token."
//              For a complete list of Tokens, see Token.java", examples of this are listed above this line
//4. Run the file. If the array of predicted tokens matches the output, the line "Arrays match" will be printed last, otherwise "Arrays do not match"
//              Above this, a complete list of tokens is displayed. If an item is not recognized, it will be declared an UNKNOWN token.
/////////////

public class TokenizerExampleTest {
    public static boolean testExample(){
        String foo = "public int foo(int x)";
        String foo2 = "int x;";
        String foo3 = "foo++ + 2;";
        String foo4 = "foo >= 5;";
        String foo5 = "_foo";
        String complicated = "public static void main(String args[]){ println(\"Hello World!\"); }";
        // Example we'll be working with
        String mainExample = "9*3 + foo77 + (12)";

        // Let's tokenize mainExample
        // You'll need a Token ArrayList, we'll call it tokenList
        ArrayList<Token> tokenList = Token.tokenize(foo5);

        // Let's create an array of what we exact the output to be, which is a Number, an asterisk, a number, a plus,
        // A variablename, a plus, a left paren, a number, and a right paren
        // This can be a little tedious

//        Token.TokenType[] expectedTypes = {Token.TokenType.NUMBER, Token.TokenType.SYMBOL_ASTERISK, Token.TokenType.NUMBER,
//                Token.TokenType.SYMBOL_PLUS, Token.TokenType.IDENTIFIER, Token.TokenType.SYMBOL_PLUS,
//                Token.TokenType.SYMBOL_LEFTPAREN, Token.TokenType.NUMBER, Token.TokenType.SYMBOL_RIGHTPAREN}; //mainExample

//        Token.TokenType[] expectedTypes = {Token.TokenType.KEYWORD_PUBLIC, Token.TokenType.KEYWORD_STATIC, Token.TokenType.KEYWORD_VOID,
//        Token.TokenType.IDENTIFIER, Token.TokenType.SYMBOL_LEFTPAREN, Token.TokenType.KEYWORD_STRING, Token.TokenType.IDENTIFIER,
//                Token.TokenType.SYMBOL_LEFTBRACKET, Token.TokenType.SYMBOL_RIGHTBRACKET, Token.TokenType.SYMBOL_RIGHTPAREN, Token.TokenType.SYMBOL_LEFTCURLY,
//                Token.TokenType.IDENTIFIER, Token.TokenType.SYMBOL_LEFTPAREN, Token.TokenType.SYMBOL_QUOTE, Token.TokenType.IDENTIFIER,
//                Token.TokenType.IDENTIFIER, Token.TokenType.SYMBOL_EXCLAMATION, Token.TokenType.SYMBOL_QUOTE, Token.TokenType.SYMBOL_RIGHTPAREN, Token.TokenType.SYMBOL_SEMICOLON,
//                Token.TokenType.SYMBOL_RIGHTCURLY}; //complicated


        Token.TokenType[] expectedTypes = {};

        // Now let's get an array of types from the input
        Token.TokenType[] receivedTypes = Token.extractTokenTypes(tokenList);

        // And compare the list
        // In junit, you would use assertArrayEquals, but we'll use Arrays.equal, and print the result to the terminal

        for (int i = 0; i < receivedTypes.length; i++) {
            System.out.println(receivedTypes[i]);
        }

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
