package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PClassDeclaration {
    // Access Mod, Identifier, list of statements
    public Token accessModifier; //REQUIRED
    public Token identifier;       // REQUIRED
    public Token extendsIdentifier; // If null, no extends
    public ArrayList<PDeclaration> declarationList;

    public PClassDeclaration(Token accessModifier, Token identifier, Token extendsIdentifier, ArrayList<PDeclaration> declarationList){
        this.accessModifier = accessModifier;
        this.identifier = identifier;
        this.declarationList = declarationList;
        this.extendsIdentifier = extendsIdentifier;
    }

    public String generateClassString() throws CodeGenException{
        // This is gonna suck
        // Let's seperate the declaration types
        // Using instanceof like this is kind of bad but it's going to work for now
        ArrayList<PVariableDeclaration> variableDeclarations = new ArrayList<>();
        //ArrayList<PStatement> variableDeclarations = new ArrayList<>();
        ArrayList<PStatementFunctionDeclaration> functionDeclarations = new ArrayList<>();
        for (int i = 0 ; i < declarationList.size(); i++){
            PDeclaration currentDeclaration = declarationList.get(i);
            if (currentDeclaration instanceof PStatementFunctionDeclaration){
                functionDeclarations.add((PStatementFunctionDeclaration) currentDeclaration);
            }
            else if (currentDeclaration instanceof PVariableDeclaration){
                variableDeclarations.add((PVariableDeclaration) currentDeclaration);
            }
            else{
                throw new CodeGenException(CodeGenException.UNKNOWN_DECLARATION);
            }
        }

        // Create the linked maps
        LinkedHashMap globalMemberList = new LinkedHashMap();
        LinkedHashMap localMemberList = new LinkedHashMap();

        // Create the string builder
        StringBuilder classString = new StringBuilder();


        // First we need to create a struct that contains a list of the variable declarations
        classString.append("struct " + identifier.getTokenString() + "{\n");
        for (int i = 0; i < variableDeclarations.size(); i++){
            classString.append("    " + variableDeclarations.get(i).generateCodeStatement(null, null, null, 0) + ";\n");
            globalMemberList.put(variableDeclarations.get(i).identifier.getTokenString(), null);
        }
        classString.append("};\n");

        // Now we need to create the functions, formatted with the class identifier, underscore, then the function name
        for (int i = 0 ; i < functionDeclarations.size(); i++){
            PStatementFunctionDeclaration currentDeclaration = functionDeclarations.get(i);
            classString.append(currentDeclaration.generateCodeStatement(identifier.getTokenString(), globalMemberList, localMemberList, 0));
        }

        return classString.toString();
    }
}
