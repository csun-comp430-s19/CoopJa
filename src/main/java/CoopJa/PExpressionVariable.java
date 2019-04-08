package CoopJa;

public class PExpressionVariable implements  PExpression, PExpressionAtom{
    public Token variable;

    public PExpressionVariable(Token variable){
        this.variable = variable;
    }


    @Override
    public String generateString() throws CodeGenException {
        return variable.getTokenString();
    }
}
