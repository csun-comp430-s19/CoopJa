package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PStatementFunctionCall implements PStatement, PExpression, PExpressionAtom {
    // Statements are in the form
    // fooCall(expressions, moreExpressions)
    // this is a function call
    // Identifier, then a list of expressions (params)
    public Token identifier;
    public ArrayList<PExpression> expressionsInput;

    public PStatementFunctionCall(Token identifier, ArrayList<PExpression> expressionsInput) {
        this.identifier = identifier;
        this.expressionsInput = expressionsInput;

    }

    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, String callingVar) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Function Call");

        // First build the expression list string
        StringBuilder expressionListString = new StringBuilder();

        // Add a pointer reference if needed
        if (globalMembers != null && globalMembers.containsKey(identifier.getTokenString())){
            expressionListString.append(identifier.getTokenString() + "->");
        }
        // Rest
        for (int i = 0; i < expressionsInput.size(); i++){
            expressionListString.append(expressionsInput.get(i).generateString(globalClassName, globalMembers, localMembers));
            if (i < expressionsInput.size()-1){
                expressionListString.append(",");
            }
        }
        // Now we can make the string
        // TODO: The first parameter shouldn't always be "this", it's dependent on the parent function. In particular,  the identifier reference here matters, work needs to be done on this, but classes currently aren't fully working
        // TODO: suck less
        if (globalClassName != null) {
            return globalClassName + "_" + identifier.getTokenString() + "(" + callingVar + ", " + expressionListString.toString() + ")";
        }
        else{
            return identifier.getTokenString() + "(" + callingVar + ", " + expressionListString.toString() + ")";
        }
    }
    // TODO: Generate function declarations AND calls
    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers) throws CodeGenException {
        return generateString(globalClassName, globalMembers, localMembers, "this");
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, int blockLevel) throws CodeGenException {
        return generateString(globalClassName, globalMembers, localMembers);
    }
}
