package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionAtomNullLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomNullLiteral(Token literalToken){
        this.literalToken = literalToken;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers) {
        return "NULL";
    }
}
