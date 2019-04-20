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
    public String generateString(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Identifier Reference");
        return identifier.getTokenString() + "->" + next.generateCodeStatement(null, null, null);
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Identifier Reference");
        return generateString(globalClassName, null, null);
    }
}
