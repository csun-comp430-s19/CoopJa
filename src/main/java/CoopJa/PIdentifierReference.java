package CoopJa;

// identifier.Statement

import java.util.LinkedHashMap;

public class PIdentifierReference implements PStatement, PExpressionAtom{
    public Token identifier;
    public PStatement next;

    public PIdentifierReference(Token identifier, PStatement next){
        this.identifier = identifier;
        this.next = next;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Identifier Reference");
        // Need to know what type this identifier belongs to first
        String functionType = globalMembers.get(identifier.getTokenString());
        if (functionType == null){
            functionType = localMembers.get(identifier.getTokenString());
        }
        if (next instanceof PStatementFunctionCall) {
            return functionType + "_" + ((PStatementFunctionCall)next).generateString(null, null, null, identifier.getTokenString());
        }
        else{
            return identifier.getTokenString() + "->" +  next.generateCodeStatement(null, null, null, 0);
        }
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, int blockLevel) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Identifier Reference");
        return generateString(globalClassName, globalMembers, localMembers);
    }
}
