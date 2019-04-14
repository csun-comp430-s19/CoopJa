package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PStatementForStatement implements PStatement {
    public PStatement statement1;
    public PExpression expression;
    public PStatement statement2;
    public ArrayList<PStatement> statementList;

    public PStatementForStatement(PStatement statement1, PExpression expression, PStatement statement2, ArrayList<PStatement> statementList){
        this.statement1 = statement1;
        this.expression = expression;
        this.statement2 = statement2;
        this.statementList = statementList;
    }

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "For Loop");
    }
}
