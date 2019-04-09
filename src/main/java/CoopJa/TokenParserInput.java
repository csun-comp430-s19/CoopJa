package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

public class TokenParserInput implements Input<Token> {

    private ArrayList<Token> tokenList;
    private int position;
    private final TokenParserInput other;

    public ArrayList<Token> getTokenList() {
        return tokenList;
    }

    public TokenParserInput(ArrayList<Token> tokenList){
        this.tokenList = tokenList;
        position = 0;
        other = new TokenParserInput(tokenList, this);
    }

    public TokenParserInput(ArrayList<Token> tokenList, TokenParserInput other){
        this.tokenList = tokenList;
        position = 0;
        this.other = other;
    }

    public TokenParserInput setPosition(int position){
        this.position = position;
        return this;
    }

    @Override
    public boolean isEof() {
        return position >= tokenList.size();
    }

    @Override
    public Token get() {
        if (isEof()){
            throw new RuntimeException("End of Input");
        }
        else {
            return tokenList.get(position);
        }
    }

    @Override
    public Input<Token> next() {
        //position++;
        return other.setPosition(position+1);
    }

    @Override
    public Object position() {
        return position;
    }

    @Override
    public boolean equals(Object o){
        System.out.printf("Equals called\n");
        return false;
    }
}
