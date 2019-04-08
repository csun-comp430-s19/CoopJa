package CoopJa;

public class PStatementPrintln implements PStatement{
    public Token printStringToken;

    public PStatementPrintln(Token printStringToken){
        this.printStringToken = printStringToken;
    }
    
    //@Override
    public String generateString() throws CodeGenException
    {      
      /*
      if(this.printStringToken == null)
      {
        
      }
      */
      //System.out.println("Type: "+this.printStringToken.getClass());
      
      //(!) Should assume that println will ultimately receive a single string type.
      return "printf(\"%s\\n\","+this.printStringToken.getTokenString()+");";
    }//end generateString
}
