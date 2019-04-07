package CoopJa;

public class PVariableAssignment implements PStatement {
    public Token identifier;
    public PExpression value;

    public PVariableAssignment(Token identifier, PExpression value){
        this.identifier = identifier;
        this.value = value;
    }
}
