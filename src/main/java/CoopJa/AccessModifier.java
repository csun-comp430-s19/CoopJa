package main.java.CoopJa;

import main.java.CoopJa.Token;

public class AccessModifier{
    public Token token = null;
    public AccessModifier(Token inputToken){
        if (inputToken.getType() == Token.TokenType.KEYWORD_PUBLIC ||
                inputToken.getType() == Token.TokenType.KEYWORD_PRIVATE ||
                inputToken.getType() == Token.TokenType.KEYWORD_PROTECTED){
            token = inputToken;
        }
    }
}