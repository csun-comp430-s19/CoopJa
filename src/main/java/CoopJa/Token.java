package main.java.CoopJa;

import java.util.regex.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Token {
    // enum of the different token types
    public enum TokenType {
        KEYWORD_VOID,KEYWORD_INT,KEYWORD_DOUBLE,KEYWORD_CHAR,KEYWORD_BOOLEAN,KEYWORD_STRING,KEYWORD_AUTO,KEYWORD_PUBLIC,
        KEYWORD_PRIVATE,KEYWORD_PROTECTED,KEYWORD_BREAK,KEYWORD_RETURN,KEYWORD_WHILE,KEYWORD_FOR,KEYWORD_IF,KEYWORD_EXTENDS,
        KEYWORD_THIS,KEYWORD_STATIC,KEYWORD_CLASS, KEYWORD_TRUE, KEYWORD_FALSE, KEYWORD_PRINTLN, KEYWORD_NULL,SYMBOL_PLUS,SYMBOL_MINUS,SYMBOL_ASTERISK,SYMBOL_SLASH, SYMBOL_BACKSLASH,SYMBOL_GREATERTHAN, SYMBOL_LESSTHAN,
        SYMBOL_EXCLAMATION,SYMBOL_EQUALS,SYMBOL_BAR,SYMBOL_AMPERSAND,SYMBOL_CARET,SYMBOL_TILDE,SYMBOL_QUOTE,SYMBOL_SEMICOLON,
        SYMBOL_LEFTPAREN,SYMBOL_RIGHTPAREN,SYMBOL_LEFTCURLY,SYMBOL_RIGHTCURLY,SYMBOL_LEFTBRACKET,SYMBOL_RIGHTBRACKET, SYMBOL_COMMA, SYMBOL_PERIOD,
        SYMBOL_GREATERTHANEQUAL, SYMBOL_LESSTHANEQUAL, SYMBOL_DOUBLEEQUALS, SYMBOL_NOTEQUAL, SYMBOL_DOUBLEBAR, SYMBOL_DOUBLEAMPERSAND,
        SYMBOL_SHIFTRIGHT,SYMBOL_SHIFTLEFT,SYMBOL_DOUBLEPLUS, SYMBOL_DOUBLEMINUS,
        IDENTIFIER, OBJECTNAME, NUMBER, STRING, UNKNOWN
    }


    // Create hashmap of the different token types
    // List of Keyword: void int double char boolean String auto public private protected break return while for if
    // extends this static
    // List of symbols: +|-|*|/|>|<|!|=||(literally the bar)|&|^|~|"|;|(|)|{|}|[|]|;
    private Map<String, TokenType> TOKEN_MAP =
            new HashMap<String, TokenType>() {{
                put("void", TokenType.KEYWORD_VOID);
                put("int", TokenType.KEYWORD_INT);
                put("double", TokenType.KEYWORD_DOUBLE);
                put("char", TokenType.KEYWORD_CHAR);
                put("boolean", TokenType.KEYWORD_BOOLEAN);
                put("String", TokenType.KEYWORD_STRING);
                put("auto", TokenType.KEYWORD_AUTO);
                put("public", TokenType.KEYWORD_PUBLIC);
                put("private", TokenType.KEYWORD_PRIVATE);
                put("protected", TokenType.KEYWORD_PROTECTED);
                put("break", TokenType.KEYWORD_BREAK);
                put("return", TokenType.KEYWORD_RETURN);
                put("while", TokenType.KEYWORD_WHILE);
                put("for", TokenType.KEYWORD_FOR);
                put("if", TokenType.KEYWORD_IF);
                put("extends", TokenType.KEYWORD_EXTENDS);
                put("this", TokenType.KEYWORD_THIS);
                put("static", TokenType.KEYWORD_STATIC);
                put("class", TokenType.KEYWORD_CLASS);
                put("true", TokenType.KEYWORD_TRUE);
                put("false", TokenType.KEYWORD_FALSE);
                put("null", TokenType.KEYWORD_NULL);
                put("println", TokenType.KEYWORD_PRINTLN);
                put("+", TokenType.SYMBOL_PLUS);
                put("-", TokenType.SYMBOL_MINUS);
                put("*", TokenType.SYMBOL_ASTERISK);
                put("/", TokenType.SYMBOL_SLASH);
                put("\\", TokenType.SYMBOL_BACKSLASH);
                put(">", TokenType.SYMBOL_GREATERTHAN);
                put("<", TokenType.SYMBOL_LESSTHAN);
                put("!", TokenType.SYMBOL_EXCLAMATION);
                put("=", TokenType.SYMBOL_EQUALS);
                put("|", TokenType.SYMBOL_BAR);
                put("&", TokenType.SYMBOL_AMPERSAND);
                put("^", TokenType.SYMBOL_CARET);
                put("~", TokenType.SYMBOL_TILDE);
                put("\"", TokenType.SYMBOL_QUOTE);
                put(";", TokenType.SYMBOL_SEMICOLON);
                put("(", TokenType.SYMBOL_LEFTPAREN);
                put(")", TokenType.SYMBOL_RIGHTPAREN);
                put("{", TokenType.SYMBOL_LEFTCURLY);
                put("}", TokenType.SYMBOL_RIGHTCURLY);
                put("[", TokenType.SYMBOL_LEFTBRACKET);
                put("]", TokenType.SYMBOL_RIGHTBRACKET);
                put(",", TokenType.SYMBOL_COMMA);
                put(".", TokenType.SYMBOL_PERIOD);
                // Recent additions
                put(">=", TokenType.SYMBOL_GREATERTHANEQUAL);
                put("<=", TokenType.SYMBOL_LESSTHANEQUAL);
                put("==", TokenType.SYMBOL_DOUBLEEQUALS);
                put("!=", TokenType.SYMBOL_NOTEQUAL);
                put("||", TokenType.SYMBOL_DOUBLEBAR);
                put("&&", TokenType.SYMBOL_DOUBLEAMPERSAND);
                put(">>", TokenType.SYMBOL_SHIFTRIGHT);
                put("<<", TokenType.SYMBOL_SHIFTLEFT);
                put("++", TokenType.SYMBOL_DOUBLEPLUS);
                put("--", TokenType.SYMBOL_DOUBLEMINUS);
            }};

    private TokenType type;
    private String tokenString;

    // Constructor
    public Token(String tokenString){
        this.tokenString = tokenString;

        // Determine the type of token
        if (TOKEN_MAP.containsKey(tokenString)){
            type = TOKEN_MAP.get(tokenString);
        }
        else {
            /*if ((Character.isLetter(tokenString.charAt(0)) && Character.isLowerCase(tokenString.charAt(0))) || tokenString.charAt(0) == '_') {
                type = TokenType.IDENTIFIER;
            }
            else if (Character.isLetter(tokenString.charAt(0)) && Character.isUpperCase(tokenString.charAt(0))){
                type = TokenType.OBJECTNAME;
            }*/
            if (Character.isLetter(tokenString.charAt(0)) || tokenString.charAt(0) == '_') {
                type = TokenType.IDENTIFIER;
            }
            // If the first character is a number, categorize this as a number,
            else if (Character.isDigit(tokenString.charAt(0))) {
                type = TokenType.NUMBER;
            }
            // If the first character is a quote, it's probably a string
            else if (tokenString.charAt(0) == '\"') {
                type = TokenType.STRING;
            }
            // if it's something else, mark it as an unknown token, let the parser figure it out
            else {
                type = TokenType.UNKNOWN;
            }
        }
    }

    public String getTokenString(){
        return tokenString;
    }

    public TokenType getType(){
        return type;
    }

    // Static method to tokenize
    public static ArrayList<Token> tokenize(String input){
        // Regular expression pattern for seperating the string to a token array
        //Pattern p = Pattern.compile("[a-zA-Z_]+[a-zA-Z0-9_]*|[0-9]+|\\S");
        Pattern p = Pattern.compile("\\\"(\\\\.|[^\"\\\\])*\\\"|(>=|<=|==|!=|\\|\\||&&|>>|<<|\\+\\+|--)|[a-zA-Z_]+[a-zA-Z0-9_]*|[0-9]+|\\S");
        // Apply the matcher
        Matcher m = p.matcher(input);

        // Create an arraylist for processing
        ArrayList<Token> tokenList = new ArrayList<Token>();

        while (m.find()){
            tokenList.add(new Token(m.group()));
        }

        return tokenList;
    }

    // Static method to extract a token list from an arraylist of tokens
    // This is mostly meant for unit testing
    public static TokenType[] extractTokenTypes(ArrayList<Token> tokenList){
        TokenType[] tokenTypeArray = new TokenType[tokenList.size()];
        for (int i = 0; i < tokenList.size(); i++){
            tokenTypeArray[i] = tokenList.get(i).getType();
        }
        return tokenTypeArray;
    }


    public static boolean isSameType(){
        return false;
    }

    @Override
    public boolean equals(Object o){ //used to help the Unit Testing (TokenizerUnitTests.java)
        //System.out.print("Equals??");
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return token.type == this.type && token.tokenString.equals(this.tokenString);
    }
}
