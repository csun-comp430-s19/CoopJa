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
    public String generateString(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Identifier Reference");
        return identifier.getTokenString() + "->" + next.generateCodeStatement(globalMembers, localMembers);
    }

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Identifier Reference");
        return generateString(globalMembers, localMembers);
    }
}
