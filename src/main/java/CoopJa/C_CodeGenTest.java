package CoopJa;

import java.io.*;
import java.util.ArrayList;
import java.lang.StringBuilder;


//(i) Whenever there's a class variable declaration (not definition),
//_ it will have to be initialized then, but not necessarily on the same line.
//Left off at: building main function, generateCode(final PStatement ...), control statements.
public class C_CodeGenTest 
{
    /*(!) Can include stdio.h by default or
      can add it if there's a println statement,
      in that case not making it final.
    */
    private static StringBuilder programHeader;// = 
    //"#include <stdio.h>\n"; //(i)unimplemented)Should be empty, bc will add header files dynamically as needed.

    //(i)(unimplemented)There should be a newline between the headers & the functions.
    
    private static StringBuilder structSectionStrBuilder;

    private static StringBuilder functionSectionStrBuilder;// = new StringBuilder();

    private static final String MAIN_START =
                    "\n" +
                    "int main(int argc, char **argv)\n" +
                    "{\n";

    private static StringBuilder programContentStrBuilder;

    private static final String MAIN_END = "" + ///XXXXXXXXXXwill clean up later
                    "\treturn 0;\n" +
                    "}";

    private static StringBuilder finalOutputProgramStrBuilder;// = new StringBuilder(programHeader);
    
    private static PProgram inputProgram;

    public C_CodeGenTest() 
    {
        programHeader = new StringBuilder("#include <stdio.h>\n");
                              // "#include <stdio.h>\n"+
                              // "#include <stdlib.h>\n"+
                              // "#include <string.h>"); ////XXXXXXXXXXXXXXxwill clean up later

        structSectionStrBuilder = new StringBuilder();
        functionSectionStrBuilder = new StringBuilder("\n");//newline separates functions from main().
        //This var will hold the string of the program content going inside main().
        programContentStrBuilder = new StringBuilder();
        finalOutputProgramStrBuilder = new StringBuilder();
        //inputProgram = new PProgram();
    }

    public static void main(String[] args) throws IOException 
    {
        String programString = "public class example" +
                               "{"+
                                  "public boolean cool = true;" + //\"Cool1\" + \"yeah\";" +
                                  "public void method1(int one, int two)" +
                                  "{" +
                                      "String three = \"Three\";" +
                                     //"println(\"Print From Function\")";
                                  "}\n" +
                                  "public int main()"+
                                  "{"+
                                      "int variable = 370;"+
                                      "example exampleVar;"+
                                      "exampleVar.method1(1, 2);"+
                                  "}"+
                                "}";

        //Get a println statement expression.
        String printLineString = "println(\"Print Me\")";
        ArrayList<Token> printLnTokens = Token.tokenize(printLineString);
        TokenParserInput printLnTokenList = new TokenParserInput(printLnTokens);

        MainParser parsers = new MainParser();
        PStatementPrintln printLnExpression = parsers.printlnParser.parse(printLnTokenList).getOrThrow();
        //PExpression fooTester2 = parsers.expressionLargeParser.parse(new TokenParserInput(fooToken)).getOrThrow();

        //Parse a program.
        ArrayList<Token> programTokenList = Token.tokenize(programString);
        TokenParserInput programTokenListInput = new TokenParserInput(programTokenList);

        PProgram programExpression = parsers.programParser.parse(programTokenListInput).getOrThrow();
        //println doesn't parse well, so making a literal one.
        PStatementPrintln expression = new PStatementPrintln(new Token("\"Print Me\""));

        //Instantiate class to use the members.
        C_CodeGenTest codeGenerator = new C_CodeGenTest();
        //(!) Will pass PProgram eventually.
        //codeGenerator.generateCode(printLnExpression);
        codeGenerator.generateCode(programExpression);

    }// main()

/****************************** PPROGRAM ****************************/
    //(!) Will make accept a PProgram & will return String, for now just prints.
    public void generateCode(final PProgram pProgram) //throws IOException 
    {
        //Get reference to pProgram
        inputProgram = pProgram;
        try 
        {
            //Go through the class declarations.
            for(PClassDeclaration classDeclaration : inputProgram.classDeclarationList)
            {   
                //generate the code for the class Declarations.
                generateCode(classDeclaration, 0);
            } //(!) Delete unneeded lines after this in this method.
            //Add / append the program content until it's all processed.
            //Can be in a loop if needed.
            //_(!)programContentStrBuilder.insert(0, getIndents(printLnExpression.getScope()));
          /*Actually, since we are diving into the code linearly, the scope will be directly known by
            this program.
          */
            //Don't have any info on scope, I believe, so don't know how many \t to prepend.

            //Add the necessary header files.
            // if(printLnExpression.includeRequired())
            // {
            // if( ! (includeExists(programHeader, printLnExpression.includeRequired())))
            // programHeader.append(printLnExpression.includeRequired());
            // }

            //System.out.println(printLnExpression.generateString());
            //System.out.println("Class: "+fooTester2.getClass());

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        //Add the last-msecond touches
        programHeader.append("\n");
        
        //Append the program headers
        finalOutputProgramStrBuilder.append(programHeader.toString());
        //Append structs to the program
        finalOutputProgramStrBuilder.append(structSectionStrBuilder.toString());
        //Append any functions.
        finalOutputProgramStrBuilder.append(functionSectionStrBuilder.toString());
        //Append the main start
        finalOutputProgramStrBuilder.append(MAIN_START); //not a stringBuilder.

        //Append the program content to the programHeader at this point.
        finalOutputProgramStrBuilder.append(programContentStrBuilder.toString());
        //Separate the program content from the return statement. 
        //_ Can be added in programContentStrBuilder building? (?)
        finalOutputProgramStrBuilder.append("\n");

        //Append the program Footer to the finalOutputProgramStrBuilder.
        finalOutputProgramStrBuilder.append(MAIN_END);

        System.out.println(finalOutputProgramStrBuilder);

        //Send the program to be written to a file in the current dir.
        String outputFileName = "HelloWorld.c";

        //will make better solution later XXXXXXX
        //this converts the string to an arraylist of strings, for writing purposes need platform's line separator.
        //ArrayList<String> progLines = StringtoArrayList(finalOutputProgramStrBuilder.toString());
        //writeCompleteFile(progLines, new File(""), outputFileName); //write output to a file

    }//end generateCode PProgram

/************* CLASS DEC ****************************/
    public static void generateCode(final PClassDeclaration classDeclaration, final int scopeLevel)
    {
        //scopeLevel is not used since classDeclaration should be in the 0th scope, but here for consistency.
        /*This class is responsible for making the complete struct & its members.*/
        /*(!)(?) How will class extension be handled? 
          One possibility is just adding the same members from the class that's being extended.
        */
        
        StringBuilder structStrBuilder = new StringBuilder(
                                          "typedef struct"+ "\n" + /*classDeclaration.identifier.getTokenString()*/
                                          "{\n");
        //Now we add any variable & function declarations it might have.
        for(PDeclaration pDeclaration : classDeclaration.declarationList)
        {   
            //Increment the scope level to tab it.
            //generateCode will return a string of the pDeclaration
            /*(i)Functions should be added in 3 places: 
                inside struct they're declared,
                function area above main(),
                in program content inside main() assigned to function() * of struct (member).
                The identifier in struct takes precedence, just as identifier of struct instance
                takes precedence over struct name.
                //Constructor will be treated differently. It will just be an initial assignment in main().
            */
            if(pDeclaration instanceof PVariableDeclaration)
            {   //arg true: //Indicate that it's declared in a struct so can't be assigned.
                generateCode((PVariableDeclaration)pDeclaration, scopeLevel+1, structStrBuilder, true);

            }
            if(pDeclaration instanceof PStatementFunctionDeclaration)
            {    
                //Should have v for consistency, yet functionDeclaration, is more complicated, so no.
                //addTabs(scopeLevel+1);
                generateCode((PStatementFunctionDeclaration)pDeclaration, scopeLevel+1, structStrBuilder);
            }
        }//end adding struct member declarations.

        //Add the name of the struct so we can declare like: MyClass classInstance;
        structStrBuilder.append("} " + classDeclaration.identifier.getTokenString() + "Struct;\n");
        //Add the entire struct to the struct section.
        structSectionStrBuilder.append(structStrBuilder.toString());
    }
    
/***************** FUNCTION DEC *************************/
    //This method adds to the class' static members for function section &, but it adds t
    public static void generateCode(final PStatementFunctionDeclaration functionDeclaration, final int scopeLevel, StringBuilder structStrBuilder)
    {
      /* This method's job is to generate the code for the struct's methods*/
      //(!)(!) Check if it's a constructor, so we need the name of the class, else in previous callback
      //(!)(!) Check if it's main(), in that case these things will get added to the main program content,
      //      but not before aggregating it in functionInMainBuilder(?).
      //_ we can check before calling this function, and call another function instead.
      StringBuilder functionSectionBuilder = new StringBuilder();
      StringBuilder functionInMainBuilder = new StringBuilder();
      // StringBuilder functionPtrInStructBuilder = new StringBuilder();
      
      // System.out.println("% Function name: "+functionDeclaration.identifier.getTokenString());
      //Check if function is not main
      if( ! functionDeclaration.identifier.getTokenString().equalsIgnoreCase("MAIN"))
      {
          /*Begin add function to struct as function pointer*/
          addTabs(structStrBuilder, scopeLevel);
          //Make it a function pointer.
          structStrBuilder.append(functionDeclaration.returnType.getTokenString() +
                                    " (*" + functionDeclaration.identifier.getTokenString() + ")");
                                    // "(");
          //Add parameters list.
          structStrBuilder.append("(");
          //for(PVariableDeclaration parameterDeclaration : functionDeclaration.variableDeclarations)
          for(int i = 0; i < functionDeclaration.variableDeclarations.size() ; i++)
          {
              //generateCodeParameter(structStrBuilder, functionDeclaration.variableDeclarations.get(i));
              generateCode(functionDeclaration.variableDeclarations.get(i), scopeLevel+1, structStrBuilder, true, false);
              if(i+1 < functionDeclaration.variableDeclarations.size())
                  structStrBuilder.append(", ");
          }
          structStrBuilder.append(");\n");
          /*End add function pointer to struct.*/

          /*Begin add function to function section*/
          //(!)(!) Try keeping both the member & declared function w/ the same name, see if there's a conflict.
          functionSectionBuilder.append(functionDeclaration.returnType.getTokenString() + " " +
                                        functionDeclaration.identifier.getTokenString());
          //Add parameters list.
          functionSectionBuilder.append("(");
          //for(PVariableDeclaration parameterDeclaration : functionDeclaration.variableDeclarations)
          for(int i = 0; i < functionDeclaration.variableDeclarations.size() ; i++)
          {
              //generateCodeParameter(functionSectionBuilder, functionDeclaration.variableDeclarations.get(i));
              generateCode(functionDeclaration.variableDeclarations.get(i), scopeLevel+1, functionSectionBuilder, true, false); //forceDeclaration, don't add tabs.
              if(i+1 < functionDeclaration.variableDeclarations.size())
                  functionSectionBuilder.append(", ");
          }
          functionSectionBuilder.append(")\n");
          //Build the body of the function.
          functionSectionBuilder.append("{\n");
          /** Mistook variableDeclarations to be variable decs in the function body, 
            so below comments are deletable.
          **/
          //variableDeclarations, is actually parameterDeclarations. Common sense is not so common...
          // // //Add the variable declarations in the body.
          // // for(PVariableDeclaration variableDeclaration : functionDeclaration.variableDeclarations)
          // // {
              // // generateCode(variableDeclaration, scopeLevel+1, functionSectionBuilder);
              // // functionSectionBuilder.append(";\n");
          // // }
          
          //Add the statements in the body.
          for(PStatement functionStatement : functionDeclaration.statementList)
          {
              //Since we are defining the function at a different level from the struct, we don't need to add to the scope level.
              generateCode(functionStatement, scopeLevel, functionSectionBuilder);
              //The return, if any, should be valid. Should have a correct return if the program made it this far in the compiler.
          }
          //Close the body of the function.
          functionSectionBuilder.append("}\n");
          //Add it to the static function
          functionSectionStrBuilder.append(functionSectionBuilder.toString());
          /*End add function to function section*/
      }//end check if main.
      else
        generateCodeMainFunction(functionDeclaration, scopeLevel);
      /*Begin add function in main()*/
          
      /*End add function in main()*/
      
    } // generateCode for functionDeclarations.
    
    private static void generateCodeMainFunction(final PStatementFunctionDeclaration functionDeclaration, final int scopeLevel) //, StringBuilder structStrBuilder)
    {
      //Add the statements in the body.
      for(PStatement functionStatement : functionDeclaration.statementList)
      {//The scope level
          generateCode(functionStatement, scopeLevel, programContentStrBuilder);
          //The return, if any, should be valid. Should have a correct return if the program made it this far in the compiler.
      }
    }
    
/***************** VAR DEC *************************/

    //Using function overloading to create a wrapper to have default parameter value type functionality.
    //For variable definitions. 
    public static void generateCode(final PVariableDeclaration variableDeclaration, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {   //don't force declaration, and add tabs.
        generateCodeVarDeclaration(variableDeclaration, scopeLevel, enclosingBlockStrBuilder, false, true);
        //(!) Could have also chained:
        //generateCode(variableDeclaration, scopeLevel, enclosingBlockStrBuilder, false);
        
    }// generateCode for varDeclarations.
    
    //For declaration statements. Default is adding tabs.
    public static void generateCode(final PVariableDeclaration variableDeclaration, final int scopeLevel, StringBuilder enclosingBlockStrBuilder, final boolean forceDeclaration)
    {
        generateCodeVarDeclaration(variableDeclaration, scopeLevel, enclosingBlockStrBuilder, forceDeclaration, true);
    }// generateCode for varDeclarations.

    //For parameters.
    public static void generateCode(final PVariableDeclaration variableDeclaration, final int scopeLevel, StringBuilder enclosingBlockStrBuilder, final boolean forceDeclaration, final boolean addTabs)
    {
        generateCodeVarDeclaration(variableDeclaration, scopeLevel, enclosingBlockStrBuilder, forceDeclaration, addTabs);
    }// generateCode for varDeclarations.
    
    public static void generateCodeVarDeclaration(final PVariableDeclaration variableDeclaration, final int scopeLevel, StringBuilder enclosingBlockStrBuilder, final boolean forceDeclaration, final boolean addTabs)
    {
        /* This method's job is to generate the code for the variables in enclosing blocks. or branch*/
        //
        //System.out.println("*From PVariableDeclaration:"+variableDeclaration.identifier.getTokenString()+". Received scope level:"+scopeLevel);
        //(!)(i) Will be the responsibility of caller to add tabs, so that all var decs can use this function.
        String typeStr = typeTranslatorRough(variableDeclaration); //okenString());
        
        if(addTabs)
        {
            addTabs(enclosingBlockStrBuilder, scopeLevel);
        }
        //type variableName
        enclosingBlockStrBuilder.append(typeStr+ " " + variableDeclaration.identifier.getTokenString());
        
        if( ! forceDeclaration) //No assignments allowed in struct nor parameters.
        {
          if(variableDeclaration.assignment != null)
          {
              enclosingBlockStrBuilder.append(" = "); // + generateCode(variableDeclaration.assignment) vs. vv
              generateCode(variableDeclaration.assignment, scopeLevel, enclosingBlockStrBuilder);
          }
          else if(variableDeclaration.variableType.getType() == Token.TokenType.IDENTIFIER) //if it's a class basically
          { //(!) Conditional might cause problems, for example if we do: structA var = structB. will var's members be initializd?
              enclosingBlockStrBuilder.append(" = ");
              //Search for the type by going through the program, & initialize the members if necessary.
              
              generateCodeInitializeStructs(variableDeclaration, scopeLevel, enclosingBlockStrBuilder);
          }
        }

        //If we don't add tabs & force declaration, then it's a parameter
        if(addTabs)
        {
          enclosingBlockStrBuilder.append(";\n");
        }
    }// generateCode for varDeclarations.
    
    public static void generateCodeParameter(StringBuilder structStrBuilder, final PVariableDeclaration parameterDeclaration)
    {
        String typeStr = typeTranslatorRough(parameterDeclaration); //.variableType.getType());//getTokenString());
        structStrBuilder.append(typeStr + " " + parameterDeclaration.identifier.getTokenString());
    }//code generation parameters.
    
/***************** PSTATEMENTS ******************************/
    //Called by: generateCode(PStatementFunctionDeclaration) in function builder section.
    public static void generateCode(final PStatement statement, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
      //This function forwards the pstatement to the correct function.
      //PExpressionIdentifierReference
      //PIdentifierReference
      //PStatementBreak
      //PStatementForStatement
      //PStatementFunctionCall
      //PStatementFunctionDeclaration
      //PStatementIfStatement
      //PStatementPrintln
      //PStatementReturn
      //PStatementWhileStatement
      //PVariableAssignment
      //PVariableDeclaration
      
      //Let's handle the control statements first.
      if(statement instanceof PStatementForStatement)
      {
          // generateCode(statement, scopeLevel, enclosingBlockStrBuilder);
      }
      if(statement instanceof PStatementWhileStatement)
      {
          // generateCode(statement, scopeLevel, enclosingBlockStrBuilder);
      }
      if(statement instanceof PStatementIfStatement)
      {
          // generateCode(statement, scopeLevel, enclosingBlockStrBuilder);
      }
      // (!)(?) PVariableAssignment not used?
      if(statement instanceof PVariableAssignment)
      {
          // System.out.println("\t\t\tIs a variable Assignment.");
          //generateCode((PVariableAssignment)statement, scopeLevel, enclosingBlockStrBuilder);
      }
      
      if(statement instanceof PExpressionIdentifierReference)
      {
          //generateCode((PExpressionIdentifierReference)statement, scopeLevel, enclosingBlockStrBuilder);
      }
      if(statement instanceof PVariableDeclaration)
      {
          generateCode((PVariableDeclaration)statement, scopeLevel, enclosingBlockStrBuilder);
      }

    }
    /*****************  FOR STATEMENT ***************/
    private static void generateCode(PStatementForStatement forExpression, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
        
    }
    
/****************************** PEXPRESSION ***********************************/
    private static void generateCode(final PExpression expression, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
        //interface:
        //PExpressionAtom

        //part of the above interface as well:
        //PExpressionBinOp
        //PExpressionIdentifierReference
        //PExpressionVariable
        //PStatementFunctionCall

        //other:
        //PExpressionStub
        
        if(expression instanceof PExpressionAtom)
        {
            generateCode((PExpressionAtom)expression, scopeLevel, enclosingBlockStrBuilder);
        }
        // if(expression instanceof PExpressionBinOp)
        // {
            // generateCode((PExpressionBinOp)expression, scopeLevel, enclosingBlockStrBuilder);
        // }
        // if(expression instanceof PExpressionIdentifierReference)
        // {
            // generateCode((PExpressionIdentifierReference)expression, scopeLevel, enclosingBlockStrBuilder);
        // }
        // if(expression instanceof PExpressionVariable)
        // {
            // generateCode((PExpressionVariable)expression, scopeLevel, enclosingBlockStrBuilder);
        // }
        // if(expression instanceof PStatementFunctionCall)
        // {
            // generateCode((PStatementFunctionCall)expression, scopeLevel, enclosingBlockStrBuilder);
        // }
        //else don't handle, including PExpressionStub
      
    }
    /****** PExpressionAtom *****/
    private static void generateCode(final PExpressionAtom atomicExpression, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
        //Forward to appropriate Atomic Expression

        //PExpressionAtomBooleanLiteral
        //PExpressionAtomNullLiteral
        
        //PExpressionAtomNumberLiteral
        //PExpressionAtomObjectConstruction
        //PExpressionAtomStringLiteral
        //PIdentifierReference
    
        /*(!) There is no need for so many expression types. 
            Should have just had a class w/ Type member, and  a method to give you the literal as a string.
            Then these little expressions just inherit from that, and they just use the values we want.
        */
        if(atomicExpression instanceof PExpressionAtomBooleanLiteral)
        {
            generateCode((PExpressionAtomBooleanLiteral)atomicExpression, scopeLevel, enclosingBlockStrBuilder);
        }
        if(atomicExpression instanceof PExpressionAtomNullLiteral)
        {
            generateCode((PExpressionAtomNullLiteral)atomicExpression, scopeLevel, enclosingBlockStrBuilder);
        }
        if(atomicExpression instanceof PExpressionAtomNumberLiteral)
        {
            generateCode((PExpressionAtomNumberLiteral)atomicExpression, scopeLevel, enclosingBlockStrBuilder);
        }
        if(atomicExpression instanceof PExpressionAtomStringLiteral)
        {
            generateCode((PExpressionAtomStringLiteral)atomicExpression, scopeLevel, enclosingBlockStrBuilder);
        }
        // if(atomicExpression instanceof PExpressionAtomObjectConstruction)
        // {
            // generateCode((PExpressionAtomObjectConstruction)atomicExpression, scopeLevel, enclosingBlockStrBuilder);
        // }
        // if(atomicExpression instanceof PIdentifierReference)
        // {
            // generateCode((PIdentifierReference)atomicExpression, scopeLevel, enclosingBlockStrBuilder);
        // }
    } //end PAtomic Expression Forwarder
    
    private static void generateCode(final PExpressionAtomBooleanLiteral booleanLiteral, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
        String booleanValue = (booleanLiteral.literalToken.getType() == Token.TokenType.KEYWORD_TRUE) ? "true" : "false";
        enclosingBlockStrBuilder.append(booleanValue);
    }
    private static void generateCode(final PExpressionAtomNullLiteral nullLiteral, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
        enclosingBlockStrBuilder.append("NULL");
    }
    private static void generateCode(final PExpressionAtomNumberLiteral numberLiteral, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
        enclosingBlockStrBuilder.append(numberLiteral.literalToken.getTokenString());
    }
    
    private static void generateCode(final PExpressionAtomStringLiteral stringLiteral, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
        enclosingBlockStrBuilder.append(stringLiteral.literalToken.getTokenString());//"\""+stringLiteral.literalToken.getTokenString()+"\"");
    }
/***************** Helper Functions ******************************/
    private static String typeTranslatorRough(PVariableDeclaration variableDeclaration)//Token.TokenType typeEnum) //typeStringArg
    {
        Token.TokenType typeThisVar = variableDeclaration.variableType.getType();
        
        String typeStr = null;
        /*Can't rely on a clear way of getting type directly from variableDeclaration:
          variableDeclaration.variableType.getTokenString() or variableDeclaration.variableType.getType()????
          No need to still be using tokens this far in the compiler...
        */
        // if(typeThisVar == "AUTO")
        // {
            // Type.TokenType typeTemp = TypeChecker.getType(variableDeclaration, scopeLevel);
            // switch(typeTemp)
            // {
                // //del
                // /*below cases in enum format. 
                  // No one shouldn't have to look at where the typechecker gets the type of this
                  // poorly structure PObject.
                // */
                // default:
                  // break;
            // }
        // }
        //Determine the type translation
        if(typeThisVar == Token.TokenType.KEYWORD_STRING)
        {
            typeStr = "char *";
        }
        if(typeThisVar == Token.TokenType.KEYWORD_BOOLEAN)
        {
            //Check if bool already exists in the global space.
            if(! (checkHeaderFiles("stdbool")))
            {
                //Add the header file.
                //addHeaderFile("stdbool");
                programHeader.append("#include <stdbool.h>\n");
            }
            typeStr = "bool";
        }
        if(typeThisVar == Token.TokenType.IDENTIFIER)
        {
            //Assume it's a reference type to a struct
            typeStr = variableDeclaration.variableType.getTokenString()+"Struct";
        }
            
        typeStr = (typeStr == null)? variableDeclaration.variableType.getTokenString() : typeStr;
        
        //End determining type of var (declaration).
        
        return typeStr;
    }
    //(!)Cannot handle identifier types
    // private static String typeTranslatorRough(Token.TokenType typeEnum) //typeStringArg
    // {
        // Token.TokenType typeThisVar = typeEnum; //variableDeclaration.variableType.getType();
        
        // String typeStr = null;
        // /*Can't rely on a clear way of getting type directly from variableDeclaration:
          // variableDeclaration.variableType.getTokenString() or variableDeclaration.variableType.getType()????
          // No need to still be using tokens this far in the compiler...
        // */
        // // if(typeThisVar == "AUTO")
        // // {
            // // Type.TokenType typeTemp = TypeChecker.getType(variableDeclaration, scopeLevel);
            // // switch(typeTemp)
            // // {
                // // //del
                // // /*below cases in enum format. 
                  // // No one shouldn't have to look at where the typechecker gets the type of this
                  // // poorly structure PObject.
                // // */
                // // default:
                  // // break;
            // // }
        // // }
        // //Determine the type translation
        // if(typeThisVar == Token.TokenType.KEYWORD_STRING)
        // {
            // typeStr = "char *";
        // }
        // if(typeThisVar == Token.TokenType.KEYWORD_BOOLEAN)
        // {
            // //Check if bool already exists in the global space.
            // if(! (checkHeaderFiles("stdbool")))
            // {
                // //Add the header file.
                // //addHeaderFile("stdbool");
                // programHeader.append("#include <stdbool.h>\n");
            // }
            // typeStr = "bool";
        // }
        // //(!)Cannot handle identifier types
        // // if(typeThisVar == Token.TokenType.IDENTIFIER)
        // // {
            // // //Assume it's a reference type to a struct
            // // typeStr = variableDeclaration.identifier.getTokenString()+"Struct";
        // // }
            
        // typeStr = (typeStr == null)? "NO_TYPE" : typeStr;
        
        // //End determining type of var (declaration).
        
        // return typeStr;
    // }//using TokenType.
    
    //(!)Cannot handle identifier types
    // private static String typeTranslatorRough(String typeStringArg)
    // {
        // String typeThisVar = typeStringArg;
        // typeThisVar = typeThisVar.trim().toUpperCase();
        // String typeStr = null;
        // /*Can't rely on a clear way of getting type directly from variableDeclaration:
          // variableDeclaration.variableType.getTokenString() or variableDeclaration.variableType.getType()????
          // No need to still be using tokens this far in the compiler...
        // */
        // // if(typeThisVar == "AUTO")
        // // {
            // // Type.TokenType typeTemp = TypeChecker.getType(variableDeclaration, scopeLevel);
            // // switch(typeTemp)
            // // {
                // // //del
                // // /*below cases in enum format. 
                  // // No one shouldn't have to look at where the typechecker gets the type of this
                  // // poorly structure PObject.
                // // */
                // // default:
                  // // break;
            // // }
        // // }
        // //Determine the type translation
        // if(typeThisVar.contentEquals("STRING"))
        // {
            // typeStr = "char *";
        // }
        // if(typeThisVar.contentEquals("BOOLEAN"))
        // {
            // //Check if bool already exists in the global space.
            // if(! (checkHeaderFiles("stdbool")))
            // {
                // //Add the header file.
                // //addHeaderFile("stdbool");
                // programHeader.append("#include <stdbool.h>\n");
            // }
            // typeStr = "bool";
        // }
        // typeStr = (typeStr == null)? typeStringArg : typeStr;
        
        // //End determining type of var (declaration).
        
        // return typeStr;
    // }

    private static void addTabs(StringBuilder strB, final int scopeLevel)
    {
        for(int i = 0; i < scopeLevel; i++)
        {
            strB.append("\t");
        }
    }//addTabs
    private static boolean checkHeaderFiles(final CharSequence headerName)
    {
        return programHeader.toString().contains(headerName); 
        //can also try programHeader.toString().matches(String regex);
    }

/************** INIT STRUCTS *************/    
    //(!) This function should be placed and work w/ the assignment function. It only executes if the assignment is null.
    private static void generateCodeInitializeStructs(final PVariableDeclaration variableDeclaration, final int scopeLevel, StringBuilder enclosingBlockStrBuilder)
    {
      
        //What I'm aiming for: https://stackoverflow.com/questions/13706809/structs-in-c-with-initial-values
        PClassDeclaration currentStruct = null;
        
        StringBuilder tempStructInitializerBuilder = new StringBuilder();
        //Search for and retrieve the correct PClassDeclaration.
              // // getStructDefinition(variableDeclaration.identifier.getTokenString());
        String structName = variableDeclaration.variableType.getTokenString(); //typeTranslatorRough(variableDeclaration);//variableDeclaration.variableType.getTokenString()+"Struct";
        // //Remove the "Struct" in name.
        // structName.remove("Struct");
            //Go through the class declarations.
        for(PClassDeclaration classDeclaration : inputProgram.classDeclarationList)
        {   
            //if struct found, save it.
            if(classDeclaration.identifier.getTokenString().equalsIgnoreCase(structName))
            {
                currentStruct = classDeclaration;
            }
        }
        
        //Make sure we found the struct we were looking for.
        if(currentStruct != null)
        {
            //Go through the members & if they have an initialization, we add it.
            for(PDeclaration pDeclaration : currentStruct.declarationList)
            {   
                if(pDeclaration instanceof PVariableDeclaration)
                {   
                      PVariableDeclaration temp = (PVariableDeclaration) pDeclaration;
                      //Make sure it has an assignment before adding it.
                      if(temp.assignment != null)
                      {
                          if(tempStructInitializerBuilder.length() == 0) //makes sure that we first add a "{"
                          {
                              tempStructInitializerBuilder.append("{ ");
                          }
                          else //this is not the first definition.
                          {
                              tempStructInitializerBuilder.append(", ");
                          }
                          tempStructInitializerBuilder.append("."+ temp.identifier.getTokenString());
                          tempStructInitializerBuilder.append(" = ");
                          generateCode(temp.assignment, scopeLevel+1, tempStructInitializerBuilder); //basically a parameter declaration.
                        //generateCode((PVariableDeclaration)pDeclaration, scopeLevel+1, structStrBuilder, true);
                      }
                }
                if(pDeclaration instanceof PStatementFunctionDeclaration)
                {
                      
                      PStatementFunctionDeclaration temp = (PStatementFunctionDeclaration) pDeclaration;
                      
                      //Main should not be a member.
                      if(! temp.identifier.getTokenString().equalsIgnoreCase("MAIN"))
                      {
                          if(tempStructInitializerBuilder.length() == 0) //makes sure that we first add a "{"
                          {
                              tempStructInitializerBuilder.append("{ ");
                          }
                          else //this is not the first member initialization.
                          {
                              tempStructInitializerBuilder.append(", ");
                          }
                          tempStructInitializerBuilder.append("."+temp.identifier.getTokenString());
                          tempStructInitializerBuilder.append(" = "+temp.identifier.getTokenString());
                      }
                }//end check type of PDeclaration.
            }//end adding struct member declarations.
        }//check if member was found.
        
        if(tempStructInitializerBuilder.length() > 0) //makes sure that we first add a "{"
        {
            tempStructInitializerBuilder.append(" }");
        }
        
        //tempStructInitializerBuilder.append(";\n");
        enclosingBlockStrBuilder.append(tempStructInitializerBuilder.toString());
    }
/************************************COMPILER & PROGRAM EXECUTION SECTION ********************************************************/
    //(!) Will make accept a PProgram & will return String, for now just prints.
    public void generateCode(PStatementPrintln printLnExpression) throws IOException 
    {
        try {
            //Add / append the program content until it's all processed.
            //Can be in a loop if needed.
            this.programContentStrBuilder.insert(0, "\t");
            //_(!)programContentStrBuilder.insert(0, getIndents(printLnExpression.getScope()));
          /*Actually, since we are diving into the code linearly, the scope will be directly known by
            this program.
          */

            //Don't have any info on scope, I believe, so don't know how many \t to prepend.
            this.programContentStrBuilder.append(printLnExpression.generateString());


            //Add the necessary header files.
            // if(printLnExpression.includeRequired())
            // {
            // if( ! (includeExists(programHeader, printLnExpression.includeRequired())))
            // programHeader.append(printLnExpression.includeRequired());
            // }

            //System.out.println(printLnExpression.generateString());
            //System.out.println("Class: "+fooTester2.getClass());

        } 
        catch (CodeGenException e) 
        {
            e.printStackTrace();
        }

        //Append the program headers
        finalOutputProgramStrBuilder.append(programHeader.toString());
        //Append structs to the program
        finalOutputProgramStrBuilder.append(structSectionStrBuilder.toString());
        
        //Append any functions.
        finalOutputProgramStrBuilder.append(functionSectionStrBuilder.toString());
        //Append the main start
        finalOutputProgramStrBuilder.append(MAIN_START); //not a stringBuilder.

        //Append the program content to the programHeader at this point.
        finalOutputProgramStrBuilder.append(programContentStrBuilder.toString());
        //Separate the program content from the return statement. 
        //_ Can be added in programContentStrBuilder building? (?)
        finalOutputProgramStrBuilder.append("\n");

        //Append the program Footer to the finalOutputProgramStrBuilder.
        finalOutputProgramStrBuilder.append(MAIN_END);

        System.out.println(finalOutputProgramStrBuilder);

        //(!) Stopped copying to other method(PProgram ) from here.
        //Send the program to be written to a file in the current dir.
        String tempfilename = "HelloWorld.c";


        //will make better solution later XXXXXXX
        //this converts the string to an arraylist of strings, for writing purposes
        ArrayList<String> progLines = StringtoArrayList(finalOutputProgramStrBuilder.toString());

        writeCompleteFile(progLines, new File(""), tempfilename); //write output to a file

    }//end generateCode

    public void writeCompleteFile(final ArrayList<String> programString, final File dir, final String fileZname) throws IOException {
        //dir is the directory of the file including a slash at the end
        //filename is the name of the file plus extension

        File fileout;
        System.out.println();
        if (dir.length() != 0) { //if dir is not blank
            System.out.println("dir not blank");
            if (!dir.exists()) { //if dir doesnt exist
                System.out.println("creating dir");
                dir.mkdir(); //create directory
            } else {
                System.out.println("dir exists");
            }
            fileout = new File(dir, fileZname); //create file out of name + dir
        } else {
            System.out.println("dir is blank");
            fileout = new File(fileZname); //create file from name
        }

        fileout.createNewFile();
        FileWriter filewrite = new FileWriter(fileout);

        try {
            for (int i = 0; i < programString.size(); i++) { //for every newline
                filewrite.write(programString.get(i)); //write the line
                filewrite.write(System.getProperty("line.separator")); //write a newline char
                filewrite.flush();
            }
            System.out.println("file has been outputed");
        } catch (Exception e) {
            System.err.println(e);
            //throw error?
        }

        COMPILER(fileout); //////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    } //end writeCompleteFile

    public static ArrayList<String> StringtoArrayList(String input) { //input a string, output will be an arraylist separating all new lines in the string
        ArrayList<String> out = new ArrayList<>();
        String output[] = input.split("\n"); //split by character

        for(String temp : output) {
            out.add(temp);
        }
        return out;
    }

    public static void COMPILER(File file) throws IOException {
        final String loc = ""; //location of our project on your computer !!_NOTICE_!!
        String UPGRADEloc = loc + "tcc"; //where tcc compiler is in our proj folder
        String fileloc;
        String dothis = "tcc "; //command to compile the file, NOT execute, must be done separately
        final String COMMAND_1;
        System.out.println();
        System.out.println(file.getPath());
        System.out.println(file.getName());
        if (file.getPath().equals(file.getName())) { //location of the file is in root dir?
            fileloc = loc; //save the location of the file if not specified (proj dir by default)
        } else {
            fileloc = file.getPath();
        }
        dothis += fileloc + "..\\" + file.getName();
        Runtime rt = Runtime.getRuntime();
        //COMMAND_1 = "cmd.exe /c cd \"" + UPGRADEloc + "\" & start cmd.exe /k \"" + dothis + "";
        //COMMAND_1 = "cmd.exe /k print hello";
        COMMAND_1 = "tcc\\tcc.exe HelloWorld.c";
        Process PROCESS_1 = rt.exec(COMMAND_1); //execute the tcc compiler and compile the given file
        try {
            PROCESS_1.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //NOTE: output file is currently set to default location with tcc.exe XXXXXXX
        //NOTE: currently output file is not executed or read XXXXXX
        RUNFILE(PROCESS_1, UPGRADEloc, file);
    } // end COMPILER()

    public static void RUNFILE(Process inputProcess, String UPGRADEloc, File file) throws IOException {
        String[] split = file.getName().split("\\.");
        System.out.println(split[0]);
        String two = split[0] + ".exe"; // + "\" & start cmd.exe /k \"" + dothis + ""
        System.out.println(two);
        //String three = "cmd.exe /c cd \"" + UPGRADEloc + "\" & start cmd.exe /c \"" + two;
        String three = "HelloWorld.exe";
        Runtime rt = Runtime.getRuntime();
        Process newprocess = rt.exec(three);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(newprocess.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(newprocess.getErrorStream()));

// read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

// read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }
}