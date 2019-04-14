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
    public String generateString(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "expression identifier reference");
        return identifier.getTokenString() + "->" + next.generateString(globalMembers, localMembers);
    }

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Expression Identifier Reference");
        return generateString(globalMembers, localMembers);
    }
}
