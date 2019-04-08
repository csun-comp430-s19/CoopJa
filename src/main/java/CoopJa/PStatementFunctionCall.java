package CoopJa;

import java.util.ArrayList;

public class PStatementFunctionCall implements PStatement, PExpression, PExpressionAtom{
    // Statements are in the form
    // foo.fooCall(expressions, moreExpressions)
    // Identifier, identifier2, then a list of expressions
    public Token identifier;
    public ArrayList<PExpression> expressionsInput;

    public PStatementFunctionCall(Token identifier, ArrayList<PExpression> expressionsInput){
        this.identifier = identifier;
        this.expressionsInput = expressionsInput;

    }

    // TODO: Generate function declarations AND calls
    @Override
    public String generateString() throws CodeGenException {
        throw new CodeGenException(CodeGenException.UNIMPLEMENTED_EXPRESSION_TYPE + "Function Call");
    }
}
