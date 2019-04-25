package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionAtomObjectConstruction implements PExpressionAtom {
    public Token identifier;
    public PExpressionAtomObjectConstruction(Token identifier){
        this.identifier = identifier;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        //throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Object Construction");
        // TODO: THIS IS PRETTY HACKY (See variable declaration for actual constructor crap)
        return "{}";
    }
}
