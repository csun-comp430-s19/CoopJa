package CoopJa;

import java.util.LinkedHashMap;

public class PStatementReturn implements PStatement{
    public PExpression returnExpression;

    public PStatementReturn(PExpression returnExpression){
        this.returnExpression = returnExpression;
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Return Statement");
        if (returnExpression != null){
            return "return " + returnExpression.generateString(globalClassName, globalMembers, localMembers);
        }
        else{
            return "return";
        }
    }
}
