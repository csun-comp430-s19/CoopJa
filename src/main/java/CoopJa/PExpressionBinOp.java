package CoopJa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PExpressionBinOp implements PExpression {
    // This describes a binary expression binary tree

    // Left-hand and Right-hand pointers for this particular node
    public PExpression lhs;
    public PExpression rhs;

    // The operator token
    public Token operatorToken;
    
    public static PExpression computeExpression(ArrayList<PExpressionParserElement> parserElements, int min_prec){
        PExpression lhs_local = (PExpressionAtom)parserElements.get(0);
        parserElements.remove(0);
        while (true){
            PExpressionParserElement currentToken;
            try {
                currentToken = parserElements.get(0);
            }
            catch(Exception IndexOutOfBounds){
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
}
