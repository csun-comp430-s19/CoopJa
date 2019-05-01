package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionAtomNumberLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomNumberLiteral(Token literalToken){
        this.literalToken = literalToken;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers) {
        return literalToken.getTokenString();
    }
}
