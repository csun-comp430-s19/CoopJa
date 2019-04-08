package CoopJa;

public class PExpressionAtomBooleanLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomBooleanLiteral(Token literalToken){
        this.literalToken = literalToken;
    }

    @Override
    public String generateString() {
        return (literalToken.getType() == Token.TokenType.KEYWORD_TRUE) ? "1" : "0";
    }
}
