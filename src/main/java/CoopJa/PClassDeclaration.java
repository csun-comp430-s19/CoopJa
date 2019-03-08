package main.java.CoopJa;

import java.util.ArrayList;

public class PClassDeclaration {
    // Access Mod, Identifier, list of statements
    public Token accessModifier;
    public Token identifier;       // REQUIRED
    public ArrayList<PDeclaration> declarationList;

    public PClassDeclaration(Token accessModifier, Token identifier, ArrayList<PDeclaration> declarationList){
        this.accessModifier = accessModifier;
        this.identifier = identifier;
        this.declarationList = declarationList;
    }
}
