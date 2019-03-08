package main.java.CoopJa;

import java.util.ArrayList;

public class PClassDeclaration {
    // Access Mod, Identifier, list of statements
    public Token accessModifier;
    public Token identifier;       // REQUIRED
    public ArrayList<PStatement> statementList;

    public PClassDeclaration(Token accessModifier, Token identifier, ArrayList<PStatement> statementList){
        this.accessModifier = accessModifier;
        this.identifier = identifier;
        this.statementList = statementList;
    }
}
