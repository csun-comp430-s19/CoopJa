package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

//import CoopJa.OuterScope;

/*This system is better just on the thoroughness,
  but what is one of the selling points is the passing of scope decs.
*/
//In lieu of Types, will use PObjects.

public class TypeChecker
{
  //This method is just to test the TypeChecker itself, so how another program would use TypeChecker.
  public static void main(String[] args) throws Exception 
  {
      String program_string = "public class foo{public int foo4 = 0;}" +
              "public class foo6 extends foo{public int foo4 = 1;}" +
              "public class foo2{" +
              "public string foo3 = 1 + \"string thingy\";" +
              "public string foo966;" +
              "public boolean foofi = true | 1 < 2;" +
              "public int foo8 = 1;" +
              "public int bar = foo8;" +
              "public int main(){" +
              "foo.foo4(); " +
              "foo9 = foo3;" +
              "foo9 = (1 + 9)*5;" +
              "for (int i = 0; i < 9; i = i+1;){" +
              "foo = foo + 5;" +
              "}" +
              "if (1 == 1){" +
              "int i = 0;" +
              "}" +
              "else{" +
              "int i = 1;" +
              "}" +
              "int i = 2;" +
              "return;" +
              "}" +
              "}";

      //tokenize 
      ArrayList<Token> tokenList = Token.tokenize(program_string);
      //Start funcj parser set up.
      Input<Token> tokenListInput = new TokenParserInput(tokenList);
      MainParser parsers = new MainParser();
      //(?)getOrThrow() ?
      //pProgram = parsed program.
      PProgram pProgram = parsers.programParser.parse(tokenListInput).getOrThrow();
      System.out.println();
      //For now, will call the typechecker through its constructor,
      //_but another option is to have typeCheck be static: TypeChecker.typeCheck(PProgram);
      //Set up the type checker, and constructor calls the typeCheck.
      TypeChecker typeChecker = new TypeChecker(pProgram);
      // //Do the typeChecking. non-static method.
      // typeChecker.typeCheck();
  }//End main() typechecker.
  
  //The entire parsed program constant.
  private /*final*/ PProgram inputProgram;
  
  /*The constructor is to initialize the field members that basically hold
    the information we need to effectively typecheck. A table to refer to.
  */
  
  /** (!) init members **/
  public TypeChecker(){;}
  public TypeChecker(final PProgram pProgram) throws Exception
  {    
    //Store the parsed input program.
    this.inputProgram = pProgram;
    
    //assert == assuming declaration.
    /*Get the maps of user-defined types*/
    
   /*Get the list of 'declarations' in the program.
       Declarations contain the information (needed) in the declaration of the type.
       +functionDefinitions
       +variable Declarations
       +other named types.
   */
   
    ScopeStruct classScopeStruct = new ScopeStruct();
    //Begin the typecheck. Entry point is the list of classes.
    for(PClassDeclaration classDeclaration : inputProgram.classDeclarationList)
    {
      //There is nothing defined outside of classes so the scope is empty? No, add this class.
      //(?) exp is an entry point?
      
      //OuterScope arg is to check what the outerscope is in case we are recurring,
      //_ so that we know what we can't do. Example: cant define classes or methods in a for loop.
      //OuterScope Arg prob might not be needed until statements, or at all.
      this.typeCheckClass(classDeclaration, classScopeStruct, OuterScope.CLASS); //scope = 0;
      classScopeStruct.addClass(classDeclaration.identifier.getTokenString()/*.toUpperCase()*/);      
    }
  }//End TypeChecker Constructor.
  
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

  // private PObject typeCheck(final PProgram pProgram, final List<HashMap<String, PObject>> scopeList, OuterScope parentScope) //not final because of casting.
  // {
    // final Type finalType;
    
    // //(!) Assume class defs ARE propogated down to other classes (useable).
    
  // }//End typeCheck( )

  private void typeCheckClass(final PClassDeclaration pClass, final ScopeStruct scopeStruct, OuterScope scopeLevel) throws Exception //not final because of casting.
  {
    //Start w/ a copy of the scope declarations.
    ScopeStruct thisScopeStruct = new ScopeStruct(scopeStruct);
    
    String strThisClassName = pClass.identifier.getTokenString();
    //First check if this class' name conflicts w/ an existing class or var.
    if(thisScopeStruct.alreadyExists(strThisClassName))
    {
        throw new Exception("Class " + strThisClassName + " is already defined.");
    }
    //Add this class to the scope.
    thisScopeStruct.addClass(pClass.identifier.getTokenString());//Identifier should directly be a String.
    
    //check access modifier. Should accept an accessModifierExpression instead.
    typecheckAccessModifier(pClass.accessModifier, (pClass instanceof PClassDeclaration)); //Have to pass in the Token bc it's more universal.       

    //Check if extends a valid class.
    if (pClass.extendsIdentifier != null) //the class does extend another
    {
        String strExtendingClass = pClass.extendsIdentifier.getTokenString();
        //Check if it extends itself w/c is an error
        if(strExtendingClass.equals(strThisClassName))
        {
          throw new Exception("Class " + strThisClassName + " cannot extend from itself!");
        }
        //check if it extends a class that is in scope.
        if( ! (thisScopeStruct.classAlreadyExists(strExtendingClass))) //check if this class (that the working class is supposed to extend) is known yet/exists
        {
            throw new Exception("Class Error: Cannot extend from class " + 
                                strExtendingClass + " because it does not exist.");                                
        }
    }//end check valid class extends.
    
    
    //(!)Left off. Check if you can just make a catch-all for these.
    //At least for starters.
    //That's it for class type checking.
    //Now we check the declarations.
    for(PDeclaration classMember : pClass.declarationList)
    {
      ;
    }//end going through declarationList.
    
  }//End class type check ().  

  // private PObject typeCheckClass(PProgram final pProgram, List<HashMap<String, PObject>> scopeList, OuterScope scopeLevel) //not final because of casting.
  // {
    // /* Maps || List of user-defined types.
       // Maps name to the (types) that define them.  
    // */
    // //Variables: name to type (Token)
    // HashMap<String, Token> mapVarsInScope;
    // //Methods: name to signature (return type & parameter order, type, & number.)
    // //(!)(?)(Answer is at EOL~) Is it an error to have another method
    // //_ w/ same signature except the order of the vars are different? Yes.
    // HashMap<String, PObject> mapMethodsInScope;
    // //Classes? (Not needed if not used by other classes,
    // //_ but nested classes are actually used in the syntax def.
    // //_ Assume we can use the class itself and any classes previously
    // //_ defined in this class.)    
    // List<String> mapClassInScope;
    
    // //Construct the scopeList from pProgram? Yes bc it will be used when we delve inside.
    
    // //Add this class to the scope.

    // //Accumulate the
    // scopeList.add( );
    // scopeList.add( );
    // scopeList.add( );
    
    
  // }
  
  public static void typecheckAccessModifier(Token input, boolean isClass) throws Exception 
  {
      //access modifier required in class, but will fail at parser level if not there
      if (isClass) 
      {
          if (!(input.getType() == Token.TokenType.KEYWORD_PUBLIC || 
                 input.getType() == Token.TokenType.KEYWORD_PRIVATE || 
                 input.getType() == Token.TokenType.KEYWORD_PROTECTED))          
          {
              throw new Exception("Class Typecheck Error: Class Access Modifier Invalid");
          }
      } 
      else 
      { //not class, access modifier may be blank
          if(!(input == null || 
               input.getType() == Token.TokenType.KEYWORD_PUBLIC || 
               input.getType() == Token.TokenType.KEYWORD_PRIVATE || 
               input.getType() == Token.TokenType.KEYWORD_PROTECTED))
          {
              throw new Exception("Declaration Error: Access Modifier Invalid");
          }
      }//end check if class.
  }//end typecheckAccessModifier()
  
  //Struct that contains the user-defined 'vars' for this scope level.
  /*private*/ class ScopeStruct  
  {
    //Variables: name to type (Token)
    private HashMap<String, Token> mapVarsInScope;
    //Methods: name to signature (return type & parameter order, type, & number.)
    //(!)(?)(Answer is at EOL~) Is it an error to have another method
    //_ w/ same signature except the order of the vars are different? Yes.
    private HashMap<String, SignatureStruct> mapMethodsInScope;
    //Classes? (Not needed if not used by other classes,
    //_ but nested classes are actually used in the syntax def.
    //_ Assume we can use the class itself and any classes previously
    //_ defined in this class.)    
    private List<String> mapClassInScope;
    
  /**** Begin Constructors ****/  
    public ScopeStruct()
    {
      this.mapVarsInScope = new HashMap<String, Token>();
      this.mapMethodsInScope = new HashMap<String, SignatureStruct>();
      this.mapClassInScope = new ArrayList<String>();
    }//constructor empty
    
    public ScopeStruct(HashMap<String, Token> mapVarsInScope,
                        HashMap<String, SignatureStruct> mapMethodsInScope,
                            List<String> mapClassInScope)
    {
      this.mapVarsInScope = mapVarsInScope;
      this.mapMethodsInScope = mapMethodsInScope;
      this.mapClassInScope = mapClassInScope;
    }//constructor individual scopes;
    
    public ScopeStruct(ScopeStruct scopeStruct)
    {
      //perform a deep copy of the ScopeStruct
      //vars copy
      this.mapVarsInScope = new HashMap<String, Token>(scopeStruct.mapVarsInScope.size());
      for(Map.Entry<String, Token> varEntry : scopeStruct.mapVarsInScope.entrySet())
      {
          this.mapVarsInScope.put(varEntry.getKey(), varEntry.getValue());
      }
      //methods copy
      this.mapMethodsInScope = new HashMap<String, SignatureStruct>();
      for(Map.Entry<String, SignatureStruct> methodEntry : scopeStruct.mapMethodsInScope.entrySet())
      {
          this.mapMethodsInScope.put(methodEntry.getKey(), methodEntry.getValue());
      }
      //class copy
      this.mapClassInScope = new ArrayList<String>();
      for(String classEntry : scopeStruct.mapClassInScope)
      {
          this.mapClassInScope.add(classEntry);
      }
    }//constructor ScopeStruct;
  /**** End Constructors ****/

  
  /**** Check if alreadyExists ****/
  //(i) Doesn't allow duplicates identifier of any type.
    boolean alreadyExists(String name)
    {
      boolean varsResult, methodResult, classResult;
      //check vars
        varsResult = this.mapVarsInScope.containsKey(name);
      //check methods
        methodResult = this.mapMethodsInScope.containsKey(name);
      //check classes
        classResult = this.mapClassInScope.contains(name);
        
        return (varsResult || methodResult || classResult);
    }//end alreadyExists( ).
    
    boolean variableAlreadyExists(String name)
    {
      return this.mapVarsInScope.containsKey(name);
    }//end variableAlreadyExists( ).  
    
    boolean methodAlreadyExists(String name)
    {       
      return this.mapMethodsInScope.containsKey(name);
    }//end methodAlreadyExists( ).    
    
    boolean classAlreadyExists(String name)
    {
      return this.mapClassInScope.contains(name);
    }//end class alreadyExists( ).    
  
    /**** Add( ) the entire scope type. ****/ //implied package private access
    void addVariables(HashMap<String, Token> mapVarsInScope)
    {
      this.mapVarsInScope = mapVarsInScope;
    }//vars    
    void addMethods(HashMap<String, SignatureStruct> mapMethodsInScope)
    {
      this.mapMethodsInScope = mapMethodsInScope;
    }//methods
    void addClasses(List<String> mapClassInScope)
    {
      this.mapClassInScope = mapClassInScope;
    }//classes
    
    
    /**** Update( ) a single scope type. Add an entry. Deletes needed? ****/ //implied package private access
    void addVariable(String variableName, Token type)
    {
      this.mapVarsInScope.put(variableName, type);
    }//vars    
    void addMethod(String methodName, SignatureStruct methodSignature)
    {
      this.mapMethodsInScope.put(methodName, methodSignature);
    }//methods
    void addClass(String classInScope)
    {
      //(!)It's up to the typechecker to check before adding. This is a bit dangerous (!).
      this.mapClassInScope.add(classInScope);
    }//classes    
    
  }//End class ScopeStruct
  /**********************************************/
  
  //Struct for method signature: (return type & parameter order, type, & number.)
  /*private*/ class SignatureStruct
  {
    Token returnType;       // REQUIRED
    ArrayList<Token> parameters; //parameters list. We just care about type, order, & size.
    
    //PVariableDeclaration;
    
    //(!)(!) Initialize the members.
    public SignatureStruct(){};    
    
    void addReturnType(Token returnType)
    {
      this.returnType = returnType;
    }
    
    //Will do equal checks outside so I can throw exceptions there. & it's the outerscope
    //_ class that does the type checking, not this class.
    
    // //Won't override equals()—don't want to override hashcode. hasho.
    // boolean equalReturnTypes(SignatureStruct otherSignatureStruct) throws Exception
    // {
      // //Check return types.
      // return ( ! (this.returnType.getTokenString().equals(
                  // otherSignatureStruct.returnType.getTokenString())));
    // }   
    
    void addParameters(ArrayList<Token> parameters)
    {
      this.parameters = new ArrayList<Token>(parameters.size());
      
      for(Token paramType : parameters)
      {
        this.parameters.add(paramType);
      }
    }
    
    void addParametersPVarDec(ArrayList<PVariableDeclaration> parameters)
    {
      this.parameters = new ArrayList<Token>(parameters.size());
      
      for(PVariableDeclaration param : parameters)
      {
        this.parameters.add(param.variableType);
      }
    }
  }//End Class ScopeStruct

     // // if(exp instanceof BinOpExp)
     // // {
         // // finalType = typeCheckBinOp(exp); //((BinOpExp) exp)
     // // }
     // // else if(exp instanceof UnOpExp)
     // // {
         // // finalType = typeCheckUnOpExp(exp);
     // // }
  // // }

       // // //beforehand checks if(exp instanceof BinOpExp){ typeCheckBinOp(ex)}
       // // public Type typeCheckBinOp(BinOpExp exp) throws TypeException
       // // {
           // // final Type leftType = checkType(exp.leftExp); //using as get/checkTypeOfExp(exp)
           // // final Type rightType = checkType(exp.rightExp);
           // // final Operator binOp = exp.operator;
           // // //Makes sure types are appropriate for the operator used.
           // // //Makes sure types are appropriate for each other.
           // // //recursively checks the operators.

           // // //Type check depends on type.
           // // //Operators are prob enums, so do that.
           // // switch(binOp)
           // // {
               // // //(!)Needs more operators.
          // // /**** Arithmetic Operators ****/
               // // case Operator.PLUS: // +
               // // case Operator.MINUS: // -
               // // case Operator.MULTIPLY: // *
               // // case Operator.DIVIDE: // /
          // // /**** Bitwise Operators ****/
               // // case Operator.BITAND: // &
               // // case Operator.BITXOR: // ^
               // // case Operator.BITOR: // |
               // // case Operator.SHFTLT: // <<
               // // case Operator.SHFTRT: // >>
               // // case Operator.INVERT: // ~

                   // // //Assume that the + operator works w/ both nums & strings.
                   // // //Check if it's a numberType (e.g. float, int, etc.)
                   // // if(leftType instanceof NumType)
                   // // {
                       // // if(rightType instanceof NumType)
                       // // {
                           // // //expecting another numType
                           // // return new NumType();
                       // // }
                       // // else//didn't get a numType.
                       // // {
                           // // throw new TypeException("Expected " +
                               // // leftType.typeName + ", but instead got type " + rightType.typeName);
                       // // }
                   // // }
                   // // //check if binOp is PLUS for concatenation.
                   // // if(binOp == Operator.PLUS)
                   // // {
                       // // if(rightType instanceof stringType)
                       // // {
                           // // //expecting another numType
                           // // return new stringType();
                       // // }
                       // // else//didn't get a numType.
                       // // {
                           // // throw new TypeException("Expected " +
                               // // leftType.typeName + ", but instead got type " + rightType.typeName);
                       // // }
                   // // }
                   // // throw new TypeException("Expected " +
                               // // new NumType().typeName + "or " + new StringType().typeName +
                               // // ", but instead got type "+leftType.typeName);
                   // // break;//Shouldn't reach break;

          // // /**** Comparison Operators ****/
               // // case Operator.EQ://==
               // // case Operator.NTEQ://!=
                   // // //Check if they're both the same type.
                   // // if(leftType instanceof (rightType.getClass()))
                   // // {
                       // // //expecting another numType
                       // // return new BoolType();
                   // // }

                   // // throw new TypeException("Types not equal. Left Hand is " +
                               // // new rightType().typeName + "and Right Hand is " + new rightType().typeName);
                   // // break;//Shouldn't reach break;
         // // /**** (Numerical) Comparison Operators ****/
               // // case Operator.GT:// >
               // // case Operator.GTEQ:// >=
               // // case Operator.LT:// <
               // // case Operator.:LTEQ:// <=
                   // // //Assume only works w/ number types, but can be changed later.
                   // // //Check if it's a numberType (e.g. float, int, etc.)
                   // // if(leftType instanceof numType && rightType instanceof numType)                    {
                       // // //expecting another numType
                       // // return new BoolType();
                   // // throw new TypeException("Expected " +
                               // // new NumType().typeName +
                               // // ", but instead got type "+leftType.typeName);
                   // // break;//Shouldn't reach break;
               // // default:
                   // // //If it reaches this, then the parser is messed up bc not a valid binop type.
                   // // /*throw new FatalException("ABORT! ABORT! "+ binOp.Operator +
                                   // // "is not a valid binary Operator. Check Parser is parsing correctly!");
                   // // */
                   // // break;
            // // }//End Switch BinOp
            // // //Switch should handle all possibilities.
       // // }//End typeCheckBinOp


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