package main.java.CoopJa;

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

    // It would be nice to have some quick lookup on the associative and precedence rules of a given token




    // This should be an Implementation of the "Precedence Climbing" Expression algorithm
    public static PExpression computeExpression(ArrayList<PExpressionParserElement> parserElements, int min_prec){
        // Grab the first token, it should be an expression token.
        PExpression lhs_local = (PExpressionAtom)parserElements.get(0);
        PExpression rhs_local;
        parserElements.remove(0);
        int next_min_prec;

        while (true){
            PExpressionOperator currentToken;
            try {
                currentToken = (PExpressionOperator) parserElements.get(0);
                parserElements.remove(0);
            }
            catch (Exception IndexOutOfBoundsException){
                currentToken = null;
            }

            if (currentToken != null && currentToken.precedence() >= min_prec){
                if (currentToken.isLeftAssociative()){
                    next_min_prec = currentToken.precedence() + 1;
                }
                else{
                    next_min_prec = currentToken.precedence();
                }
                rhs_local = computeExpression(parserElements, next_min_prec);
                lhs_local = new PExpressionBinOp(lhs_local, rhs_local);
            }
            else{
                return lhs_local;
            }

        }
    }

    public PExpressionBinOp(PExpression lhs, PExpression rhs){
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
