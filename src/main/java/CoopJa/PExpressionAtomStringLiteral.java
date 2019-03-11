package CoopJa;

public class PExpressionAtomStringLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomStringLiteral(Token literalToken){
        this.literalToken = literalToken;
    }
}
