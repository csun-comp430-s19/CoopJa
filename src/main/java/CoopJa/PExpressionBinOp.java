package CoopJa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PExpressionBinOp implements PExpression, PExpressionAtom {
    // This describes a binary expression binary tree

    // Left-hand and Right-hand pointers for this particular node
    public PExpression lhs;
    public PExpression rhs;

    // The operator token
    public Token operatorToken;
    
    public static PExpression computeExpression(ArrayList<PExpressionParserElement> parserElements, int min_prec){
        PExpression lhs_local;
        if (parserElements.size() > 0){
            lhs_local = (PExpressionAtom)parserElements.get(0);
            parserElements.remove(0);
        }
        else{
            lhs_local = new PExpressionBinOp(null, null, null);
        }

        while (true){
            PExpressionParserElement currentToken;
            if (parserElements.size() > 0) {
                currentToken = parserElements.get(0);
            }
            else{
                break;
            }

            if (currentToken == null|| !(currentToken instanceof PExpressionOperator) || ((PExpressionOperator)currentToken).precedence() < min_prec){
                break;
            }
            int next_min_prec;
            if (((PExpressionOperator) currentToken).isLeftAssociative()){
                next_min_prec = ((PExpressionOperator) currentToken).precedence() + 1;
            }
            else{
                next_min_prec = ((PExpressionOperator) currentToken).precedence();
            }
            parserElements.remove(0);
            PExpression rhs_local = computeExpression(parserElements, next_min_prec);
            lhs_local = new PExpressionBinOp(lhs_local, rhs_local, ((PExpressionOperator) currentToken).operatorToken);
        }
        return lhs_local;
    }

    public PExpressionBinOp(PExpression lhs, PExpression rhs, Token operatorToken){
        this.lhs = lhs;
        this.rhs = rhs;
        this.operatorToken = operatorToken;
    }

    @Override
    public String generateString(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException {
        return "(" + lhs.generateString(globalMembers, localMembers) + ")" + operatorToken.getTokenString() + "(" + rhs.generateString(globalMembers, localMembers) + ")";
    }
}
