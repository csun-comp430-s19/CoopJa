package CoopJa;

public class PExpressionAtomBooleanLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomBooleanLiteral(Token literalToken){
        this.literalToken = literalToken;
    }
}
