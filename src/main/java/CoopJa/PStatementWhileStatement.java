package CoopJa;

import java.util.ArrayList;

public class PStatementWhileStatement implements PStatement {
    PExpression expression;
    ArrayList<PStatement> statementList;

    public PStatementWhileStatement(PExpression expression, ArrayList<PStatement> statementList){
        this.expression = expression;
        this.statementList = statementList;
    }
}
