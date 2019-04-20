package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionAtomStringLiteral implements PExpressionAtom {
    public Token literalToken;
    public PExpressionAtomStringLiteral(Token literalToken){
        this.literalToken = literalToken;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        return literalToken.getTokenString();
    }
}
