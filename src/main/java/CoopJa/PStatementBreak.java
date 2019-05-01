package CoopJa;

import java.util.LinkedHashMap;

public class PStatementBreak implements PStatement {
    public Token token;
    public PStatementBreak(Token token){
        this.token = token;
    }

    @Override
    public String generateCodeStatement(String globalClassName, LinkedHashMap<String, String> globalMembers, LinkedHashMap<String, String> localMembers, int blockLevel) throws CodeGenException {
        return "break";
    }
}
