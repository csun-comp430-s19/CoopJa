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
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Function Declaration");
    }
}
