package CoopJa;

import java.util.ArrayList;

public class PStatementIfStatement implements PStatement {
    PExpression expression;
    ArrayList<PStatement> statementList;

    public PStatementIfStatement(PExpression expression, ArrayList<PStatement> statementList){
        this.expression = expression;
        this.statementList = statementList;
    }
}
