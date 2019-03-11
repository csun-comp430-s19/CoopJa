package main.java.CoopJa;

import java.util.ArrayList;

class VarType{
    public Token token = null;
    public boolean isArray = false;
    public VarType(Token inputToken){
        if(inputToken.getType() == Token.TokenType.KEYWORD_INT ||
                inputToken.getType() == Token.TokenType.KEYWORD_DOUBLE ||
                inputToken.getType() == Token.TokenType.KEYWORD_CHAR ||
                inputToken.getType() == Token.TokenType.KEYWORD_BOOLEAN ||
                inputToken.getType() == Token.TokenType.KEYWORD_STRING ||
                inputToken.getType() == Token.TokenType.KEYWORD_AUTO ||
                inputToken.getType() == Token.TokenType.KEYWORD_VOID ||
                inputToken.getType() == Token.TokenType.VARIABLENAME){
            token = inputToken;
        }
    }

    public void changeArray(boolean input){
        isArray = input;
    }

    // This may be relevant later
    public boolean checkIfArray(ArrayList<Token> token, int start){
        return (token.get(start).getType() == Token.TokenType.SYMBOL_LEFTBRACKET && token.get(start+1).getType() == Token.TokenType.SYMBOL_RIGHTBRACKET);
    }
}