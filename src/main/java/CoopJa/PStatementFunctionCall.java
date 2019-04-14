package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PStatementFunctionCall implements PStatement, PExpression, PExpressionAtom{
    // Statements are in the form
    // foo.fooCall(expressions, moreExpressions)
    // Identifier, identifier2, then a list of expressions
    public Token identifier;
    public ArrayList<PExpression> expressionsInput;

    public PStatementFunctionCall(Token identifier, ArrayList<PExpression> expressionsInput){
        this.identifier = identifier;
        this.expressionsInput = expressionsInput;

    }

    // TODO: Generate function declarations AND calls
    @Override
    public String generateString(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Function Call");

        // First build the expression list string
        StringBuilder expressionListString = new StringBuilder();
        for (int i = 0; i < expressionsInput.size(); i++){
            expressionListString.append(expressionsInput.get(i));
            if (i < expressionsInput.size()-1){
                expressionListString.append(",");
            }
        }
        // Now we can make the string
        return identifier.getTokenString() + "(" + expressionListString + ")";
    }

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        return generateString(globalMembers, localMembers);
    }
}
