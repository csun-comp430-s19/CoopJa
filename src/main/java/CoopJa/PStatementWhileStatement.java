package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PStatementWhileStatement implements PStatement {
    public PExpression expression;
    public ArrayList<PStatement> statementList;

    public PStatementWhileStatement(PExpression expression, ArrayList<PStatement> statementList){
        this.expression = expression;
        this.statementList = statementList;
    }

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "While Statement");
    }
}
