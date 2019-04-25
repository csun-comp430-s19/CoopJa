package CoopJa;

// identifier.Expression

import java.util.LinkedHashMap;

public class PExpressionIdentifierReference implements PStatement, PExpression, PExpressionAtom{
    public Token identifier;
    public PExpression next;

    public PExpressionIdentifierReference(Token identifier, PExpression next){
        this.identifier = identifier;
        this.next = next;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "expression identifier reference");
        return identifier.getTokenString() + "->" + next.generateString(null, null, null);
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers, int blockLevel) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Expression Identifier Reference");
        return generateString(globalClassName, globalMembers, localMembers);
    }
}
