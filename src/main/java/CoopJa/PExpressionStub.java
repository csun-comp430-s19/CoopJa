package CoopJa;

import java.util.LinkedHashMap;

public class PExpressionStub implements  PExpression { //unused, just temp stuff
    Token stubToken;
    public PExpressionStub(Token stubToken){
        this.stubToken = stubToken;
    }

    @Override
    public String generateString(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers) throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Stub (Delte this)");
    }
}
