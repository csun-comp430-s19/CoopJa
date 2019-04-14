package CoopJa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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

    String generateString(LinkedHashMap<String, Object> globalMembers, LinkedHashMap<String, Object> localMembers) throws CodeGenException;

    // TODO: REAL UNIT TESTS
    public static void main(String[] args){
        //String foo = "2 + 16 / 4 * 3 + 1 - 6";
        //String foo = "63 / 1 / 3 / 7 / 1";
        //String foo = "256 / 2 * 3 -50 / 5 + 26";
        //String foo = "9 + 7 - 20"; //negative test
        //String foo = "( 8 * 7 - 1 ) / 5"; //paren test
        String foo = "38 / 3 * 2 - 5 / 2"; //decimal not kept or rounded
        ArrayList<Token> fooToken = Token.tokenize(foo);
        MainParser parsers = new MainParser();
        PExpression fooTester2 = parsers.expressionLargeParser.parse(new TokenParserInput(fooToken)).getOrThrow();
        try {
            System.out.println(fooTester2.generateString(null, null));
        } catch (CodeGenException e) {
            e.printStackTrace();
        }
    }
}
