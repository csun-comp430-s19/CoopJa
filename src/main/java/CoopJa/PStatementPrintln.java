package CoopJa;

public class PStatementPrintln implements PStatement{
    public Token printStringToken;

    public PStatementPrintln(Token printStringToken){
        this.printStringToken = printStringToken;
    }
}
