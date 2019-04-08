package CoopJa;

public class PExpressionStub implements  PExpression { //unused, just temp stuff
    Token stubToken;
    public PExpressionStub(Token stubToken){
        this.stubToken = stubToken;
    }

    @Override
    public String generateString() throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Stub (Delte this)");
    }
}
