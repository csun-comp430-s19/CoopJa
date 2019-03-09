package CoopJa;

public class PExpressionAtomLiterals implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomLiterals(Token literalToken){
        this.literalToken = literalToken;
    }
}
