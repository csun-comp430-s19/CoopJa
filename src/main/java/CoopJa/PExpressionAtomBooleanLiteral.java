package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionAtomBooleanLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomBooleanLiteral(Token literalToken){
        this.literalToken = literalToken;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers) {
        return (literalToken.getType() == Token.TokenType.KEYWORD_TRUE) ? "1" : "0";
    }
}
