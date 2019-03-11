package CoopJa;

import java.util.HashMap;
import java.util.Map;

public class PExpressionOperator implements PExpressionParserElement {
    public Token operatorToken;

    // These tables are pretty verbatim from https://introcs.cs.princeton.edu/java/11precedence/
    // This table might actually be useless
    private Map<Token.TokenType, Boolean> ASSOCIATIVE_MAP =
            new HashMap<>() {{
                put(Token.TokenType.SYMBOL_PLUS, true);
                put(Token.TokenType.SYMBOL_MINUS, true);
                put(Token.TokenType.SYMBOL_ASTERISK, true);
                put(Token.TokenType.SYMBOL_SLASH, true);
                put(Token.TokenType.SYMBOL_LESSTHAN, true);
                put(Token.TokenType.SYMBOL_GREATERTHAN, true);
                put(Token.TokenType.SYMBOL_LESSTHANEQUAL, true);
                put(Token.TokenType.SYMBOL_GREATERTHANEQUAL, true);
                put(Token.TokenType.SYMBOL_DOUBLEEQUALS, true);
                put(Token.TokenType.SYMBOL_NOTEQUAL, true);
                put(Token.TokenType.SYMBOL_DOUBLEBAR, true);
                put(Token.TokenType.SYMBOL_BAR, true);
                put(Token.TokenType.SYMBOL_DOUBLEAMPERSAND, true);
                put(Token.TokenType.SYMBOL_AMPERSAND, true);
                put(Token.TokenType.SYMBOL_CARET, true);
                put(Token.TokenType.SYMBOL_SHIFTRIGHT, true);
                put(Token.TokenType.SYMBOL_SHIFTLEFT, true);
            }};

    // At least this isn't useless
    private Map<Token.TokenType, Integer> PRECEDENCE_MAP =
            new HashMap<>() {{
                put(Token.TokenType.SYMBOL_PLUS, 11);
                put(Token.TokenType.SYMBOL_MINUS, 11);
                put(Token.TokenType.SYMBOL_ASTERISK, 12);
                put(Token.TokenType.SYMBOL_SLASH, 12);
                put(Token.TokenType.SYMBOL_LESSTHAN, 9);
                put(Token.TokenType.SYMBOL_GREATERTHAN, 9);
                put(Token.TokenType.SYMBOL_LESSTHANEQUAL, 9);
                put(Token.TokenType.SYMBOL_GREATERTHANEQUAL, 9);
                put(Token.TokenType.SYMBOL_DOUBLEEQUALS, 8);
                put(Token.TokenType.SYMBOL_NOTEQUAL, 8);
                put(Token.TokenType.SYMBOL_DOUBLEBAR, 3);
                put(Token.TokenType.SYMBOL_BAR, 5);
                put(Token.TokenType.SYMBOL_DOUBLEAMPERSAND, 4);
                put(Token.TokenType.SYMBOL_AMPERSAND, 7);
                put(Token.TokenType.SYMBOL_CARET, 6);
                put(Token.TokenType.SYMBOL_SHIFTRIGHT, 10);
                put(Token.TokenType.SYMBOL_SHIFTLEFT, 10);
            }};

    public boolean isLeftAssociative(){
        return ASSOCIATIVE_MAP.get(operatorToken.getType());
    }

    public int precedence(){
        return PRECEDENCE_MAP.get(operatorToken.getType());
    }

    public PExpressionOperator(Token operatorToken){
        this.operatorToken = operatorToken;
    }
}
