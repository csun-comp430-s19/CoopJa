package CoopJa;

public class PExpressionAtomNumberLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomNumberLiteral(Token literalToken){
        this.literalToken = literalToken;
    }
}
