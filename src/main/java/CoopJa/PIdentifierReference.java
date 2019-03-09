package main.java.CoopJa;

public class PIdentifierReference implements PStatement{
    public Token identifier;
    public PStatement next;

    public PIdentifierReference(Token identifier, PStatement next){
        this.identifier = identifier;
        this.next = next;
    }
}
