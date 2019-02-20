package main.java.CoopJa;

import java.util.ArrayList;

public class N_ParserExample {

    public static String exampletest = "if ( stuff ) { more stuff }"; //example statement to test parser
    public static ArrayList<Token> alltokens = Token.tokenize(exampletest); //tokenize var
    public static Token.TokenType[] receivedtypeslist = Token.extractTokenTypes(alltokens); //get list of tokens names

    public static void main(String[] args){

        printTokens(receivedtypeslist);
        Validator(receivedtypeslist);

    }

    public static void printTokens(Token.TokenType[] toPrint) {
        System.out.println("// List of Tokens //");
        for (int i = 0; i < toPrint.length; i++) {
            System.out.println(toPrint[i]);
        }
        System.out.println("// End of List //");
    }

    public static void Validator(Token.TokenType[] tokenslist) {

        int currentpos = 0; //probably not needed
        //start with first token in list (could use to chop off already dealt with material)
        switch (tokenslist[currentpos]) {
            case KEYWORD_IF:
                if_dealwith(tokenslist);
                break;
            default:
                System.out.println("idk yet");
                break;
        }

    }

    public static Token.TokenType[] if_dealwith(Token.TokenType[] tokenstuff) {

        //while will be similar to beginning of if
        int currentpos = 0;
        //if the list contains "if" followed immediately by "(" -- so "if("
        //tokenstuff[currentpos].name() gives the string name of the token (if: "KEYWORD_IF")
        if (tokenstuff[currentpos].name().equals("KEYWORD_IF") && tokenstuff[++currentpos].name().equals("SYMBOL_LEFTPAREN")) {
            System.out.println("working");
            //read until next ')', send all those tokens as a list to validator()
            //how to store these parsed parts properly?

        }
        return null;

    }

}
