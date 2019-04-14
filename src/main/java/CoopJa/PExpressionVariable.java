package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionVariable implements  PExpression, PExpressionAtom{
    public Token variable;

    public PExpressionVariable(Token variable){
        this.variable = variable;
    }


    @Override
    public String generateString(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        return variable.getTokenString();
    }
}
