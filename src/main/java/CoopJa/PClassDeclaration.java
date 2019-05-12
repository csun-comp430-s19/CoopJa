package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PClassDeclaration {
    // Access Mod, Identifier, list of statements
    public Token accessModifier; //REQUIRED
    public Token identifier;       // REQUIRED
    public Token extendsIdentifier; // If null, no extends
    public ArrayList<PDeclaration> declarationList;
    public ArrayList<PStatementFunctionDeclaration> functionDeclarationList;
    public ArrayList<PVariableDeclaration> variableDeclarationList;


    public PClassDeclaration(Token accessModifier, Token identifier, Token extendsIdentifier, ArrayList<PDeclaration> declarationList) {
        this.accessModifier = accessModifier;
        this.identifier = identifier;
        this.declarationList = declarationList;
        this.extendsIdentifier = extendsIdentifier;

        // Create the other two array lists
        functionDeclarationList = new ArrayList<>();
        variableDeclarationList = new ArrayList<>();

        // This splitting of the declarations should've been done by the parser, but it's a bit late for that now
        for (int i = 0 ; i < declarationList.size(); i++){
            PDeclaration currentDeclaration = declarationList.get(i);
            if (currentDeclaration instanceof PStatementFunctionDeclaration){
                functionDeclarationList.add((PStatementFunctionDeclaration) currentDeclaration);
            }
            else if (currentDeclaration instanceof PVariableDeclaration){
                variableDeclarationList.add((PVariableDeclaration) currentDeclaration);
            }
            else{
                // TODO: Use the correct exception type
                try {
                    throw new CodeGenException(CodeGenException.UNKNOWN_DECLARATION);
                } catch (CodeGenException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String generateClassString(/*LinkedHashMap<String, PClassDeclaration> classMap*/) throws CodeGenException{
        // Start handling class extensions
        // If this class is extending, we'll need to obtain it
        /*PClassDeclaration parentClass = null;
        if (extendsIdentifier != null){
            parentClass = classMap.get(extendsIdentifier.getTokenString());

        }*/

        // Create the linked maps
        LinkedHashMap globalMemberList = new LinkedHashMap();
        LinkedHashMap localMemberList = new LinkedHashMap();

        // Create the string builder
        StringBuilder classString = new StringBuilder();



        // First we need to create a struct that contains a list of the declarations
        classString.append("typedef struct " + identifier.getTokenString() + "{\n");

        // How to process parent members???????


        // Now onto the elements unique to our class
        for (int i = 0; i < variableDeclarationList.size(); i++){
            classString.append("    " + variableDeclarationList.get(i).generateCodeStatement(null, null, null, 0) + ";\n");
            globalMemberList.put(variableDeclarationList.get(i).identifier.getTokenString(), variableDeclarationList.get(i).variableType.getTokenString());
        }
        // Add function pointers
        for (int i = 0; i < functionDeclarationList.size(); i++){
            PStatementFunctionDeclaration currentDeclaration = functionDeclarationList.get(i);
            //classString.append("    " + currentDeclaration.returnType.getTokenString() + "(*" + currentDeclaration.identifier.getTokenString() + "_ptr)(");
            classString.append("    " + currentDeclaration.returnType.getTokenString() + "(*" + currentDeclaration.identifier.getTokenString() + ")(");

            // Add the paremeter data types
            // Start with the pointer to "this"
            classString.append("struct " + identifier.getTokenString() +  "*");
            for (int j = 0; j < currentDeclaration.variableDeclarations.size(); j++){
                classString.append(",");
                classString.append(currentDeclaration.variableDeclarations.get(j).variableType.getTokenString());
            }

            classString.append(")" + ";\n");
        }
        classString.append("}" + identifier.getTokenString() + ";\n");

        // Track if one of the function declarations is main
        boolean hasMain = false;
        // Now we need to create the functions, formatted with the class identifier, underscore, then the function name
        for (int i = 0 ; i < functionDeclarationList.size(); i++){
            PStatementFunctionDeclaration currentDeclaration = functionDeclarationList.get(i);
            classString.append(currentDeclaration.generateCodeStatement(identifier.getTokenString(), globalMemberList, localMemberList, 0));
            if (currentDeclaration.identifier.getTokenString().equals("main")){
                hasMain = true;
            }
        }

        // Need to create an an initializer to init the function pointers
        classString.append("void init_" + identifier.getTokenString() + "(" + identifier.getTokenString() + "* input){\n");
        for (int i = 0 ; i < functionDeclarationList.size(); i++){
            //classString.append("    " + "input->" + functionDeclarations.get(i).getIdentiferString() + "_ptr = &"
            classString.append("    " + "input->" + functionDeclarationList.get(i).getIdentiferString() + " = &"
                    + identifier.getTokenString() + "_" + functionDeclarationList.get(i).getIdentiferString() + ";\n");
        }
        classString.append("}\n");

        // If main was inside this class, create the main function
        // TODO: This is still a hack
        if (hasMain) {
            classString.append("int main(int argc, char** argv){\n" +
                    "    " + identifier.getTokenString() + " mainClass = {};\n" +
                    "    init_" + identifier.getTokenString() + "(&mainClass);\n" +
                    "    return " + identifier.getTokenString() + "_main(&mainClass);\n" +
                    "}\n");
        }

        return classString.toString();
    }
}
