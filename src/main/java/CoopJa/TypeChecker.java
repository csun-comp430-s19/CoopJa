package CoopJa;

import java.util.HashMap;

public class TypeChecker
{
    /*Get the list of 'declarations' in the program.
        Declarations contain the information (needed) in the declaration of the type.
        +functionDefinitions
        +variable Declarations
        +other named types.
    */

    /*Type Checker must parse expressions, must take scope into account.*/

//     HashMap<String, Type> variableDecMap;
//
//     void createVarDecMap(Program prog)
//     {
//         variableDecMap = null;
//         //Populate the map in order to check for the existence of the variables.
//         for(int i = 0; i < prog.variableDecs.length(); i++)
//             variableDecMap.put(variableDecs[i].name, variableDecs[i].type);
//     }
        //Gets type from Exp
        public Type typeCheckExp(Exp exp)
        {
            final Type finalType;

            if(exp instanceof BinOpExp)
            {
                finalType = typeCheckBinOp(exp); //((BinOpExp) exp)
            }
            else if(exp instanceof UnOpExp)
            {
                finalType = typeCheckUnOpExp(exp);
            }
        }

        //beforehand checks if(exp instanceof BinOpExp){ typeCheckBinOp(ex)}
        public Type typeCheckBinOp(BinOpExp exp) throws TypeException
        {
            final Type leftType = checkType(exp.leftExp); //using as get/checkTypeOfExp(exp)
            final Type rightType = checkType(exp.rightExp);
            final Operator binOp = exp.operator;
            //Makes sure types are appropriate for the operator used.
            //Makes sure types are appropriate for each other.
            //recursively checks the operators.

            //Type check depends on type.
            //Operators are prob enums, so do that.
            switch(binOp)
            {
                //(!)Needs more operators.
           /**** Arithmetic Operators ****/
                case Operator.PLUS: // +
                case Operator.MINUS: // -
                case Operator.MULTIPLY: // *
                case Operator.DIVIDE: // /
           /**** Bitwise Operators ****/
                case Operator.BITAND: // &
                case Operator.BITXOR: // ^
                case Operator.BITOR: // |
                case Operator.SHFTLT: // <<
                case Operator.SHFTRT: // >>
                case Operator.INVERT: // ~

                    //Assume that the + operator works w/ both nums & strings.
                    //Check if it's a numberType (e.g. float, int, etc.)
                    if(leftType instanceof NumType)
                    {
                        if(rightType instanceof NumType)
                        {
                            //expecting another numType
                            return new NumType();
                        }
                        else//didn't get a numType.
                        {
                            throw new TypeException("Expected " +
                                leftType.typeName + ", but instead got type " + rightType.typeName);
                        }
                    }
                    //check if binOp is PLUS for concatenation.
                    if(binOp == Operator.PLUS)
                    {
                        if(rightType instanceof stringType)
                        {
                            //expecting another numType
                            return new stringType();
                        }
                        else//didn't get a numType.
                        {
                            throw new TypeException("Expected " +
                                leftType.typeName + ", but instead got type " + rightType.typeName);
                        }
                    }
                    throw new TypeException("Expected " +
                                new NumType().typeName + "or " + new StringType().typeName +
                                ", but instead got type "+leftType.typeName);
                    break;//Shouldn't reach break;

           /**** Comparison Operators ****/
                case Operator.EQ://==
                case Operator.NTEQ://!=
                    //Check if they're both the same type.
                    if(leftType instanceof (rightType.getClass()))
                    {
                        //expecting another numType
                        return new BoolType();
                    }

                    throw new TypeException("Types not equal. Left Hand is " +
                                new rightType().typeName + "and Right Hand is " + new rightType().typeName);
                    break;//Shouldn't reach break;
          /**** (Numerical) Comparison Operators ****/
                case Operator.GT:// >
                case Operator.GTEQ:// >=
                case Operator.LT:// <
                case Operator.:LTEQ:// <=
                    //Assume only works w/ number types, but can be changed later.
                    //Check if it's a numberType (e.g. float, int, etc.)
                    if(leftType instanceof numType && rightType instanceof numType)                    {
                        //expecting another numType
                        return new BoolType();
                    throw new TypeException("Expected " +
                                new NumType().typeName +
                                ", but instead got type "+leftType.typeName);
                    break;//Shouldn't reach break;
                default:
                    //If it reaches this, then the parser is messed up bc not a valid binop type.
                    /*throw new FatalException("ABORT! ABORT! "+ binOp.Operator +
                                    "is not a valid binary Operator. Check Parser is parsing correctly!");
                    */
                    break;
             }//End Switch BinOp
             //Switch should handle all possibilities.
        }//End typeCheckBinOp


    /*Make a map of named types to their basic type or signature.
    */

    /* Type check method either a bunch of typecheck methods
        1 for each type, or a catch-all method.
    */
}//End class TypeChecker

/** Test Expression Classes for TypeChecker. Should be their own files. **/

// public interface Exp
// { 
//     void typeName;
// }
// //class for binary expressions
// public class BinOpExp implements Exp
// {
//     //leftExp
//     Exp 
// }    