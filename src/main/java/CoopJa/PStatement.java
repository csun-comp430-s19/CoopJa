package CoopJa;

import java.util.ArrayList;
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
    String generateCodeStatement(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException;

    // Static helper function for parsing a list of statements within a statement block.
    // For example, everything within an if-statement, a function declaration, and such
    static String generateCodeStatementBlock(ArrayList<PStatement> statementList, String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        // Need a stringbuilder for this
        StringBuilder blockString = new StringBuilder();

        // Whenever we reach a declaration, we need to add it to the localmembers list
        // Then these need to be cleared off the list once it's finished
        // If the declared variable was already created, throw an exception
        // Keep track of the current size (aka the tail index) of the local members list
        int localMembersTail = localMembers.size();

        for (int i = 0; i < statementList.size(); i++){
            PStatement currentStatement = statementList.get(i);
            // Check if it's a declaration
            /*if (currentStatement instanceof PDeclaration){
                PDeclaration currentStatementDeclaration = (PDeclaration) currentStatement;
                // Check if it's already been declared
                if (globalMembers.containsValue(currentStatementDeclaration.getIdentiferString())
                        || localMembers.containsValue(currentStatementDeclaration.getIdentiferString())){
                    throw new CodeGenException(CodeGenException.REDECLARATION);
                }
                // throw it into the list
                else{
                    localMembers.put(currentStatementDeclaration.getIdentiferString(), null);
                }
            }*/
            // Append the statement
            blockString.append("    " + currentStatement.generateCodeStatement(globalClassName, globalMembers, localMembers) + ";\n");
        }

        // Clear out any newly added members to the list now
        // This is almost certainly sub-optimal, but it's direct and I need to not pull my hair out
        // Turn the member list to an array
        String[] localMemberArray = localMembers.keySet().toArray(new String[0]);
        // Remove all the entries that were added
        for (int i = localMembersTail; i < localMemberArray.length; i++){
            localMembers.remove(localMemberArray[i]);
        }

        // Return the string
        return blockString.toString();
    }

}
