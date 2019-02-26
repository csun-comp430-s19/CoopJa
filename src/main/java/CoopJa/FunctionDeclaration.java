package main.java.CoopJa;

import java.util.ArrayList;

public class FunctionDeclaration {

    ParseDeclaration functionDeclare;   // Beggingin section
    ArrayList<ParseDeclaration> functionParameters; // List of declarations parameters


    public FunctionDeclaration(ParseDeclaration functionDeclare, ArrayList<ParseDeclaration> functionParameters){
        this.functionDeclare = functionDeclare;
        this.functionParameters = functionParameters;
    }

    public static FunctionDeclaration doParse(ArrayList<Token> inputTokens, int start, int end){
        // Examples
        // public void main(String[] args){}
        // public foo(){}
        // foo(){}
        // int foo(){}

        // First lest make a declaration list
        ArrayList<ParseDeclaration> functionParameters = new ArrayList<>();

        // Look for basic declaration within the first 2 tokens
        ParseDeclaration parsedDeclaration = DeclarationParser.doParseDeclaration(inputTokens, start, start+2);


        if (parsedDeclaration != null){
            int tokenPointer = start;

            // Start with looking for PARAMETERS
            // Look for position of left parenthesis
            while (inputTokens.get(tokenPointer).getType() != Token.TokenType.SYMBOL_LEFTPAREN){
                tokenPointer++;
            }
            // Parse next 2 variables as value declarations
            // This should not return null
            // This should be procdeded by either a "," or ")"
            // If either of these are bad, return null

            // While loop that looks for the end token (')'),
            tokenPointer++;

            while (inputTokens.get(tokenPointer).getType() != Token.TokenType.SYMBOL_RIGHTPAREN){
                // Try to create another ParseDeclaration object
                ParseDeclaration newDeclarataion = DeclarationParser.doParseDeclaration(inputTokens, tokenPointer, tokenPointer+1);
                if (newDeclarataion != null){
                    functionParameters.add(newDeclarataion);
                    tokenPointer += 2;
                }
                else{
                    return null;
                }
                // Check if next token is valid
                if (inputTokens.get(tokenPointer).getType() != Token.TokenType.SYMBOL_RIGHTPAREN &&
                        inputTokens.get(tokenPointer).getType() != Token.TokenType.SYMBOL_COMMA){
                    return null;
                }
                else if (inputTokens.get(tokenPointer).getType() == Token.TokenType.SYMBOL_COMMA){
                    tokenPointer++;
                }
            }

            // TODO: Statement parser
        }




        return new FunctionDeclaration(parsedDeclaration, functionParameters);

    }
    // Tester
    public static void main(String[] args){
        String foo = "public void foo(int foo1, float foo2)";

        ArrayList<Token> tokens = Token.tokenize(foo);

        doParse(tokens, 0, tokens.size() -1);
    }
}
