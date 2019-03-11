package CoopJa;

public class PVariableDeclaration implements PStatement, PDeclaration {
    // The following ways are how a Variable can be declared, and thus what info it may contain
    // accessModifier(optional) variableType identifier assignmentExpression
    public Token accessModifier;   // OPTIONAL
    public Token variableType;     // REQUIRED
    public Token identifier;       // REQUIRED
    public PExpression assignment; // OPTONAL

    public PVariableDeclaration(Token accessModifier, Token variableType, Token identifier, PExpression assignment){
        this.accessModifier = accessModifier;
        this.variableType = variableType;
        this.identifier = identifier;
        this.assignment = assignment;
    }
}
