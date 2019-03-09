package main.java.CoopJa;

public enum Operator //Could further separate to BinaryOperator & UnaryOperator
{
    /*Arithmetic Operators*/
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    /*Comparison Operators*/
    EQ,//==
    NTEQ,//!=
    GT,
    LT,
    GTE,
    LTE,
    /*Bitwise Operators*/
    BITAND,
    //==|
    XOR, //^
    IOR, //|
    SHFTRT, //>> Shift Right Shght
    SHFTLT, //<< Shift Left  Sheft
    INVERT, //~
}        