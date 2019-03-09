package java.main.CoopJa;

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
    
     
        //beforehand checks if(exp instanceof BinOpExp){ typeCheckBinOp(ex)}
        void typeCheckBinOp(BinOpExp exp)
        {
            //Type check depends on type.
            //Operators are prob enums, so do that. 
            switch(exp.operator)
            {
                //Needs more operators.
                case Operator.Plus: //+
                    //typecheck these, make sure they're num types or strings or correct type.
                case Operator.Equals://==
                
                case Operator.GT://>
                
                case Operator.GTEq://>=
                
                case Operator.LT://<
                
                case Operator.:LTEq://<=
                
                default: 
                    //If it reaches this, then the parser is messed up bc not a balid op type.                    
             }                
        }//End typeCheckBinOp
    /*Make a map of named types to their basic type or signature.
    */
    
    /* Type check method either a bunch of typecheck methods
        1 for each type, or a catch-all method.
    */       
}