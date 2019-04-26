package CoopJa;

import java.util.LinkedHashMap;

public class PStatementPrintln implements PStatement{
    //public Token printStringToken;
    public PExpression printExpression;

    public PStatementPrintln(Token printStringToken){
        //this.printStringToken = printStringToken;
        this.printExpression = new PExpressionAtomStringLiteral(printStringToken);
    }
    public PStatementPrintln(PExpression expression){
        this.printExpression = expression;
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
      //return "printf(\"%s\\n\","+this.printStringToken.getTokenString()+");";
        return "stub";
    }//end generateString

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, int blockLevel) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Println");
        //return "printf(" + printExpression.generateString(globalClassName, globalMembers, localMembers) + ");\n    printf(\"\\n\")";
        return "printf(\"%s\\n\", " + printExpression.generateString(globalClassName, globalMembers, localMembers) + ")";
    }
}
