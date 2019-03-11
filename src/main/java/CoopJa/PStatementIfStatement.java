package CoopJa;

import java.util.ArrayList;

public class PStatementIfStatement implements PStatement {
    public PExpression expression;
    public ArrayList<PStatement> statementList;
    public ArrayList<PStatement> elseStatementList;; // If this is null, there's no else statement

    public PStatementIfStatement(PExpression expression, ArrayList<PStatement> statementList, ArrayList<PStatement> elseStatementList){
        this.expression = expression;
        this.statementList = statementList;
        this.elseStatementList = elseStatementList;
    }
}
