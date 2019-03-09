package CoopJa;

public class PExpressionVariable implements  PExpression, PExpressionAtom{
    public Token variable;

    public PExpressionVariable(Token variable){
        this.variable = variable;
    }
}
