package main.java.CoopJa;

class Identifier{
    public Token token = null;
    public Identifier(Token inputToken){
        if (inputToken.getType() == Token.TokenType.VARIABLENAME){
            token = inputToken;
        }
    }
}