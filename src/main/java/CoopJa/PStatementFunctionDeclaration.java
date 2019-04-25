package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PStatementFunctionDeclaration implements PStatement, PDeclaration {
    // Function declarations work similar to variable declarations
    // public int foo(int x, char y, foo z){ statements}
    // We need to store access type, return type, identifier, a list of variable declarations, and a list of statements
    public Token accessModifier;   // OPTIONAL
    public Token returnType;       // REQUIRED
    public Token identifier;       // REQUIRED
    public ArrayList<PVariableDeclaration> variableDeclarations; //parameters list (declared vars)
    public ArrayList<PStatement> statementList; //all stmts in body

    public PStatementFunctionDeclaration(Token accessModifier, Token returnType, Token identifier, ArrayList<PVariableDeclaration> variableDeclarations, ArrayList<PStatement> statementList){
        this.accessModifier = accessModifier;
        this.returnType = returnType;
        this.identifier = identifier;
        this.variableDeclarations = variableDeclarations;
        this.statementList = statementList;
    }

    @Override
    public String getIdentiferString() {
        return identifier.getTokenString();
    }

    //@Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers, int blockLevel) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Function Declaration");
        // Throw all the variable delclarations and declared variables in the statement list into the localMembers
        if (localMembers == null){
            localMembers = new LinkedHashMap<>();
        }
        int localOriginalTail = localMembers.size();
        // Parameters are trivial, the rest will have to be done as we go along
        for (int i = 0; i < variableDeclarations.size(); i++){
            localMembers.put(variableDeclarations.get(i).identifier.getTokenString(), null);
        }
        // Set up the main part of the code
        StringBuilder funcDecString = new StringBuilder();
        funcDecString.append(returnType.getTokenString() + " " + globalClassName + "_" + identifier.getTokenString() + " (struct " + globalClassName + "* this");
        if (variableDeclarations.size() > 0){
            funcDecString.append(",");
        }
        for (int i = 0; i < variableDeclarations.size(); i++){
            funcDecString.append(variableDeclarations.get(i).variableType.getTokenString() + " " + variableDeclarations.get(i).identifier.getTokenString());
            if (i < variableDeclarations.size()-1){
                funcDecString.append(",");
            }
        }
        funcDecString.append(")");

        funcDecString.append(PStatement.generateCodeStatementBlock(statementList, globalClassName, globalMembers, localMembers, blockLevel)+ "\n");

        // Clear out any newly added members to the list now
        // This is almost certainly sub-optimal, but it's direct and I need to not pull my hair out
        // Turn the member list to an array
        String[] localMemberArray = localMembers.keySet().toArray(new String[0]);
        // Remove all the entries that were added
        for (int i = localOriginalTail; i < localMemberArray.length; i++){
            localMembers.remove(localMemberArray[i]);
        }

        // Main Function handler
        // TODO: This is a huge kludge to define an execution vector, it doesn't properly account for multiple main declarations or anything like that
        if (identifier.getTokenString().equals("main")) {
            funcDecString.append("int main(int argc, char** argv){\n" +
                    "    struct " + globalClassName + " mainClass = {};\n" +
                    "    return " + globalClassName + "_main(&mainClass);\n" +
                    "}");
        }

        return funcDecString.toString();
    }


}
