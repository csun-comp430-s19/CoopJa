package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionVariable implements  PExpression, PExpressionAtom{
    public Token variable;

    public PExpressionVariable(Token variable){
        this.variable = variable;
    }


    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers) throws CodeGenException {
        StringBuilder returnString = new StringBuilder();
        if (globalMembers != null && globalMembers.containsKey(variable.getTokenString())){
            //returnString.append(variable.getTokenString() + "->");
            returnString.append("this->");
        }
        returnString.append(variable.getTokenString());
        return returnString.toString();
    }
}
