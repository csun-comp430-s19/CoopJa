package CoopJa;

import java.util.ArrayList;

public interface PExpression {
    //classes that implement this class

    //interface:
    //PExpressionAtom

    //part of the above interface as well:
    //PExpressionBinOp
    //PExpressionIdentifierReference
    //PExpressionVariable
    //PStatementFunctionCall

    //other:
    //PExpressionStub

    String generateString() throws CodeGenException;

    // TODO: REAL UNIT TESTS
    public static void main(String[] args){
        String foo = "2 + 16 / 4 * 3 + 1 - 6";
        ArrayList<Token> fooToken = Token.tokenize(foo);
        MainParser parsers = new MainParser();
        PExpression fooTester2 = parsers.expressionLargeParser.parse(new TokenParserInput(fooToken)).getOrThrow();
        try {
            System.out.println(fooTester2.generateString());
        } catch (CodeGenException e) {
            e.printStackTrace();
        }
    }
}
