package CoopJa;

// identifier.Statement

import java.util.LinkedHashMap;

public class PIdentifierReference implements PStatement, PExpressionAtom {
    //PIR is in the form
    //foo.main(); or foo.var1;
    //where foo is the Token identifier, and main() will be a PStatementFunctionCall, and var1 will be a PExpressionVariable
    public Token identifier;
    public PStatement nextStatement; //for function calls --> foo."main()"
    public PExpression nextExpression; //for var ref --> foo."var"

    public PIdentifierReference(Token identifier, PStatement next){
        this.identifier = identifier;
        this.nextStatement = next;
    }

    public PIdentifierReference(Token identifier, PExpressionParserElement next){
        this.identifier = identifier;
        if (next instanceof PStatement){
            nextStatement = (PStatement) next;
        }
        else{
            nextExpression = (PExpression) next;
        }
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Identifier Reference");
        // Need to know what type this identifier belongs to first
        if (nextStatement != null) {
            /*String functionType = globalMembers.get(identifier.getTokenString());
            if (functionType == null) {
                functionType = localMembers.get(identifier.getTokenString());
            }*/
            if (nextStatement instanceof PStatementFunctionCall) {
                // old?
                //return functionType + "_" + ((PStatementFunctionCall) nextStatement).generateString(null, null, null, identifier.getTokenString());
                // new?
                return identifier.getTokenString() + "->" + ((PStatementFunctionCall) nextStatement).generateString(null, null, null, identifier.getTokenString());
            } else {
                return identifier.getTokenString() + "->" + nextStatement.generateCodeStatement(null, null, null, 0);
            }

        }
        else{
            return identifier.getTokenString() + "->" + nextExpression.generateString(null, null, null);
        }
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, int blockLevel) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Identifier Reference");
        return generateString(globalClassName, globalMembers, localMembers);
    }
}
