package CoopJa;

import java.util.LinkedHashMap;

public class PStatementBreak implements PStatement {
    public Token token;
    public PStatementBreak(Token token){
        this.token = token;
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        return "break";
    }
}
