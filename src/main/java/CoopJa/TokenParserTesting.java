package main.java.CoopJa;

import org.typemeta.funcj.functions.Functions.*;
import org.typemeta.funcj.parser.*;

import java.util.ArrayList;

public class TokenParserTesting {
    private static Predicate<Token> typePredicate(Token.TokenType type){
        return Predicate.of((Token t) -> t.getType() == type);
    }


    // Parsers
    // Single Token Parsers
    // All datatypes keywords
    Parser<Token, Token> varType = Combinators.choice(
            Combinators.satisfy("int type", typePredicate(Token.TokenType.KEYWORD_INT)),
            Combinators.satisfy("double type", typePredicate(Token.TokenType.KEYWORD_DOUBLE)),
            Combinators.satisfy("char type", typePredicate(Token.TokenType.KEYWORD_CHAR)),
            Combinators.satisfy("boolean type", typePredicate(Token.TokenType.KEYWORD_BOOLEAN)),
            Combinators.satisfy("string type", typePredicate(Token.TokenType.KEYWORD_STRING)),
            Combinators.satisfy("auto type", typePredicate(Token.TokenType.KEYWORD_AUTO)),
            Combinators.satisfy("object type", typePredicate(Token.TokenType.IDENTIFIER))  // Covers Objcect names
    );

    // Access Types
    Parser<Token, Token> accessType = Combinators.choice(
            Combinators.satisfy("public", typePredicate(Token.TokenType.KEYWORD_PUBLIC)),
            Combinators.satisfy("private", typePredicate(Token.TokenType.KEYWORD_PRIVATE)),
            Combinators.satisfy("protected", typePredicate(Token.TokenType.KEYWORD_PROTECTED))
            //Combinators.satisfy("", typePredicate(Token.TokenType.KEYWORD_INT)),
    );

    // All operators
    Parser<Token, Token> operator = Combinators.choice(
            Combinators.satisfy("plus", typePredicate(Token.TokenType.SYMBOL_PLUS)),
            Combinators.satisfy("minus", typePredicate(Token.TokenType.SYMBOL_MINUS)),
            Combinators.satisfy("multiply", typePredicate(Token.TokenType.SYMBOL_ASTERISK)),
            Combinators.satisfy("divide", typePredicate(Token.TokenType.SYMBOL_SLASH)),
            Combinators.satisfy("less than", typePredicate(Token.TokenType.SYMBOL_LESSTHAN)),
            Combinators.satisfy("greater than", typePredicate(Token.TokenType.SYMBOL_GREATERTHAN)),
            Combinators.satisfy("less than or equal", typePredicate(Token.TokenType.SYMBOL_LESSTHANEQUAL)),
            Combinators.satisfy("greater than or equal", typePredicate(Token.TokenType.SYMBOL_GREATERTHANEQUAL)),
            Combinators.satisfy("is equal", typePredicate(Token.TokenType.SYMBOL_DOUBLEEQUALS)),
            Combinators.satisfy("not equal", typePredicate(Token.TokenType.SYMBOL_NOTEQUAL)),
            Combinators.satisfy("logical or", typePredicate(Token.TokenType.SYMBOL_DOUBLEBAR)),
            Combinators.satisfy("bitwise or", typePredicate(Token.TokenType.SYMBOL_BAR)),
            Combinators.satisfy("logical and", typePredicate(Token.TokenType.SYMBOL_DOUBLEAMPERSAND)),
            Combinators.satisfy("bitwise and", typePredicate(Token.TokenType.SYMBOL_AMPERSAND)),
            Combinators.satisfy("xor", typePredicate(Token.TokenType.SYMBOL_CARET)),
            Combinators.satisfy("shift right", typePredicate(Token.TokenType.SYMBOL_SHIFTRIGHT)),
            Combinators.satisfy("shift left", typePredicate(Token.TokenType.SYMBOL_SHIFTLEFT)),
            Combinators.satisfy("increment", typePredicate(Token.TokenType.SYMBOL_DOUBLEPLUS)),
            Combinators.satisfy("decrement", typePredicate(Token.TokenType.SYMBOL_DOUBLEMINUS))
    );


    public static void main(String[] args){
        String foo = "";
        //String foo = "foo";
        ArrayList<Token> tokenList = Token.tokenize(foo);

        // Set up an input and a parser for a simple assignment
        Input<Token> tokenInput = new TokenParserInput(tokenList);
        Parser<Token, Token> identifierParser = Combinators.satisfy("Identifier", typePredicate(Token.TokenType.IDENTIFIER));
        Parser<Token, Token> numberParser = Combinators.satisfy("Identifier", typePredicate(Token.TokenType.NUMBER));
        Parser<Token, Token> equalParser = Combinators.satisfy("Identifier", typePredicate(Token.TokenType.SYMBOL_EQUALS));
        //Parser<Token, PVariableAssignment> assignment = identifierParser.andL(equalParser).and(numberParser).map(a -> b -> new PVariableAssignment(a,b));
        //Parser<Token, Token> identifierParser = Combinators.satisfy("Identifier", Token::isSameType);

       //Result<Token, PVariableAssignment> variableAssignment = assignment.parse(tokenInput);

        //PVariableAssignment foofoo = variableAssignment.getOrThrow();

        // Confusing as hell
        //Parser<Token, PVariableAssignment> fooParse;
        //fooParse = Combinators.value(foo);
    }
}
