package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PStatementPrintf implements PStatement {
    public PExpressionAtomStringLiteral printString;
    public ArrayList<PExpression> additionalExpressions;

    public PStatementPrintf(PExpressionAtomStringLiteral printString, ArrayList<PExpression> additionalExpressions){
        this.printString = printString;
        this.additionalExpressions = additionalExpressions;
    }


    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, int blockLevel) throws CodeGenException {
        StringBuilder statementString = new StringBuilder();
        statementString.append("printf(" + printString.literalToken.getTokenString());
        if (additionalExpressions.size() > 0){
            statementString.append(",");
            for (int i = 0; i < additionalExpressions.size(); i++){
                statementString.append(additionalExpressions.get(i).generateString(globalClassName, globalMembers, localMembers));
                if (i < additionalExpressions.size()-1){
                    statementString.append(",");
                }
            }
        }
        statementString.append(")");
        return statementString.toString();
    }
}
