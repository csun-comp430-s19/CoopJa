package CoopJa;

public class PStatementReturn implements PStatement{
    public PExpression returnExpression;

    public PStatementReturn(PExpression returnExpression){
        this.returnExpression = returnExpression;
    }
}
