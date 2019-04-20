package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionAtomNullLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomNullLiteral(Token literalToken){
        this.literalToken = literalToken;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) {
        return "NULL";
    }
}
