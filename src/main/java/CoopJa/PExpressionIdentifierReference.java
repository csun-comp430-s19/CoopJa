package CoopJa;

public class PExpressionIdentifierReference implements PStatement, PExpression, PExpressionAtom{
    public Token identifier;
    public PExpression next;

    public PExpressionIdentifierReference(Token identifier, PExpression next){
        this.identifier = identifier;
        this.next = next;
    }

    @Override
    public String generateString() throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "expression identifier reference");
    }
}
