package main.java.CoopJa;

import java.util.ArrayList;


class Member{
    public boolean isFunctionCall;
    public FunctionCalls functionCall;
    public Identifier identifier;

    public Member(FunctionCalls functionCall){
        this.functionCall = functionCall;
        isFunctionCall = true;
    }

    public Member(Identifier identifier){
        this.identifier = identifier;
        isFunctionCall = false;
    }
}

public class MemberReferencing {


    public ArrayList<Member> memberList;

    public MemberReferencing(ArrayList<Member> memberList){
        this.memberList = memberList;
    }


    public static MemberReferencing MemberParser(ArrayList<Token> inputTokenList, int starting, int end){
        // Index pointers
        int tokenPointer = starting;
        int secondaryTokenPointer = starting;

        // List of Members
        ArrayList<Member> memberList = new ArrayList<>();

        // Read tokens until the end
        while (secondaryTokenPointer <= end) {
            // Read tokens until period NOT IN QUOTES
            int leftParenCount = 0;
            while (secondaryTokenPointer <= end && (inputTokenList.get(secondaryTokenPointer).getType() != Token.TokenType.SYMBOL_PERIOD || leftParenCount > 0)) {
                // If '(', increment count, if ')', decrement
                if (inputTokenList.get(secondaryTokenPointer).getType() == Token.TokenType.SYMBOL_LEFTPAREN){
                    leftParenCount++;
                }
                else if (inputTokenList.get(secondaryTokenPointer).getType() == Token.TokenType.SYMBOL_RIGHTPAREN){
                    leftParenCount--;
                }
                // Increment pointer
                secondaryTokenPointer++;
            }

            // If the pointers are matching we're probably on the lsat token
            if (tokenPointer == secondaryTokenPointer){
                secondaryTokenPointer++;    // Terrible hack
            }
            // If there's more than 1 token here, try to parse out a function
            if ((secondaryTokenPointer - tokenPointer) > 1) {
                FunctionCalls functionCall = FunctionCalls.parseFunctionCalls(inputTokenList, tokenPointer, secondaryTokenPointer - 1);
                if (functionCall != null){
                    memberList.add(new Member(functionCall));
                }
                else{
                    return null;
                }
            }
            // if it's exactly one, and it's an identifier, add it to the list
            else if (inputTokenList.get(secondaryTokenPointer-1).getType() == Token.TokenType.VARIABLENAME){
                // I swear this is making sense to me on Febuary 24th 2019
                // It might not at a later date
                memberList.add(new Member(new Identifier(inputTokenList.get(secondaryTokenPointer-1))));
            }
            // Something unexpected happened
            else{
                return null;
            }
            secondaryTokenPointer++;
            tokenPointer = secondaryTokenPointer;
        }
        return new MemberReferencing(memberList);
    }

    public static void main(String[] args){
        // Dank testing

        // Test string
        String foo = "foo.bar().foofoo";
        String foo2 = "foo.bar().foofoo.barbar()";
        String foo3 = "foo";
        String foo4 = "bar()";
        // More complicated Crap
        String foo5 = "foo.bar(foo2.bar)";
        String foo6 = "foo.bar(foo2.bar).foo3(foo4())";

        // Tokenize whatever
        ArrayList<Token> tokenizedExample = Token.tokenize(foo);

        // Test in debugger
        MemberParser(tokenizedExample, 0, tokenizedExample.size() -1 );
    }
}
