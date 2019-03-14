package CoopJa;

// Bare-bones description any type of "atom" in an expression IE values, function calls, etc
public interface PExpressionAtom extends PExpressionParserElement,PExpression {
    //classes that implement this class

    //PExpressionAtomBooleanLiteral
    //PExpressionAtomNullLiteral
    //PExpressionAtomNumberLiteral
    //PExpressionAtomObjectConstruction
    //PExpressionAtomStringLiteral
    //PIdentifierReference

    //also implement PExpression:
    //PExpressionBinOp
    //PExpressionIdentifierReference
    //PExpressionVariable
    //PStatementFunctionCall
}
