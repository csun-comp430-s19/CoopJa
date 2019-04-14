package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PStatementIfStatement implements PStatement {
    public PExpression expression;
    public ArrayList<PStatement> statementList;
    public ArrayList<PStatement> elseStatementList; // If this is null, there's no else statement

    public PStatementIfStatement(PExpression expression, ArrayList<PStatement> statementList, ArrayList<PStatement> elseStatementList){
        this.expression = expression;
        this.statementList = statementList;
        this.elseStatementList = elseStatementList;
    }

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        StringBuilder ifStatmentString = new StringBuilder("if (");
        ifStatmentString.append(expression.generateString(globalMembers, localMembers) + "){");
        // Add in the list of statements
        for (int i = 0; i < statementList.size(); i++){
            ifStatmentString.append(statementList.get(i) + ";\n");
        }
        ifStatmentString.append("}");
        // Else statement exists? Then do that too
        if (elseStatementList != null){
            ifStatmentString.append("else{");
            for (int i = 0;  i < elseStatementList.size(); i++){
                ifStatmentString.append(elseStatementList.get(i) + ";\n");
            }
        }
        ifStatmentString.append("}");
        // Then return
        return ifStatmentString.toString();
    }
}
