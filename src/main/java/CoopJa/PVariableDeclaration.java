package CoopJa;


import java.util.LinkedHashMap;

//(!)(!) Not tested. 4/8/19
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
    
    //@Override
    public String generateString() throws CodeGenException
    {
      //Check if they're null
      if((variableType == null) || (identifier == null))
      {
        //(!)(?) Appropriate exception & error string?
        throw new CodeGenException("Not enough info to generate code.");
      }
      
      //The string to form
      StringBuilder returnVarDecString = new StringBuilder();

      //For basic types, the same type can be used.
      if((this.variableType.getType() == Token.TokenType.KEYWORD_INT) ||
         (this.variableType.getType() == Token.TokenType.KEYWORD_DOUBLE) ||
         (this.variableType.getType() == Token.TokenType.KEYWORD_CHAR) /*||
         (variableType.getType() == Token.TokenType.KEYWORD_BOOLEAN)*/)
         /*bool types in C, need the header file: #include <stdbool.h>,
            so either generateString, returns a String[2], where 1 is the content
            for main(), and 2 is a header file to add. We also need to declare
            functions before main().
         */
      {
        /*If we do want to use bools for nums, then in string will appear bool,
          else, it will appear as int, or short even for bool.
        */
        returnVarDecString.append(this.variableType.getTokenString()+" "+this.identifier.getTokenString());
      }//end check if basic type.
      
      //check if it's an aggregate type.
      if(variableType.getType() == Token.TokenType.KEYWORD_STRING)
      {
        
        /*(!) In C, if using char [] instead of char *,
          unless we are defining the variable at once,
          then we need to provide a size for the array
          (!) Will need to #include <string.h> for strcat.        
        String strSize = "";
        //char var[]
        //returnVarDecString.append("char "+this.identifier.getTokenString()+"[ ]");
        */
        
        //char * var
        returnVarDecString.append("char *"+this.identifier.getTokenString());        
      }//end check if String type.
      
      //Append the assignment expression.
      //if(this.assignment != null)
          //(!) returnVarDecString.append((" = "+ this.assignment.generateString()));
      
      return returnVarDecString.toString();
    }//end generateString()

    @Override
    public String generateCodeStatement(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_STATEMENT_TYPE + "Variable Declaration");
    }
}//end class PVariableDeclaration
