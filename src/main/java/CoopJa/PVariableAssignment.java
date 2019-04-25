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
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers, int blockLevel) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Return Variable Assignment");
        StringBuilder assignmentString = new StringBuilder();
        if (globalMembers.containsKey(identifier.getTokenString())){
            assignmentString.append("this->");
        }
        assignmentString.append(identifier.getTokenString() + " = " + value.generateString(globalClassName, globalMembers, localMembers));
        return assignmentString.toString();
    }
}
