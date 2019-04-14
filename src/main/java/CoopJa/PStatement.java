package CoopJa;

import java.util.LinkedHashMap;

public interface PStatement {
    //classes that implement this class

    //PExpressionIdentifierReference
    //PIdentifierReference
    //PStatementBreak
    //PStatementForStatement
    //PStatementFunctionCall
    //PStatementFunctionDeclaration
    //PStatementIfStatement
    //PStatementPrintln
    //PStatementReturn
    //PStatementWhileStatement
    //PVariableAssignment
    //PVariableDeclaration
    
    //(!) Might implement later
    //String generateString() throws CodeGenException;
    String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException;
}
