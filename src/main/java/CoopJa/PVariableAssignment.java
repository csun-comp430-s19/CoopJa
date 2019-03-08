package main.java.CoopJa;

public class PVariableAssignment implements PStatement {
    private Token identifier;
    private PExpression value;

    public PVariableAssignment(Token identifier, PExpression value){
        this.identifier = identifier;
        this.value = value;
    }
}
