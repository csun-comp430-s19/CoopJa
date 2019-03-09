package CoopJa;

public class PExpressionAtomObjectConstruction implements PExpressionAtom {
    public Token identifier;
    public PExpressionAtomObjectConstruction(Token identifier){
        this.identifier = identifier;
    }
}
