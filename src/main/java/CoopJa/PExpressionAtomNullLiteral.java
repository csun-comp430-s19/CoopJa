package CoopJa;

public class PExpressionAtomNullLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomNullLiteral(Token literalToken){
        this.literalToken = literalToken;
    }
}
