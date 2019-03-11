package CoopJa;

import java.util.ArrayList;

public class PClassDeclaration {
    // Access Mod, Identifier, list of statements
    public Token accessModifier;
    public Token identifier;       // REQUIRED
    public Token extendsIdentifier; // If null, no extends
    public ArrayList<PDeclaration> declarationList;

    public PClassDeclaration(Token accessModifier, Token identifier, Token extendsIdentifier, ArrayList<PDeclaration> declarationList){
        this.accessModifier = accessModifier;
        this.identifier = identifier;
        this.declarationList = declarationList;
        this.extendsIdentifier = extendsIdentifier;
    }
}
