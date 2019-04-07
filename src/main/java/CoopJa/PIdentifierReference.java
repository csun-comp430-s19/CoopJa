package CoopJa;

public class PIdentifierReference implements PStatement, PExpressionAtom{
    public Token identifier;
    public PStatement next;

    public PIdentifierReference(Token identifier, PStatement next){
        this.identifier = identifier;
        this.next = next;
    }

    @Override
    public String generateString() throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Identifier Reference");
    }
}
