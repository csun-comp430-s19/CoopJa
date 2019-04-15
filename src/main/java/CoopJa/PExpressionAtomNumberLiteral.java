package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionAtomNumberLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomNumberLiteral(Token literalToken){
        this.literalToken = literalToken;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) {
        return literalToken.getTokenString();
    }
}
