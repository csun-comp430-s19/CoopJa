package main.java.CoopJa;

import java.lang.reflect.Array;
import java.util.ArrayList;

class Assignable{
    // todo: More on this section
    ArrayList<Token> assignedTokens;
    public Assignable(ArrayList<Token> assignedTokens){
        this.assignedTokens = assignedTokens;
    }
};

class ParseDeclaration{
    public AccessModifier accessModifier;
    public VarType varType;
    public ParseAssignment parseAssignment;

    public ParseDeclaration(AccessModifier accessModifier, VarType varType, ParseAssignment parseAssignment){
        this.accessModifier = accessModifier;
        this.varType = varType;
        this.parseAssignment = parseAssignment;
    }
}

class ParseAssignment{
    public Identifier identifier;
    public Assignable assignment;

    public ParseAssignment(Identifier identifier, Assignable assignment){
        this.identifier = identifier;
        this.assignment = assignment;
    }
}

class operator{}

// Stub
class operations{}

//class ending{}


public class DeclarationParser {

    public AccessModifier accessModifier;
    public VarType varType;
    public Identifier identifier;
    public ParseAssignment assignment;

    // Constructor
    /*public DeclarationParser(AccessModifier accessModifier, VarType varType, Identifier identifier, ParseAssignment assignment){
        this.accessModifier = accessModifier;
        this.varType = varType;
        this.identifier = identifier;
        this.assignment = assignment;
    }*/

    public static void testing(){

    }

    public static Assignable parseAssignmenable(ArrayList<Token> tokenList, int start, int end){
        //todo: S T U B
        ArrayList<Token> assignableTokenList = new ArrayList<>();
        for (int i = start; i <= end; i++){
            assignableTokenList.add(tokenList.get(i));
        }
        return new Assignable(assignableTokenList);
    }

    // Assignment Parser
    public static ParseAssignment doParseAssignment(ArrayList<Token> inputTokens, int start, int end){
        int tokenPointer = start;
        Token focusToken = inputTokens.get(tokenPointer);

        // Check for a variable name
        Identifier varName = new Identifier(focusToken);
        if (varName != null){
            tokenPointer++;
            focusToken = inputTokens.get(tokenPointer);
        }
        else{
            return null;
        }
        // Check if =
        if (focusToken.getType() == Token.TokenType.SYMBOL_EQUALS){
            tokenPointer++;
            focusToken = inputTokens.get(tokenPointer);
        }
        else{
            return null;
        }
        // Try to get an assignable
        Assignable assignment = parseAssignmenable(inputTokens, tokenPointer, end);
        // todo: This isn't fully finished
        return new ParseAssignment(varName, assignment);
    }

    // Declration Parser
    public static ParseDeclaration doParseDeclaration(ArrayList<Token> inputTokens, int start, int end){
        // Declaration Consists of access modifier (0 or 1), type, name, equals, then an operation
        // Operations can include single numbers, but regardless
        // This isn't a type checker, nor does this check if access modifiers are appropriate for the context
        Token focusToken; // Current token of focus in the parser
        int tokenPointer = start;   // Pointer to the current token
        // Parse if there's an access modifier
        focusToken = inputTokens.get(tokenPointer);

        // Create access token
        AccessModifier accessModifier = new AccessModifier(focusToken);

        // Declare type token but don't assign it
        VarType varType;

        // Declare identifier token but don't assign yet
        Identifier identifier;

        // If first token wasn't acces modifier token, move the pointer before checking for type token
        if (accessModifier.token != null){
            tokenPointer++;
            focusToken = inputTokens.get(tokenPointer);
        }

        // Check for a type token
        varType = new VarType(focusToken);
        // Check if that was null, If it was, error.
        if (varType == null){
            return null;    // Returning null for now;
        }

        tokenPointer++; // If that didn't return null, increment pouinter
        focusToken = inputTokens.get(tokenPointer);

        // Now check if we're at the end. If it is the end, but there is probably an identifier
        // Give a smaller parseAssignment with the identifier, but no assignable
        if (tokenPointer >= end){
            ParseAssignment inputParseAssignment = new ParseAssignment(new Identifier(focusToken), null);
            return new ParseDeclaration(accessModifier, varType, inputParseAssignment);
        }
        else{
            // Let's Try to get a "ParseAssign", if that returns null, there's an error
            // So return null completely
            ParseAssignment assignment = doParseAssignment(inputTokens, tokenPointer, end);
            if (assignment != null){
                return new ParseDeclaration(accessModifier, varType, assignment);
            }
            else{
                return null;
            }
        }

    }

    public static void main(String[] args){
        // Debug crap
        String foo = "public int foo = 9";
        String foo2 = "int foo";
        ArrayList<Token> tokens = Token.tokenize(foo);
        doParseDeclaration(tokens, 0, tokens.size()-1);
    }

}
