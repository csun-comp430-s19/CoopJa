package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PStatementWhileStatement implements PStatement {
    public PExpression expression;
    public ArrayList<PStatement> statementList;

    public PStatementWhileStatement(PExpression expression, ArrayList<PStatement> statementList){
        this.expression = expression;
        this.statementList = statementList;
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, int blockLevel) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "While Statement");
        StringBuilder whileString = new StringBuilder();
        whileString.append("while(" + expression.generateString(globalClassName, globalMembers, localMembers) + ")");
        whileString.append(PStatement.generateCodeStatementBlock(statementList, globalClassName, localMembers, globalMembers, blockLevel)+ "\n");
        //whileString.append("}\n");
        return whileString.toString();

    }
}
