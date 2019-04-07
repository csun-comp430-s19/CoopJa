package CoopJa;

public class PExpressionAtomObjectConstruction implements PExpressionAtom {
    public Token identifier;
    public PExpressionAtomObjectConstruction(Token identifier){
        this.identifier = identifier;
    }

    @Override
    public String generateString() throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Object Construction");
    }
}
