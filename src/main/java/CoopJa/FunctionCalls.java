package main.java.CoopJa;

import com.sun.jdi.Value;

import java.util.ArrayList;

public class FunctionCalls {

    public Identifier identifier;
    public ArrayList<ValueRep> valueRepsList;


    private FunctionCalls(Identifier identifier, ArrayList<ValueRep> valueRepsList){
        this.identifier = identifier;
        this.valueRepsList = valueRepsList;
    }


    public static FunctionCalls parseFunctionCalls(ArrayList<Token> inputTokens, int start, int end){
        // Functioncall example
        // foo(foo1, foo2)
        // What does this consist of?
        // identifier, openening parenthesis, any number of value representations seperated by comma, closing paren

        int tokenPointer = start;
        Token focusToken = inputTokens.get(tokenPointer);

        // Check the first token for a variablename/identifier token
        // Else return null
        Identifier identifier = new Identifier(focusToken);
        if (identifier.token != null){
            tokenPointer++;
            focusToken = inputTokens.get(tokenPointer);
        }
        else{
            return null;
        }

        // Opening parenthesis needed, or else return null
        if (focusToken.getType() == Token.TokenType.SYMBOL_LEFTPAREN){
            tokenPointer++;
            focusToken = inputTokens.get(tokenPointer);
        }
        else{
            return null;
        }

        // COMPLICATED METHOD TO GATHER UNLIMITED VALUEREPS
        // While the focus token isnt' the right parenthesis
        // Look for a set of tokens, terminated by either a comma or right parenthesis token
        // Each set gets processed by ValueRep parse ValueRep
        // If ValueRep fails to find a valid ValueRep in that input, it throws out null, and that's bad
        ArrayList<ValueRep> valueRepsList = new ArrayList<>();  // List of value representations for the input
        while (focusToken.getType() != Token.TokenType.SYMBOL_RIGHTPAREN){
            // Starting from our tokenPointer, look for the index of the next comma or right paren token
            int secondaryTokenPointer = tokenPointer+1;
            while (inputTokens.get(secondaryTokenPointer).getType() != Token.TokenType.SYMBOL_COMMA &&
                    inputTokens.get(secondaryTokenPointer).getType() != Token.TokenType.SYMBOL_RIGHTPAREN){
                secondaryTokenPointer++;
            }
            // Once we find that index, parse that set and add it to the valueRepsList
            ValueRep generatedValueRep = ValueRep.parseValueRep(inputTokens, tokenPointer, secondaryTokenPointer-1);
            if (generatedValueRep != null){
                valueRepsList.add(generatedValueRep);
            }
            else{
                return null;
            }

            // Now if the secondary token pointer is over a comma, shift it over by 1, and change focus token
            tokenPointer = secondaryTokenPointer;
            if (inputTokens.get(secondaryTokenPointer).getType() == Token.TokenType.SYMBOL_COMMA){
                tokenPointer++;
            }
            focusToken = inputTokens.get(tokenPointer);
        }

        // Now return a FunctionCalls object
        return new FunctionCalls(identifier, valueRepsList);
    }

    public static void main(String[] args){
        // Hella testing
        // Create example input
        String foo = "foo(fobby, 60, 2424)";
        // Tokenize that
        ArrayList<Token> tokens = Token.tokenize(foo);
        // Run it through the parser, just checking sanity through the debugger for now.
        parseFunctionCalls(tokens, 0, tokens.size());
    }
}
