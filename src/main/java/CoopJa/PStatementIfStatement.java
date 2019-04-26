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
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, int blockLevel) throws CodeGenException {
        StringBuilder ifStatmentString = new StringBuilder("if (");
        ifStatmentString.append(expression.generateString(globalClassName, globalMembers, localMembers) + ")");
        // Add in the list of statements


        ifStatmentString.append(PStatement.generateCodeStatementBlock(statementList, globalClassName, localMembers, globalMembers, blockLevel)+ "\n");
        // Else statement exists? Then do that too
        if (elseStatementList != null){
            // Stupid formatting crap that I shouldn't have to do
            for (int i = 0; i < blockLevel; i++){
                ifStatmentString.append("    ");

            }
            ifStatmentString.append("else");
            ifStatmentString.append(PStatement.generateCodeStatementBlock(elseStatementList, globalClassName, localMembers, globalMembers, blockLevel));

        }
        // Then return
        return ifStatmentString.toString();
    }
}
