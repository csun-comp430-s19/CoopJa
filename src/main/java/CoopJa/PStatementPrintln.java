package CoopJa;

import java.util.LinkedHashMap;

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

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Println");
    }
}
