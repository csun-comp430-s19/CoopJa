package CoopJa;

import java.util.LinkedHashMap;

public class PVariableAssignment implements PStatement {
    public Token identifier;
    public PExpression value;

    public PVariableAssignment(Token identifier, PExpression value){
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Return Variable Assignment");
    }
}
