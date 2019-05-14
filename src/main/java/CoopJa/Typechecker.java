package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Typechecker {

    public static HashMap<String, Storage> ClassListAll = new HashMap(); //holds (Class Name, Storage Object (holds ArrayList<String> of names of Variables and Methods for the Class)
    public static String ClassString = ""; //keeps name of the currently typechecking class, used to find this class's Storage object from the ClassListAll var
    public static String MethodString = "";
    public static ArrayList<String[]> AutoHandler = new ArrayList<>();
    public static Storage FunctionCallParameterScope; //global for the special case where a recursive function call needs to refer to parameters from its original scope

    public static void main(String[] args) throws Exception {

//        String foo = "public class foo{public int foo4 = 0;}" + //example string to be parsed
//                "public class foo6 extends foo{public int foo4 = 1;}" +
//                "public class foo2{" +
//                //"public int foo3 = 0;" + //duplicate var able to be detected, not inside methods yet
//                "public int foo3 = 0;" +
//                "public int main(){" +
//                "foo.foo4(); " +
//                "foo3 = (1 + 9)*5;" +
//                "for (int i = 0; i < 9; i = i+1;){" +
//                "foo = foo + 5;" +
//                "}" +
//                "if (1 == 1){" +
//                "int i = 0;" +
//                "}" +
//                "else{" +
//                "int i = 1;" +
//                "}" +
//                "int i = 2;" +
//                "return;" +
//                "}" +
//                "}";

//        String foo = "public class example {" +
//                "public string cool = \"Cool1\";" +
//                "public void method1(int one, int two) {" +
//                "int three = 1;" +
//                "}" +
//                "}";

//        String foo = "public class one {" +
//                "int test = 0;" +
//                "public void main() {" +
//                "}" +
//                "}" +
//                "public class two extends one {" +
//                "public void main(int test1) {" +
//                //"int test = 0;" + //not detecting inside stuff
//                "}" +
//                "}";

//        String foo = "public class one {" +
//                "public void main(int one, int one) {" + //handled
//                "" +
//                "}";

//        String foo = "public class one {" +
//                "int foo1 = 9<90;" + //PExpressionBinOp //boolean
//                "int foo2 = foo2.oo();" + //PExpressionAtom --?-- PIdentifierReference //null
//                "int foo3 = foo2;" + //PExpressionVariable //identifier
//                "int foo4 = foo();" + //PStatementFunctionCall //null
//                "int foo5 = 1;" + //PExpressionAtom --m --- PExpressionAtomNumberLiteral //int
//                "int foo6 = \"uuu\";" + //PExpressionAtom --m -- PExpressionAtomStringLiteral //string
//                "public void main(int one) {" +
//                "" +
//                "}";

//        String foo = "public class one {" +
//                "String foo1 = null;" + //PExpressionAtomNullLiteral //null
//                "boolean foo2 = true;" + //PExpressionAtomBooleanLiteral //boolean
//                //"Object foo3 = new Object();" +
//                "public void main(int one) {" +
//                "" +
//                "}";

        String foo = "public class one {" +
                "int foo1 = foo;" +
                "public void main(int one) {" +
                "" +
                "}";


        ArrayList<Token> tokenList = Token.tokenize(foo); //tokenize example string
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser(); //create MainParser object
        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
        System.out.println();

        TypecheckMain(fooTester); //call typechecker with parsed program obj

    } //end Main()

    public static void TypecheckMain(PProgram fooTester) throws Exception { //typechecker
        ArrayList<PClassDeclaration> classlist = new ArrayList<PClassDeclaration>(1);

        for (int i = 0; i < fooTester.classDeclarationList.size(); i++) { //load classes into above ArrayList
            //Map classes to their storage.
            classlist.add(i, fooTester.classDeclarationList.get(i));
            ClassListAll.put(fooTester.classDeclarationList.get(i).identifier.getTokenString(), new Storage());
            PClassDeclaration tempClass = classlist.get(i); //assign first class to a temp var
            int x = i + 1; //used for printing
            System.out.println("Current Class: #" + x);

            String extendscheck = ClassTypecheck(tempClass); //typecheck the current class declaration

            if (extendscheck != null) { //class extends another that exists, grab info
                Storage extendsObj = ClassListAll.get(extendscheck); //get parent obj
                Storage t_S = ClassListAll.get(ClassString); //current class
                t_S.extendsClass = extendsObj; //store parent in child
                ClassListAll.put(ClassString, t_S); //store back new info
            }

            ArrayList<PDeclaration> tempDeclar = tempClass.declarationList; //pull out the declarations for this current class
            System.out.println("Class #" + x + " Declarations Amount: " + tempDeclar.size()); //how many declaration stmts are there?

            System.out.println("Declarations Begin: ");

            for (int j = 0; j < tempDeclar.size(); j++) { //for each declaration, either it is a PVariableDeclaration or a PStatementFunctionDeclaration

                int y = j + 1; //used for printing

                if (tempDeclar.get(j) instanceof PVariableDeclaration) { //handle class variable declarations
                    System.out.println("Declaration #" + y + " is instance of PVariableDeclaration");
                    PVariableDeclaration tempVar = (PVariableDeclaration) tempDeclar.get(j); //cast the PDeclaration obj into its proper form in a temp var

                    Storage t_S = ClassListAll.get(ClassString); //pull out Storage obj of the current class
                    HashMap<String, VarStor> t_VS = t_S.VariableNames; //pull the vars out of the Storage object
                    HashMap<String, VarStor> t_NEWVars; //holds new info after VDT call
                    //returns a map entry for this variable if it passes the typecheck.
                    //class storage is passed since we need to have the scope to check expressions. Might as well not pass t_VS.
                    t_NEWVars = VariableDeclarationTypecheck(t_S, tempVar, true); //call VDT with this list of vars (Scope) and get new info
                    t_VS.putAll(t_NEWVars); //add new info to old map
                    t_S.VariableNames = t_VS; //replace Storage object var list with updated copy
                    ClassListAll.put(ClassString, t_S); //replace the old Storage obj by adding it back to class hashmap with class string

                }

                if (tempDeclar.get(j) instanceof PStatementFunctionDeclaration) { //for each method declaration
                    System.out.println("Declaration #" + y + " is instance of PStatementFunctionDeclaration");
                    PStatementFunctionDeclaration tempFunc = (PStatementFunctionDeclaration) tempDeclar.get(j); //cast PDeclaration object to its proper type

                    Storage tempSendClassStor = ClassListAll.get(ClassString); //retrieve current class's Storage object
                    Storage replaceClassStor = MethodDeclarationTypecheck(tempSendClassStor, tempFunc); //call MDT, send it current class's Stor obj & the PStatementFunctionDeclaration obj, returns an updated Stor obj after typechecking the method
                    ClassListAll.put(ClassString, replaceClassStor); //update the class's stor obj with method info
                }
                System.out.println("End Declaration #" + y);
                System.out.println();
            }

            System.out.println("End of Class #" + x);
            System.out.println();
        }

        System.out.println("one typecheck done");

        //extra call to Expression and Statement Typechecker
        //this can be thought of as a SECOND typechecker that covers Expressions and statements
        //--ExpressionTypeChecker cTypeChkr = new ExpressionTypeChecker(fooTester);
        //--cTypeChkr.typeCheck();
    }

    //idea, recursive methodology
    public static Token.TokenType getType(PExpression exp) throws TypeCheckerException {
        if (exp instanceof PExpressionAtomNumberLiteral)
            return Token.TokenType.KEYWORD_INT; //Expand here once we have more than just ints
        if (exp instanceof PExpressionAtomStringLiteral)
            return Token.TokenType.KEYWORD_STRING;
        if (exp instanceof PExpressionAtomBooleanLiteral)
            return Token.TokenType.KEYWORD_BOOLEAN; //technically not the "boolean" keyword, but lets use this for now
//        if (exp instanceof PExpressionVariable) { //changed XXXXXXXXX CHANGE TO CHECK VARIABLES IN SCOPE XXXXXXXXwont call if this is the caseXXXXXXXXXXXXXXXZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ~~~~~
//            return ((PExpressionVariable) exp).variable.getType();
//        }
        if (exp instanceof PExpressionBinOp) {
            //recursivly do both hands of the expressions
            Token.TokenType lhs = getType(((PExpressionBinOp) exp).lhs);

            Token.TokenType rhs = getType(((PExpressionBinOp) exp).rhs);

            if (lhs != rhs) //The operand types do not match.
            {
                //Allows string & integer concatenation
                if ((lhs == Token.TokenType.KEYWORD_STRING && rhs == Token.TokenType.KEYWORD_INT) ||
                        (lhs == Token.TokenType.KEYWORD_INT && rhs == Token.TokenType.KEYWORD_STRING))
                    return Token.TokenType.KEYWORD_STRING; //concatinating an integer to a string
                else //anything else must fail
                    throw new TypeCheckerException("TypeCheck Error: Expected " +
                            lhs.name() + " got " + rhs.name());
            }

            Token.TokenType output = lhs;//at this point we already determined lhs and rhs are the same type
            //check if the operator is the right type for the expression
            Token.TokenType operator = ((PExpressionBinOp) exp).operatorToken.getType();
            //System.out.println("\t"+rhs.name()+"_"+operator.name()+"_"+lhs.name());
            /********** + *************/
            if (operator == Token.TokenType.SYMBOL_PLUS) //plus operator works w/ ints and strings.
            {

                if ((output != Token.TokenType.KEYWORD_STRING) && (output != Token.TokenType.KEYWORD_INT)) {
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
                }
            }/********** numeric ops *************/
            else if (operator == Token.TokenType.SYMBOL_MINUS || //number operations
                    operator == Token.TokenType.SYMBOL_ASTERISK ||
                    operator == Token.TokenType.SYMBOL_SLASH ||
                    /****** BITWISE OPS ******/
                    operator == Token.TokenType.SYMBOL_SHIFTRIGHT ||
                    operator == Token.TokenType.SYMBOL_SHIFTLEFT ||
                    operator == Token.TokenType.SYMBOL_AMPERSAND ||
                    operator == Token.TokenType.SYMBOL_BAR ||
                    operator == Token.TokenType.SYMBOL_CARET ||
                    operator == Token.TokenType.SYMBOL_TILDE) {
                if (output != Token.TokenType.KEYWORD_INT) /////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
            }/********* DOUBLE EQUALS *******/
            else if ((operator == Token.TokenType.SYMBOL_DOUBLEEQUALS) || //plus operator works w/ ints and strings.
                    (operator == Token.TokenType.SYMBOL_NOTEQUAL)) {   /*Add more valid operand types for double equals if necessary.*/
                if ((output != Token.TokenType.KEYWORD_INT) &&
                        (output != Token.TokenType.KEYWORD_BOOLEAN)) {
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type: Expected type" +
                            /*Token.TokenType.KEYWORD_INT.name().split("_")[0] + */
                            output.name());
                }
                output = Token.TokenType.KEYWORD_BOOLEAN;
            }/********** Boolean ops ***********/
            else if (operator == Token.TokenType.SYMBOL_DOUBLEAMPERSAND ||
                    operator == Token.TokenType.SYMBOL_DOUBLEBAR) {
                if (output != Token.TokenType.KEYWORD_BOOLEAN) //at this point we already determined lhs and rhs are the same type
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
            }/************ numeric equality ops ***********/
            else if (operator == Token.TokenType.SYMBOL_GREATERTHAN ||
                    operator == Token.TokenType.SYMBOL_GREATERTHANEQUAL ||
                    operator == Token.TokenType.SYMBOL_LESSTHAN ||
                    operator == Token.TokenType.SYMBOL_LESSTHANEQUAL) {
                if (output != Token.TokenType.KEYWORD_INT) //in these cases the lhs rhs are ints and the output is boolean
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
                output = Token.TokenType.KEYWORD_BOOLEAN;
            }/*****     *****/
            //else if( operator ==
            //if the two sides match just return the type of one of the sides
            return output;
        }
        return null;
    }

    public static Token.TokenType PExpressionChecker(PExpression input, Storage varMap) throws TypeCheckerException {
        if (input instanceof PExpressionBinOp) {
            System.out.println("PExpressionBinOp");
            return getType(input);
        } else if (input instanceof PExpressionIdentifierReference) {
            System.out.println("!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\nPExpressionIdentifierReference");
            //unused
            throw new TypeCheckerException("TypeCheck Error: Never reached / Undefined behavior");
        } else if (input instanceof PExpressionVariable) {
            System.out.println("PExpressionVariable");
            String varName = ((PExpressionVariable) input).variable.getTokenString(); //PExpressionVariable name
            if (varMap.VariableNames.containsKey(varName)) { //if var is in class
                VarStor tempVarCheck = varMap.VariableNames.get(varName);
                return tempVarCheck.Type.getType();
            } else { //check parent if any
                if (varMap.extendsClass != null) { //if there is a parent class
                    if (varMap.extendsClass.VariableNames.containsKey(varName)) { //yes in parent
                        VarStor tempVarCheck = varMap.extendsClass.VariableNames.get(varName);
                        if (tempVarCheck.AccessModifier.getType() == Token.TokenType.KEYWORD_PRIVATE) { //if parent is private
                            throw new TypeCheckerException("TypeCheck Error: Parent var has PRIVATE access");
                        } else {
                            return tempVarCheck.Type.getType();
                        }
                    } else { //not in parent
                        throw new TypeCheckerException("TypeCheck Error: No var exists in Parent or Child");
                    }
                } else { //no parent class
                    throw new TypeCheckerException("TypeCheck Error: No var exists");
                }
            }
        }
        return null;
    }

    public static void typeCheckVariableDec(PVariableDeclaration varDec, Storage varMap) throws TypeCheckerException {
        System.out.println("Checking Variable Declaration Body");

        Token.TokenType varType = varDec.variableType.getType();
        Token.TokenType assType; //good comment

        //boolean autoPossible = false; //true if right side could be auto

        assType = PExpressionChecker(varDec.assignment, varMap);

        if (varDec.assignment instanceof PExpressionBinOp) {
            System.out.println("PExpressionBinOp");
            if (varType == assType) {
                System.out.println("Variable Declaration Succeeded");
            } else {
                if (varType == Token.TokenType.KEYWORD_AUTO) {
                    System.out.println("AUTO var type detected!");
                    //change auto type
                    String[] auto = {ClassString, "Variable", MethodString, varDec.identifier.getTokenString(), assType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                    AutoHandler.add(auto);
                } else { //type mismatch
                    throw new TypeCheckerException("TypeCheck Error: Type mismatch");
                }
            }
        } else if (varDec.assignment instanceof PExpressionIdentifierReference) {
            System.out.println("!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\n!!!!\nPExpressionIdentifierReference");
            //unused
            throw new TypeCheckerException("TypeCheck Error: Never reached / Undefined behavior");
        } else if (varDec.assignment instanceof PExpressionVariable) {
            System.out.println("PExpressionVariable");
            String varName = ((PExpressionVariable) varDec.assignment).variable.getTokenString(); //PExpressionVariable name
            //we only know the return type of PExpressionChecker(), not where the var was found (parent or child), so if PExpressionChecker() didnt fail, the var must be in one of them
            if (varMap.VariableNames.containsKey(varName)) { //if var is in current class vars
                if (varType == Token.TokenType.KEYWORD_AUTO && assType != Token.TokenType.KEYWORD_AUTO) { //if left is auto
                    System.out.println("AUTO var type detected!");
                    //change auto type
                    String[] auto = {ClassString, "Variable", MethodString, varDec.identifier.getTokenString(), assType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                    AutoHandler.add(auto);
                } else if (assType == Token.TokenType.KEYWORD_AUTO && varType != Token.TokenType.KEYWORD_AUTO) { //right side is auto
                    System.out.println("Assignment Var has AUTO type!");
                    //change auto type of assignment var
                    String[] auto = {ClassString, "Variable", MethodString, varName, varType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                    AutoHandler.add(auto);
                } else if (assType == varType && assType == Token.TokenType.KEYWORD_AUTO) { //both auto
                    throw new TypeCheckerException("TypeCheck Error: AUTO Var cannot be assigned to another AUTO Var");
                } else {
                    throw new TypeCheckerException("TypeCheck Error: Type mismatch");
                }
            } else { //else it was in the parent
                if (varType == Token.TokenType.KEYWORD_AUTO && assType != Token.TokenType.KEYWORD_AUTO) { //if left is auto
                    System.out.println("AUTO var type detected!");
                    //change auto type
                    String[] auto = {ClassString, "Parent", "Variable", MethodString, varDec.identifier.getTokenString(), assType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                    AutoHandler.add(auto);
                } else if (assType == Token.TokenType.KEYWORD_AUTO && varType != Token.TokenType.KEYWORD_AUTO) { //right side is auto
                    System.out.println("Assignment Var has AUTO type!");
                    //change auto type of assignment var
                    String[] auto = {ClassString, "Parent", "Variable", MethodString, varName, varType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                    AutoHandler.add(auto);
                } else if (assType == varType && assType == Token.TokenType.KEYWORD_AUTO) { //both auto
                    throw new TypeCheckerException("TypeCheck Error: AUTO Var cannot be assigned to another AUTO Var");
                } else {
                    throw new TypeCheckerException("TypeCheck Error: Type mismatch");
                }
            } ///FIXED TO THIS POINT
        } else if (varDec.assignment instanceof PStatementFunctionCall) {
            System.out.println("PStatementFunctionCall");
            PStatementFunctionCall STMT = (PStatementFunctionCall) varDec.assignment;
            String functName = STMT.identifier.getTokenString();
            int functParamSize = STMT.expressionsInput.size();
            if (varMap.MethodNames.containsKey(functName)) { //if funct exists
                FunctStor tempFunct = varMap.MethodNames.get(functName);
                if (functParamSize == tempFunct.Parameters.size()) { //if param list size matches
                    System.out.println("Params Amount Match");
                    for (int i = 0; i < functParamSize; i++) { //for all params
                        PExpression temp1 = STMT.expressionsInput.get(i); //param in funct call
                        VarStor temp2 = tempFunct.Parameters.get(i); //params list already defined
                        Token.TokenType tempT2 = temp2.Type.getType(); //type of param defined
                        //XXXXXXXXX could do it better, obscure to method, but cant think of a clean way to do right now, since different things are needed for each time referenced
                        if (temp1 instanceof PExpressionBinOp) {
                            Token.TokenType tempT1 = getType(temp1); //param in funct call type
                            if (tempT1 == tempT2) { //if equal, good
                                //match
                            } else { //if not equal, why?
                                if (tempT2 == Token.TokenType.KEYWORD_AUTO) { //if pre defined param had auto type, change
                                    System.out.println("Param " + i + " Var has AUTO type!");
                                    //change auto type of assignment var
                                    String[] auto = {ClassString, "Parameter", MethodString, "" + i, tempT1.toString()}; //in class name, in parent, type (variable/method), in method name (could be empty if in class, var name, type to change to
                                    AutoHandler.add(auto);
                                } else if (tempT1 == Token.TokenType.KEYWORD_AUTO) {
                                    //var being passed into funct has an auto type, but we now know the type
                                    //except, this case will not occur since we know its an instance of PExpressionBinOp
                                } else {
                                    throw new TypeCheckerException("TypeCheck Error: Param Types do not match");
                                }
                            }
                        } else if (temp1 instanceof PExpressionIdentifierReference) {
                            //do nothing since not exist
                            throw new TypeCheckerException("TypeCheck Error: Never reached / Undefined behavior");
                        } else if (temp1 instanceof PExpressionVariable) {
                            String varName = ((PExpressionVariable) temp1).variable.getTokenString(); //param name of var
                            if (varMap.VariableNames.containsKey(varName)) { //var exists in current class
                                VarStor tempVarCheck = varMap.VariableNames.get(varName);
                                assType = tempVarCheck.Type.getType();
                                //autoPossible = true; //mostly for reference
                                if (tempT2 == assType) {
                                    //good
                                } else { //not match
                                    if (tempT2 == Token.TokenType.KEYWORD_AUTO) { //funct param declared with auto type
                                        System.out.println("Param " + i + " Var has AUTO type!");
                                        //change auto type of assignment var
                                        String[] auto = {ClassString, "Parameter", MethodString, "" + i, assType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                                        AutoHandler.add(auto);
                                    } else if (assType == Token.TokenType.KEYWORD_AUTO) { //var being passed into method as param has an auto type, fix
                                        System.out.println("Param has AUTO type!");
                                        //change auto type of assignment var
                                        String[] auto = {ClassString, "Variable", MethodString, varName, tempT2.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                                        AutoHandler.add(auto);
                                    }
                                }
                            } else { //check parent
                                if (varMap.extendsClass != null) { //if extends
                                    if (varMap.extendsClass.VariableNames.containsKey(varName)) {
                                        VarStor tempVarCheck = varMap.extendsClass.VariableNames.get(varName); //param var
                                        if (tempVarCheck.AccessModifier.getType() == Token.TokenType.KEYWORD_PRIVATE) {
                                            throw new TypeCheckerException("TypeCheck Error: Parent Var has Private access");
                                        } else { //good access
                                            assType = tempVarCheck.Type.getType();
                                            //autoPossible = true; //mostly for reference
                                            if (tempT2 == assType) {
                                                //good
                                            } else { //not match
                                                if (tempT2 == Token.TokenType.KEYWORD_AUTO) { //funct param declared with auto type
                                                    System.out.println("Param " + i + " Var has AUTO type!");
                                                    //change auto type of assignment var
                                                    String[] auto = {ClassString, "Parameter", MethodString, "" + i, assType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                                                    AutoHandler.add(auto);
                                                } else if (assType == Token.TokenType.KEYWORD_AUTO) { //var being passed into method as param has an auto type, fix
                                                    System.out.println("Param has AUTO type!");
                                                    //change auto type of assignment var
                                                    String[] auto = {ClassString, "Parent", "Variable", MethodString, varName, tempT2.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                                                    AutoHandler.add(auto);
                                                } else { //bad types
                                                    throw new TypeCheckerException("TypeCheck Error: Param Types do not match");
                                                }
                                            }
                                        }
                                    } else {
                                        throw new TypeCheckerException("TypeCheck Error: Var Does not exist in Parent or Child");
                                    }
                                } else {
                                    throw new TypeCheckerException("TypeCheck Error: Var does not exist");
                                }
                            } //end parent class check
                        } else if (temp1 instanceof PStatementFunctionCall) {
                            ///hhhhhh
                        } else if (temp1 instanceof PExpressionAtom) {
                            ///hhhhhh
                            if (varDec.assignment.getClass().toString().equals("class CoopJa.PIdentifierReference")) {
                                System.out.println("PIdentifierReference");
                                PIdentifierReference tempVar = (PIdentifierReference) varDec.assignment;
                                String PIRvarname = tempVar.identifier.getTokenString();
                                if (varMap.VariableNames.containsKey(PIRvarname)) {
                                    ///hhhhhh
                                    //var found, check next identifier
                                    VarStor PIRtemp = varMap.VariableNames.get(PIRvarname);
                                } else {
                                    if (varMap.extendsClass != null) {
                                        if (varMap.extendsClass.VariableNames.containsKey(PIRvarname)) {
                                            ///hhhhhh
                                        } else {
                                            ///hhhhhh
                                        }
                                    } else {
                                        throw new TypeCheckerException("TypeCheck Error: Var does not exist in Parent or Child");
                                    }
                                }
                            } else {
                                System.out.println("PExpressionAtom");
                                PExpressionAtom tempVar = (PExpressionAtom) varDec.assignment;
                                Token.TokenType tempType = getType(tempVar);
                                if (tempT2 == tempType) {
                                    //good
                                } else {
                                    if (tempT2 == Token.TokenType.KEYWORD_AUTO) { //funct param declared with auto type
                                        System.out.println("Param " + i + " Var has AUTO type!");
                                        //change auto type of assignment var
                                        String[] auto = {ClassString, "Parameter", MethodString, "" + i, tempType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                                        AutoHandler.add(auto);
                                    } else if (false) {
                                        //no need to check if right side is auto, since it is a defined type
                                    } else { //bad types
                                        throw new TypeCheckerException("TypeCheck Error: Param Types do not match");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    throw new TypeCheckerException("TypeCheck Error: Function Parameters Do Not Match!");
                }
            } else { //check if function is in parent
                if (varMap.extendsClass != null) { //yes extends
                    if (varMap.extendsClass.MethodNames.containsKey(functName)) { //if function is in list in parent
                        //COPY ABOVE
                        // varMap.extendsClass.MethodNames.get(functName);
                    }
                } else { //no function exists with this name
                    throw new TypeCheckerException("TypeCheck Error: Function does not exist");
                }
            }
        } else if (varDec.assignment instanceof PExpressionAtom) {
            if (varDec.assignment.getClass().toString().equals("class CoopJa.PIdentifierReference")) {
                System.out.println("PIdentifierReference");
                /////////////////////XXXXXXXXXXXX
            } else {
                System.out.println("PExpressionAtom");
                assType = getType(varDec.assignment);
                if (varType == assType) {
                    System.out.println("Variable Declaration Succeeded");
                } else {
                    if (varType == Token.TokenType.KEYWORD_AUTO) {
                        System.out.println("AUTO var type detected!");
                        //change auto type
                        String[] auto = {ClassString, "Variable", MethodString, varDec.identifier.getTokenString(), assType.toString()}; //in class name, type (variable/method), in method name (could be empty if in class, var name, type to change to
                        AutoHandler.add(auto);
                    }
                }
            }
        }

        System.out.println("[[[[[[[[[[[[[[[[[[[[" + varDec.assignment.getClass() + "]]]]]]]]]]]]]");

        System.out.println(getType(varDec.assignment));


//        if (varDec.assignment != null) { //assuming there is an expression to be checked //now useless
//            Token.TokenType assignment = getType(varDec.assignment); //BODY
//            System.out.println("Variable Declared as Type: " + varDec.variableType.getType());
//            System.out.println("VarDec Assignment Type is: " + varDec.assignment.getClass());
//            if (assignment == Token.TokenType.KEYWORD_STRING) {
//                if (varDec.variableType.getType() != assignment) //string = not string
//                    throw new TypeCheckerException("TypeCheck Error: Expected " +
//                            varDec.variableType.getType() + " got " + assignment);
//            }
//            if (assignment == Token.TokenType.IDENTIFIER) {
//                System.out.println("IDENTIFIER Dectected");
//                System.out.println(varDec.assignment.getClass() + " -------class");
//            }
//        } else {
//            System.out.println("Empty VarDec Body");
//        }
//        System.out.println("Variable Declaration is Valid");
    }

    public static HashMap<String, VarStor> VariableDeclarationTypecheck(Storage map, PVariableDeclaration input, boolean assignmentAllowed) throws Exception { //take in map of all vars declared in scope, and the declaration stmt

        HashMap<String, VarStor> mapNEW = new HashMap<>(); //used to hold new vars
        AccessModifierTypecheck(input.accessModifier, false); //check if the access modifier is valid or not
        if (input.accessModifier != null) { //this if/else could be removed, mostly for visual output
            System.out.println("Declaration Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());
        } else {
            System.out.println("Declaration Access Modifier Type: NONE");
        }

        //check if type of var is valid, primitive types tokens: KEYWORD_INT,KEYWORD_DOUBLE,KEYWORD_CHAR,KEYWORD_BOOLEAN,KEYWORD_STRING
        if (input.variableType.getType() == Token.TokenType.KEYWORD_INT || input.variableType.getType() == Token.TokenType.KEYWORD_DOUBLE || input.variableType.getType() == Token.TokenType.KEYWORD_CHAR || input.variableType.getType() == Token.TokenType.KEYWORD_BOOLEAN || input.variableType.getType() == Token.TokenType.KEYWORD_STRING) {
            System.out.println("Primitive Type");
            System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
        } else if (input.variableType.getType() == Token.TokenType.KEYWORD_AUTO) { //is type of var AUTO? (token = KEYWORD_AUTO)
            System.out.println("Auto Type");
            //do AUTO stuff later XXXXXXX, maybe a boolean if it is an auto, and before assignment and storing etc, check bool and evaluate the type
            System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
        } else if (input.variableType.getType() == Token.TokenType.IDENTIFIER) { //is the type of the var a Class? (token = IDENTIFIER)
            System.out.println("Variable Declared of a Class");
            if (ClassListAll.containsKey(input.variableType.getTokenString())) { //check all class list for name
                System.out.println("Class Found");
                System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
            } else { //class not declared yet
                throw new Exception("Variable Declaration Error: Class of Variable Type not defined");
            }
        } else { //if all else fails, invalid type
            throw new Exception("Variable Declaration Error: Variable Type unrecognized");
        }

        if (map.extendsClass != null) { //yes extends
            System.out.println("extends 269");
            if (map.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Variable Declaration Error: Variable with same name already defined in scope: \n Inside class as a Variable");
            } else if (map.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Variable Declaration Error: Variable with same name already defined in scope: \n Inside class as a Method");
            } else if (map.extendsClass.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Variable Declaration Error: Variable with same name already defined in scope: \n Inside Parent class as a Variable");
            } else if (map.extendsClass.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Variable Declaration Error: Variable with same name already defined in scope: \n Inside Parent class as a Method");
            } else { //variable is okay
                VarStor tempVS = new VarStor(input.variableType, input.accessModifier); //create a new VarStor obj with the variable's data
                mapNEW.put(input.identifier.getTokenString(), tempVS); //add variable to new list of vars
                System.out.println("Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
            }
        } else { //no extends
            System.out.println("no extends 284");
            if (map.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Variable Declaration Error: Variable with same name already defined in scope: \n Inside class as a Variable");
            } else if (map.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Variable Declaration Error: Variable with same name already defined in scope: \n Inside class as a Method");
            } else { //variable is okay
                VarStor tempVS = new VarStor(input.variableType, input.accessModifier); //create a new VarStor obj with the variable's data
                mapNEW.put(input.identifier.getTokenString(), tempVS); //add variable to new list of vars
                System.out.println("Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
            }
        }

        if (input.assignment != null) { //if there is an assignment
            System.out.println("Assignment is present for Var");
            if (assignmentAllowed) {
                System.out.println("Assignment is allowed to be here");
                typeCheckVariableDec(input, map); //check variable declaration, is it valid?
            } else {
                System.err.println("No Assignment is allowed here!");
                throw new Exception("Variable Declaration Error: Variable given an assignment where it is not allowed");
            }
        } else {
            System.out.println("No Assignment is present for Var");
        }


        //passing in old map bc int new_var = new_var should throw new_var not declared.
        /////TEMP_unused_code_for_Expressions__VARDEC(input, containingClassMembers); ////XXXXXXXXXXXXXXXXXXXXXXXX fix, would resolve the body of the variable declaration (PExpression object)



        return mapNEW; //return the updated map of all defined variables in current scope
    }

    public static Storage MethodDeclarationTypecheck(Storage map, PStatementFunctionDeclaration input) throws Exception { //input: a class's Storage object & function declaration

        AccessModifierTypecheck(input.accessModifier, false); //check if the access modifier is valid or not
        if (input.accessModifier != null) { //this if/else could be removed, mostly for visual output
            System.out.println("Declaration Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());
        } else {
            System.out.println("Declaration Access Modifier Type: NONE");
        }

        //check if return type is valid, primitive types tokens: KEYWORD_INT,KEYWORD_DOUBLE,KEYWORD_CHAR,KEYWORD_BOOLEAN,KEYWORD_STRING
        if (input.returnType.getType() == Token.TokenType.KEYWORD_INT || input.returnType.getType() == Token.TokenType.KEYWORD_DOUBLE || input.returnType.getType() == Token.TokenType.KEYWORD_CHAR || input.returnType.getType() == Token.TokenType.KEYWORD_BOOLEAN || input.returnType.getType() == Token.TokenType.KEYWORD_STRING) {
            System.out.println("Primitive Return Type");
            System.out.println("Method Declaration Return Type: " + input.returnType.getType() + " " + input.returnType.getTokenString());
        } else if (input.returnType.getType() == Token.TokenType.KEYWORD_AUTO) { //is return type AUTO? (token = KEYWORD_AUTO)
            System.out.println("Auto Type");
            //do AUTO stuff later XXXXXXX, AUTO return type allowed?
            System.out.println("Method Declaration Return Type: " + input.returnType.getType() + " " + input.returnType.getTokenString());
        } else if (input.returnType.getType() == Token.TokenType.KEYWORD_VOID) {
            System.out.println("Void Type");
            System.out.println("Method Declaration Return Type: " + input.returnType.getType() + " " + input.returnType.getTokenString());
        } else if (input.returnType.getType() == Token.TokenType.IDENTIFIER) { //is return type a Class? (token = IDENTIFIER)
            System.out.println("Method Returns Type of Class");
            if (ClassListAll.containsKey(input.returnType.getTokenString())) { //check all class list for name
                System.out.println("Class Found");
                System.out.println("Method Returns Type of Class: " + input.returnType.getType() + " " + input.returnType.getTokenString());
            } else { //class not declared yet
                throw new Exception("Method Declaration Error: Class of Return Type not defined");
            }
        } else { //if all else fails, invalid type
            throw new Exception("Method Declaration Error: Return Type unrecognized");
        }

        FunctStor tempFS = new FunctStor(); //store all function stuff
        //check if method name already exists in scope (given map obj), check both var names and method names
        if (map.extendsClass != null) { //yes extends
            System.out.println("extends");
            if (map.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Method Declaration Error: Var with same name already defined in scope: \n Inside class as a Variable");
            } else if (map.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Method Declaration Error: Var with same name already defined in scope: \n Inside class as a Method");
            } else if (map.extendsClass.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Method Declaration Error: Var with same name already defined in scope: \n Inside Parent class as a Variable");
            } else if (map.extendsClass.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Method Declaration Error: Var with same name already defined in scope: \n Inside Parent class as a Method");
            } else { //if not, add it as a new var
                System.out.println("Method Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
                tempFS.AccessModifier = input.accessModifier;
                tempFS.ReturnType = input.returnType;
                tempFS.Classname = ClassString;
                //two more things to add: params & stmts, to FunctStor at this point
                map.MethodNames.put(input.identifier.getTokenString(), new FunctStor()); //add method to method names list with Blank FunctStor object for now
            }
        } else { //no extends
            System.out.println("no extends");
            if (map.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Method Declaration Error: Var with same name already defined in scope: \n Inside class as a Variable");
            } else if (map.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new Exception("Method Declaration Error: Var with same name already defined in scope: \n Inside class as a Method");
            } else { //if not, add it as a new var
                System.out.println("Method Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
                tempFS.AccessModifier = input.accessModifier;
                tempFS.ReturnType = input.returnType;
                tempFS.Classname = ClassString;
                //two more things to add: params & stmts, to FunctStor at this point
                map.MethodNames.put(input.identifier.getTokenString(), new FunctStor()); //add method to method names list with Blank FunctStor object for now
            }
        }

        MethodString = input.identifier.getTokenString(); //used for auto

        //deal with params
        HashMap<String, VarStor> tempFunctionVars = new HashMap<String, VarStor>();
        if (input.variableDeclarations != null) { //if method has params
            System.out.println("Method Parameters:");
            HashMap<String, VarStor> tempClassVars = map.VariableNames; //grab list of all class vars
            tempFunctionVars = map.MethodNames.get(input.identifier.getTokenString()).VariableNames; //grab all method vars
            HashMap<String, VarStor> combinedVars = new HashMap<>(); //define hashmap to store all vars the method needs to know about
            combinedVars.putAll(tempClassVars); //add class vars to combined vars list
            if (tempFunctionVars != null) { //if stuff is in list ///PUTALL ISSUE: here it will fail if you put "...size() != 0" but down it will fail if you put "... != null"
                combinedVars.putAll(tempFunctionVars); //add method vars to combined vars list, ie merge them
            } else { //if the list is empty, it will fail to putall
                //no need to merge, but need to initialize
                tempFunctionVars = new HashMap<String, VarStor>();
            }
//            if (map.extendsClass != null) { //if extends, add stuff
//                System.out.println("extends");
//                if (map.extendsClass.VariableNames != null || map.extendsClass.VariableNames.size() > 0) { //if extends class has stuff
//                    combinedVars.putAll(map.extendsClass.VariableNames);
//                }
//                if (map.extendsClass.MethodNames != null || map.extendsClass.MethodNames.size() > 0) { //if extends class has stuff
//                    Set<String> keys = map.extendsClass.MethodNames.keySet(); //pull out all keys of hashmap, since they dont match since vars use VarStor and methods use FunctStor
//                    for (String key : keys) { //add each key to combined list and use blank VarStor because we just care about name conflicts
//                        combinedVars.put(key, new VarStor());
//                    }
//                }
//            } else {
//                System.out.println("no extends");
//            } //commented out block note: i think VDT already handles this, but i wrote it, so it's staying here just in case
            Storage tempBuiltStor = new Storage(combinedVars, map.MethodNames, map.extendsClass); //create temp Storage object, so var combinations arent permanent, send this to verify var
            for (int i = 0; i < input.variableDeclarations.size(); i++) { //for all parameters in method
                HashMap<String, VarStor> output; //declare var for return of VDT()
                output = VariableDeclarationTypecheck(tempBuiltStor, input.variableDeclarations.get(i), false);
                combinedVars.putAll(output); //add new vars to combined vars list
                VarStor tempStor = output.get(input.identifier.getTokenString()); //just used to show how to get the VarStor obj
                tempFS.Parameters.add(i, tempStor); //add param to FunctStor object, ordered
                tempFunctionVars.put(input.variableDeclarations.get(i).identifier.getTokenString(), tempStor); //put param in method var storage
            }

        } else { //no method params
            System.out.println("Method has no Parameters");
        }

        HashMap<String, VarStor> methodBodyVars = new HashMap<String, VarStor>(); //store all method vars here
        if (tempFunctionVars != null) { //add params to method vars
            System.out.println("Method Params added to Variable List");
            methodBodyVars.putAll(tempFunctionVars);
        } else {
            System.out.println("Method had no variables, so none added to pre-check variable list");
        }

        if (input.statementList != null) {
            System.out.println("Method Declaration Body: Statement List");
            Storage statementStorage = map.Copy();//extendclass Merged copy of storage for use in statement blocks without editing the real map
            for (int k = 0; k < input.statementList.size(); k++) { //for all body stmts (PStmt)
                PStatement tempStmtExp = input.statementList.get(k);

                TEMP_unused_code_for_PStmts__PSTATEMENT(tempStmtExp, statementStorage);
                ///NOTE: if there is a variable declaration, it needs to be added to a list after
                ///need to keep a "HashMap<String,VarStor>" of all vars, then add to "tempFS.VariableNames", using "methodBodyVars"

            }
        } else {
            System.out.println("Method Body has no statements");
        }

        if (tempFS.VariableNames == null) {
            tempFS.VariableNames = new HashMap<>();
        }

        if (methodBodyVars.size() != 0) { //yes method body vars ///PUTALL ISSUE: here, it will not work correctly if you say "... != null", but above it will fail if you put "...size() != 0"
            tempFS.VariableNames.putAll(methodBodyVars); //XXXXXXXXXX Fix, right now it is EMPTY, used to give all var names for method
            //all 5 parts of tempFS (FunctStor) obj added, need to replace this FunctStor object for this method in map
        } else { //no method body vars, empty
            //do nothing since empty
        }

        map.MethodNames.put(input.identifier.getTokenString(), tempFS); //update FunctStor (before was blank), replace previous entry

        MethodString = "";

        return map; //return class Storage object updated

    }

    public static String ClassTypecheck(PClassDeclaration input) throws Exception { //typecheck the class declaration

        String extendsHandler = null; //no
        AccessModifierTypecheck(input.accessModifier, true); //make sure the access modifier is valid
        System.out.println("Class Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());

        System.out.println("Class Identifier (Name): " + input.identifier.getType() + " " + input.identifier.getTokenString());
        ClassString = input.identifier.getTokenString(); //assign current class name

        System.out.print("Class Extends a Class?: "); //find out if this current class extends another class (based on its declaration)
        if (input.extendsIdentifier != null) { //the class does extend another
            //Check if the class extends itself.
            if (input.extendsIdentifier.getTokenString().equals(ClassString)) {
                throw new Exception("Class Error: Class cannot extend itself.");
            }
            if (ClassListAll.containsKey(input.extendsIdentifier.getTokenString())) { //check if this class (that the working class is supposed to extend) is known yet/exists
                System.out.println("yes " + input.extendsIdentifier.getType() + " " + input.extendsIdentifier.getTokenString()); //it does exist
                extendsHandler = input.extendsIdentifier.getTokenString(); //yes, give name of class
            } else { //class extends class that does not exist (yet)
                throw new Exception("Class Error: Class Extends Class that does not exist");
            }
        } else { //the class does not extend another
            System.out.println("no");
        }
        return extendsHandler;

    }

    public static void AccessModifierTypecheck(Token input, boolean isClass) throws Exception {
        //access modifier required in class, but will fail at parser level if not there
        if (isClass) {
            if (input.getType() == Token.TokenType.KEYWORD_PUBLIC || input.getType() == Token.TokenType.KEYWORD_PRIVATE || input.getType() == Token.TokenType.KEYWORD_PROTECTED) {
                //good
            } else {
                throw new Exception("Class Typecheck Error: Class Access Modifier Invalid");
            }
        } else { //not class, access modifier may be blank
            if (input == null || input.getType() == Token.TokenType.KEYWORD_PUBLIC || input.getType() == Token.TokenType.KEYWORD_PRIVATE || input.getType() == Token.TokenType.KEYWORD_PROTECTED) {
                //good
            } else {
                throw new Exception("Declaration Error: Access Modifier Invalid");
            }
        }
    }

    //Should return a type.
    public static void TEMP_unused_code_for_Expressions__VARDEC(PVariableDeclaration input, Storage containingClassMembers) throws Exception {
        System.out.println("Declaration Body: ");
        if (input.assignment != null) {

            if (input.assignment instanceof PExpressionStub) {
                System.out.println("Instance of PExpressionStub");
                PExpressionStub tempExp = (PExpressionStub) input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionBinOp) { //----------------
                System.out.println("Instance of PExpressionBinOp");
                PExpressionBinOp tempExp = (PExpressionBinOp) input.assignment;
                //2 pexpressions 1 token
            }
            if (input.assignment instanceof PExpressionIdentifierReference) {
                System.out.println("Instance of PExpressionIdentifierReference");
                PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference) input.assignment;
                //1 token 1 pexpr
            }
            if (input.assignment instanceof PExpressionVariable) { //----------------
                System.out.println("Instance of PExpressionVariable");
                PExpressionVariable tempExp = (PExpressionVariable) input.assignment;
                //1 token
            }
            if (input.assignment instanceof PStatementFunctionCall) {
                System.out.println("Instance of PStatementFunctionCall");
                PStatementFunctionCall tempExp = (PStatementFunctionCall) input.assignment;
                ////XXXXXXX this method should now be obsolete, with the implementation of getType() etc, the one we need to work on is the ...PSTATEMENT temp method()
                ////XXXXXXX if this is not the case, what is not being handled?
                //Do we need to check if the function being called exists, or has it been checked by this point. ---> no, this is called during function evaluation / declaration, but that's fine XXXXXXXXXXXX
                //_will just typecheck here again for safety.
                //Function call token doesn't have return type member, so we have to get it.
                checkFunctionCallExists(tempExp, containingClassMembers);
                //1 Token , 1 ArrayList<PExpression>
            }
            if (input.assignment instanceof PExpressionAtomBooleanLiteral) { //----------------
                System.out.println("Instance of PExpressionAtomBooleanLiteral");
                PExpressionAtomBooleanLiteral tempExp = (PExpressionAtomBooleanLiteral) input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionAtomNullLiteral) {
                System.out.println("Instance of PExpressionAtomNullLiteral");
                PExpressionAtomNullLiteral tempExp = (PExpressionAtomNullLiteral) input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionAtomNumberLiteral) { //----------------
                System.out.println("Instance of PExpressionAtomNumberLiteral");
                PExpressionAtomNumberLiteral tempExp = (PExpressionAtomNumberLiteral) input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionAtomObjectConstruction) {
                System.out.println("Instance of PExpressionAtomObjectConstruction");
                PExpressionAtomObjectConstruction tempExp = (PExpressionAtomObjectConstruction) input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionAtomStringLiteral) { //----------------
                System.out.println("Instance of PExpressionAtomStringLiteral");
                PExpressionAtomStringLiteral tempExp = (PExpressionAtomStringLiteral) input.assignment;
                //1 token
            }
            if (input.assignment instanceof PIdentifierReference) {
                System.out.println("Instance of PIdentifierReference");
                PIdentifierReference tempExp = (PIdentifierReference) input.assignment;
                //1 Token , 1 PStatement
            }
        } else {
            System.out.println("Declaration PExpression assignment Empty");
        }
    }

    public static void TEMP_unused_code_for_PStmts__PSTATEMENT(PStatement tempStmtExp, Storage currentScope) throws TypeCheckerException {
        if (tempStmtExp instanceof PExpressionIdentifierReference) {
            System.out.println("Instance of PExpressionIdentifierReference");
            PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference) tempStmtExp;
            //1 token 1 pexpr
        }
        if (tempStmtExp instanceof PIdentifierReference) {
            System.out.println("Instance of PIdentifierReference");
            PIdentifierReference tempExp = (PIdentifierReference) tempStmtExp;
            //1 token 1 pstmt
            getExpressionType((PIdentifierReference)tempStmtExp, currentScope);//technically can also be expression so it can be handled as an expression
        }
        if (tempStmtExp instanceof PStatementBreak) {
            System.out.println("Instance of PStatementBreak");
            PStatementBreak tempExp = (PStatementBreak) tempStmtExp;
            //1 token
        }
        if (tempStmtExp instanceof PStatementForStatement) {
            System.out.println("Instance of PStatementForStatement");
            PStatementForStatement tempExp = (PStatementForStatement) tempStmtExp;
            //1 PStatement ,  1 PExpression,  1 PStatement , 1 ArrayList<PStatement>
            typeCheckForStatement(tempExp, currentScope);
        }
        if (tempStmtExp instanceof PStatementFunctionCall) {
            System.out.println("Instance of PStatementFunctionCall");
            PStatementFunctionCall tempExp = (PStatementFunctionCall) tempStmtExp;
            //1 Token , 1 ArrayList<PExpression>
        }
        if (tempStmtExp instanceof PStatementFunctionDeclaration) {
            System.out.println("Instance of PStatementFunctionDeclaration");
            PStatementFunctionDeclaration tempExp = (PStatementFunctionDeclaration) tempStmtExp;
            //handled above
        }
        if (tempStmtExp instanceof PStatementIfStatement) {
            System.out.println("Instance of PStatementIfStatement");
            PStatementIfStatement tempExp = (PStatementIfStatement) tempStmtExp;
            //1 PExpression , 1 ArrayList<PStatement> , 1 ArrayList<PStatement>

            typeCheckIfStatement(tempExp, currentScope);
        }
        if (tempStmtExp instanceof PStatementPrintln) {
            System.out.println("Instance of PStatementPrintln");
            PStatementPrintln tempExp = (PStatementPrintln) tempStmtExp;
            //1 token
        }
        if (tempStmtExp instanceof PStatementReturn) {
            System.out.println("Instance of PStatementReturn");
            PStatementReturn tempExp = (PStatementReturn) tempStmtExp;
            //1 pexpr
        }
        if (tempStmtExp instanceof PStatementWhileStatement) {
            System.out.println("Instance of PStatementWhileStatement");
            PStatementWhileStatement tempExp = (PStatementWhileStatement) tempStmtExp;
            //1 PExpression , 1 ArrayList<PStatement>
            typeCheckWhileStatement(tempExp, currentScope);
        }
        if (tempStmtExp instanceof PVariableAssignment) {
            System.out.println("Instance of PVariableAssignment");
            PVariableAssignment tempExp = (PVariableAssignment) tempStmtExp;
            //1 token, 1 pexpr

            typeCheckVariableAssignment(tempExp, currentScope);
        }
        if (tempStmtExp instanceof PVariableDeclaration) {
            System.out.println("Instance of PVariableDeclaration");
            PVariableDeclaration tempExp = (PVariableDeclaration) tempStmtExp;
            //already handled
        }
    }

    public static void checkFunctionCallExists(PStatementFunctionCall tempExp, Storage containingClassMembers) throws Exception {
        //Get the functions in the storage.
      /*(i)(!) because we check class's PStatements (functions) after vars,
        methods used in a line before they're declared will throw an exception.
      */
        HashMap<String, FunctStor> methodsInScope = containingClassMembers.MethodNames;
        //check if the function call doesn't exist.
        if (!(methodsInScope.containsKey(tempExp.identifier.getTokenString()))) {
            //we throw an exception
            throw new Exception("Method Call Error: Method " + tempExp.identifier.getTokenString() + " does not exist.");
        } else //we check if the signature is a match, so param vs arg types.
        {
            FunctStor possMatch = methodsInScope.get(tempExp);

            //(!) The parser should have checked that the params are the same length?
            if (possMatch.Parameters.size() != tempExp.expressionsInput.size())
                throw new Exception("Method Call Error: Method " + tempExp.identifier.getTokenString() + " does not exist.");

            for (int param = 0; param < possMatch.Parameters.size(); param++) {
                //No good. Needs better way to distinguish Types of tokens, or better var names, because Type.TokenType ??
                //Token argType = TEMP_unused_code_for_Expressions__VARDEC(tempExp.expressionsInput.get(param));
                //Go through list of parameters.
                //if(possMatch.Parameters.get(param).Type.TokenType != argType.TokenType)
                //   throw new Exception("Method Call Error: Method parameter"+param+" expected type " +
                //                  possMatch.Parameters.get(param).Type.getTokenString()+
                //                "but instead got type " + tempExp.identifier.getTokenString());
            }
        }//End check signature matcch.
    }//End checkFunctionCallExists( ).

    //***************************************EXPRESSION TYPECHECKING********************************

    private static void typeCheckVariableAssignment(PVariableAssignment varAss, Storage currentScope) throws TypeCheckerException {
        //typecheck assignement
        Token.TokenType assignment = getExpressionType(varAss.value, currentScope);
        //check if assignee is within the scope
        Token.TokenType assignee = VariableInScope(varAss.identifier.getTokenString(), currentScope);
        if (assignment == Token.TokenType.KEYWORD_STRING) {//strings types name return as type identifiers rather than KEYWORD_STRING, this if handles that
            if (!assignee.equals("string"))
                throw new TypeCheckerException("TypeCheck Error: Expected " +
                        assignee + " got " + assignment);

        } else if (assignment != assignee) {
            throw new TypeCheckerException("TypeCheck Error: Expected " +
                    assignee + " got " + assignment);
        }
    }

    private static void typeCheckWhileStatement(PStatementWhileStatement whileStatement, Storage currentScope) throws TypeCheckerException {
        Storage whileScope = currentScope.Copy();
        if (getExpressionType(whileStatement.expression, whileScope) != Token.TokenType.KEYWORD_BOOLEAN)
            throw new TypeCheckerException("While Loop Expression must be of type BOOLEAN");
        for (PStatement statement : whileStatement.statementList) {
            TEMP_unused_code_for_PStmts__PSTATEMENT(statement, whileScope);
        }
    }

    private static void typeCheckIfStatement(PStatementIfStatement ifStatement, Storage currentScope) throws TypeCheckerException {
        Storage ifScope = currentScope.Copy(); //if statement needs its own scope, anything declared inside stays inside
        Storage elseScope = currentScope.Copy(); //exclusive scope for the else block that wont interfere with anything outside
        //check if expression is boolean
        if (getExpressionType(ifStatement.expression, ifScope) != Token.TokenType.KEYWORD_BOOLEAN)
            throw new TypeCheckerException("Expression in IF statement not a Boolean");
        //typecheck elements in if statement
        for (PStatement statement : ifStatement.statementList) {
            TEMP_unused_code_for_PStmts__PSTATEMENT(statement, ifScope);
        }
        //typecheck elements in else statement
        for (PStatement statement : ifStatement.elseStatementList) {
            TEMP_unused_code_for_PStmts__PSTATEMENT(statement, elseScope);
        }
    }

    private static void typeCheckForStatement(PStatementForStatement forStatement, Storage currentScope) throws TypeCheckerException {
        Storage forScope = currentScope.Copy();//exclusive scope for the For Loop that wont interfere with anything outside
        if (!(forStatement.statement1 instanceof PVariableDeclaration))//make sure first statement is a variable declaration
            throw new TypeCheckerException("First Statement in For Loop Must be a variable declaration");
        //remember to add the incrementor declaration to the scope of the for loop
        PVariableDeclaration varDec = (PVariableDeclaration) forStatement.statement1;
        VarStor newVariableStore = new VarStor(varDec.variableType, varDec.accessModifier);
        forScope.VariableNames.put(varDec.identifier.getTokenString(), newVariableStore);
        TEMP_unused_code_for_PStmts__PSTATEMENT(forStatement.statement1, forScope); //typecheck variable decleration,....might not be implemented in this function
        if (getExpressionType(forStatement.expression, forScope) != Token.TokenType.KEYWORD_BOOLEAN) //typecheck continue expression
            throw new TypeCheckerException("For Loop Expression must be of type BOOLEAN");
        //we're going to ignore the third part of the for loop for now.....
        for (PStatement statement : forStatement.statementList) {
            TEMP_unused_code_for_PStmts__PSTATEMENT(statement, forScope);
        }
    }

    //this part is likely unnessesary as VDT can handles this part
    /*
    private static void typeCheckStatementVariableDec(PVariableDeclaration varDec, Storage currentScope) throws TypeCheckerException {
        //not sure if already initialized variables are typechecked yet
        //add here if they arent
        VarStor newVariableStore = new VarStor(varDec.accessModifier, varDec.variableType);
        currentScope.VariableNames.put(varDec.identifier.getTokenString(), newVariableStore);
        if (varDec.assignment != null) { //assuming there is an expression to be checked
            Token.TokenType assignment = getExpressionType(varDec.assignment, currentScope); //BODY
            if (assignment == Token.TokenType.KEYWORD_STRING) {//strings types name return as type identifiers rather than KEYWORD_STRING, this if handles that
                if (!(varDec.variableType.getTokenString().equals("String") || varDec.variableType.getTokenString().equals("string")))
                    throw new TypeCheckerException("TypeCheck Error: Expected " +
                            varDec.variableType.getType() + " got " + assignment);

            } else if (assignment != varDec.variableType.getType()) {//compare types with assignment
                throw new TypeCheckerException("TypeCheck Error: Expected " +
                        varDec.variableType.getType() + " got " + assignment);
            }
        }
    }
    */

    //idea, recursive methodology also make sure that currentScope is a copy.
    public static Token.TokenType getExpressionType(PExpression exp, Storage currentScope) throws TypeCheckerException {
        if (exp instanceof PExpressionAtomNumberLiteral)
            return Token.TokenType.KEYWORD_INT; //Expand here once we have more than just ints
        else if (exp instanceof PExpressionAtomStringLiteral)
            return Token.TokenType.KEYWORD_STRING;
        else if (exp instanceof PExpressionAtomBooleanLiteral)
            return Token.TokenType.KEYWORD_BOOLEAN; //technically not the "boolean" keyword, but lets use this for now
        else if (exp instanceof PExpressionVariable) {
            //oh no, this is getting its own method
            return VariableInScope(((PExpressionVariable) exp).variable.getTokenString(), currentScope);
        }
        else if (exp instanceof PIdentifierReference) {
            PIdentifierReference PIR = (PIdentifierReference) exp;
            return IdentifierReferenceTypeCheckDriver(PIR, currentScope);//moved to own method
        }
        else if (exp instanceof PStatementFunctionCall){
            PStatementFunctionCall functionCall = (PStatementFunctionCall) exp;
            return FunctionCallTypeCheck(functionCall, currentScope);
        }
        else if (exp instanceof PExpressionBinOp) {
            //recursivly do both hands of the expressions
            Token.TokenType lhs = getExpressionType(((PExpressionBinOp) exp).lhs, currentScope);

            Token.TokenType rhs = getExpressionType(((PExpressionBinOp) exp).rhs, currentScope);

            if (lhs != rhs) //The operand types do not match.
            {
                //Allows string & integer concatenation
                if ((lhs == Token.TokenType.KEYWORD_STRING && rhs == Token.TokenType.KEYWORD_INT) ||
                        (lhs == Token.TokenType.KEYWORD_INT && rhs == Token.TokenType.KEYWORD_STRING))
                    return Token.TokenType.KEYWORD_STRING; //concatinating an integer to a string
                else //anything else must fail
                    throw new TypeCheckerException("TypeCheck Error: Expected " +
                            lhs.name() + " got " + rhs.name());
            }

            Token.TokenType output = lhs;//at this point we already determined lhs and rhs are the same type
            //check if the operator is the right type for the expression
            Token.TokenType operator = ((PExpressionBinOp) exp).operatorToken.getType();
            //System.out.println("\t"+rhs.name()+"_"+operator.name()+"_"+lhs.name());
            /********** + *************/
            if (operator == Token.TokenType.SYMBOL_PLUS) //plus operator works w/ ints and strings.
            {

                if ((output != Token.TokenType.KEYWORD_STRING) && (output != Token.TokenType.KEYWORD_INT)) {
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
                }
            }/********** numeric ops *************/
            else if (operator == Token.TokenType.SYMBOL_MINUS || //number operations
                    operator == Token.TokenType.SYMBOL_ASTERISK ||
                    operator == Token.TokenType.SYMBOL_SLASH ||
                    /****** BITWISE OPS ******/
                    operator == Token.TokenType.SYMBOL_SHIFTRIGHT ||
                    operator == Token.TokenType.SYMBOL_SHIFTLEFT ||
                    operator == Token.TokenType.SYMBOL_AMPERSAND ||
                    operator == Token.TokenType.SYMBOL_BAR ||
                    operator == Token.TokenType.SYMBOL_CARET ||
                    operator == Token.TokenType.SYMBOL_TILDE) {
                if (output != Token.TokenType.KEYWORD_INT) /////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
            }/********* DOUBLE EQUALS *******/
            else if ((operator == Token.TokenType.SYMBOL_DOUBLEEQUALS) || //plus operator works w/ ints and strings.
                    (operator == Token.TokenType.SYMBOL_NOTEQUAL)) {   /*Add more valid operand types for double equals if necessary.*/
                if ((output != Token.TokenType.KEYWORD_INT) &&
                        (output != Token.TokenType.KEYWORD_BOOLEAN)) {
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type: Expected type" +
                            /*Token.TokenType.KEYWORD_INT.name().split("_")[0] + */
                            output.name());
                }
                output = Token.TokenType.KEYWORD_BOOLEAN;
            }/********** Boolean ops ***********/
            else if (operator == Token.TokenType.SYMBOL_DOUBLEAMPERSAND ||
                    operator == Token.TokenType.SYMBOL_DOUBLEBAR) {
                if (output != Token.TokenType.KEYWORD_BOOLEAN) //at this point we already determined lhs and rhs are the same type
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
            }/************ numeric equality ops ***********/
            else if (operator == Token.TokenType.SYMBOL_GREATERTHAN ||
                    operator == Token.TokenType.SYMBOL_GREATERTHANEQUAL ||
                    operator == Token.TokenType.SYMBOL_LESSTHAN ||
                    operator == Token.TokenType.SYMBOL_LESSTHANEQUAL) {
                if (output != Token.TokenType.KEYWORD_INT) //in these cases the lhs rhs are ints and the output is boolean
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
                output = Token.TokenType.KEYWORD_BOOLEAN;
            }/*****     *****/
            //else if( operator ==
            //if the two sides match just return the type of one of the sides
            return output;
        }
        return null;
    }

    //for both FunctionCallTypeCheck and IdentifierReferenceTypeCheck, we need to make sure the parameters are typechecked properly as well
    //probably check to make sure we dont use the wrong storage for the internal parameters
    public static Token.TokenType FunctionCallTypeCheck (PStatementFunctionCall functionCall, Storage currentScope)throws TypeCheckerException{
        System.out.println("PStatementFunctionCall");
        String identifierName = functionCall.identifier.getTokenString();
        if (currentScope.MethodNames.containsKey(identifierName)){//check if method is within scope
            FunctStor method = currentScope.MethodNames.get(identifierName);
            ArrayList<VarStor> parameters = method.Parameters; //retrieve parameters
            TypeCheckFunctionCallParameters(parameters, functionCall, currentScope);
            return method.ReturnType.getType();
        }
        else { //otherwise check if its within its parents scope
            if (currentScope.extendsClass != null) {
                return ExtendedFunctionCallTypeCheck(functionCall, currentScope.extendsClass);
            } else
                throw new TypeCheckerException("Method " + identifierName + " not in scope;");
        }
    }

    //to handle private instances in extended classes
    public static Token.TokenType ExtendedFunctionCallTypeCheck (PStatementFunctionCall functionCall, Storage currentScope)throws TypeCheckerException{
        String identifierName = functionCall.identifier.getTokenString();
        if (currentScope.MethodNames.containsKey(identifierName)){//check if method is within scope
            FunctStor method = currentScope.MethodNames.get(identifierName);
            if (method.AccessModifier.equals(Token.TokenType.KEYWORD_PRIVATE))//this is the only part that is different
                throw new TypeCheckerException("Method access violation");
            ArrayList<VarStor> parameters = method.Parameters; //retrieve parameters
            TypeCheckFunctionCallParameters(parameters, functionCall, currentScope);
            return method.ReturnType.getType();
        }
        else { //otherwise check if its within its parents scope
            if (currentScope.extendsClass != null) {
                return ExtendedFunctionCallTypeCheck(functionCall, currentScope.extendsClass);
            } else
                throw new TypeCheckerException("Method " + identifierName + " not in scope;");
        }
    }

    //this will work as a recursive method within the recursive method getExpressionType
    //this will run through its own recursive routine to handle
    //foo.foo1.foo2(x, y) is valid foo.foo1().foo2 is not
    public static Token.TokenType IdentifierReferenceTypeCheckDriver (PIdentifierReference PIR, Storage currentScope) throws TypeCheckerException{
        Token.TokenType resultingType;
        //set global to keep track of surface storage
        FunctionCallParameterScope = currentScope;
        resultingType = IdentifierReferenceTypeCheck(PIR, currentScope);//call recursive function
        FunctionCallParameterScope = null; //set global scope to null..........just in case
        return resultingType;
    }

    //as this method runs through its recursive calls the FunctionCallParameterScope will remain the scope on its surface
    public static Token.TokenType IdentifierReferenceTypeCheck (PIdentifierReference PIR, Storage currentScope) throws TypeCheckerException{
        System.out.println("PIdentifierReference");
        String identifierName = PIR.identifier.getTokenString();
        if (ClassListAll.containsKey(identifierName)) {//make sure the class being called is in the list
            Storage classSpecificStorage = ClassListAll.get(identifierName);//get scope of class being called
            if(PIR.nextStatement == null){//if this of type foo.variable or foo.identifier.identifier2...... and so on
                if (PIR.nextExpression instanceof PExpressionVariable){//variable called from within another method foo.var
                    //use storage from within variables class to determine its validity
                    return  getExpressionType(PIR.nextExpression, classSpecificStorage);
                }
                else if (PIR.nextExpression instanceof PIdentifierReference){//if it is a daisy chained call foo.identifier.identifier2......
                    return IdentifierReferenceTypeCheck((PIdentifierReference)PIR.nextExpression, classSpecificStorage);//
                }
                else {
                    throw new TypeCheckerException("Unexpected result in IdentifierReferenceTypeCheck: " + PIR.nextExpression.getClass());
                }
            }
            else {//PIR.nextExpression == null....... in other words if this is of type foo.method()
                return FunctionCallTypeCheckFromIRef((PStatementFunctionCall) PIR.nextStatement, classSpecificStorage);
            }
        }
        else
            throw new TypeCheckerException("Unrecognized class: " + identifierName);
    }

    //same as FunctionCallTypeCheck but using FunctionCallParameterScope ClassSpecificStorage is still used to determine the method validity
    //for use in IdentifierReferenceTypeCheck
    public static Token.TokenType FunctionCallTypeCheckFromIRef (PStatementFunctionCall functionCall, Storage currentScope)throws TypeCheckerException{
        System.out.println("PStatementFunctionCall");
        String identifierName = functionCall.identifier.getTokenString();
        if (currentScope.MethodNames.containsKey(identifierName)){//check if method is within scope
            FunctStor method = currentScope.MethodNames.get(identifierName); // retrieve method
            ArrayList<VarStor> parameters = method.Parameters; //retrieve parameters
            //USE FunctionCallParameterScope to make sure we are not using a nested scope to check for the given variables
            //remember these variables are expected to be declared on the method that is calling this IRef
            //foo.bar(x,y) x and y are declared in the surface scope
            TypeCheckFunctionCallParameters(parameters, functionCall, FunctionCallParameterScope);
            //moved for loop to method to Typecheckfunctioncallparameters
            return method.ReturnType.getType();
        }
        else { //otherwise check if its within its parents scope
            if (currentScope.extendsClass != null) {
                //another recursive call to check
                return ExtendedFunctionCallTypeCheckFromIRef(functionCall, currentScope.extendsClass);
            } else
                throw new TypeCheckerException("Method " + identifierName + " not in scope;");
        }
    }

    //for extended class function calls
    public static Token.TokenType ExtendedFunctionCallTypeCheckFromIRef (PStatementFunctionCall functionCall, Storage currentScope)throws TypeCheckerException{
        System.out.println("PStatementFunctionCall");
        String identifierName = functionCall.identifier.getTokenString();
        if (currentScope.MethodNames.containsKey(identifierName)){//check if method is within scope
            FunctStor method = currentScope.MethodNames.get(identifierName); // retrieve method
            if (method.AccessModifier.equals(Token.TokenType.KEYWORD_PRIVATE))//this is the only part that is different
                throw new TypeCheckerException("Method access violation");
            ArrayList<VarStor> parameters = method.Parameters; //retrieve parameters
            //USE FunctionCallParameterScope to make sure we are not using a nested scope to check for the given variables
            TypeCheckFunctionCallParameters(parameters, functionCall, FunctionCallParameterScope);
            return method.ReturnType.getType();
        }
        else { //otherwise check if its within its parents scope
            if (currentScope.extendsClass != null) {
                //another recursive call to check
                return FunctionCallTypeCheckFromIRef(functionCall, currentScope.extendsClass);
            } else
                throw new TypeCheckerException("Method " + identifierName + " not in scope;");
        }
    }

    public static void TypeCheckFunctionCallParameters(ArrayList<VarStor> parameters, PStatementFunctionCall functionCall, Storage currentScope) throws TypeCheckerException{
        for (int i=0; i < parameters.size(); i++){//TypeCheck all of the parameters
            Token.TokenType expectedType = parameters.get(i).Type.getType();//retrive expected Type of parameter
            PExpression givenVariable = functionCall.expressionsInput.get(i);//retrieve given type of parameter
            Token.TokenType givenType = getExpressionType(givenVariable, currentScope);
            if (expectedType != givenType){
                throw new TypeCheckerException("expected type " + expectedType + " got " + givenType);
            }
        }
    }

    //cleanly check for variable in scope
    //if it does not exist throw an exception
    public static Token.TokenType VariableInScope (String varName, Storage currentScope) throws TypeCheckerException{


        System.out.println("PExpressionVariable");
        if (currentScope.VariableNames.containsKey(varName)) { //if var is in class
            VarStor tempVarCheck = currentScope.VariableNames.get(varName);
            return tempVarCheck.Type.getType();
        }
        else { //check parent if any
            if (currentScope.extendsClass != null) { //if there is a parent class
                return VariableInExtendedScope(varName, currentScope.extendsClass);
            }
            else { //no parent class
                throw new TypeCheckerException("TypeCheck Error: No var exists");
            }
        }
    }

    //similar to VariableInScope, however will throw exception if Variable has private access type
    public static Token.TokenType VariableInExtendedScope (String varName, Storage currentScope) throws TypeCheckerException{
        if (currentScope.VariableNames.containsKey(varName)) { //if var is in class
            VarStor tempVarCheck = currentScope.VariableNames.get(varName);
            if (tempVarCheck.AccessModifier.equals(Token.TokenType.KEYWORD_PRIVATE))//this is the only part that is different
                throw new TypeCheckerException("Private Access Violation");
            return tempVarCheck.Type.getType();
        }
        else { //check parent if any
            if (currentScope.extendsClass != null) { //if there is a parent class
                return VariableInExtendedScope(varName, currentScope.extendsClass);
            }
            else { //no parent class
                throw new TypeCheckerException("TypeCheck Error: No var exists");
            }
        }
    }
}//End TypChecker class

class Storage {

    HashMap<String, VarStor> VariableNames; //name, object
    HashMap<String, FunctStor> MethodNames;
    Storage extendsClass;

    public Storage(HashMap<String, VarStor> vars, HashMap<String, FunctStor> funct, Storage exClass) {
        VariableNames = vars;
        MethodNames = funct;
        extendsClass = exClass;
    }

    public Storage() {
        VariableNames = new HashMap();
        MethodNames = new HashMap();
        extendsClass = null;
    }

    public Storage Copy() {
        HashMap<String, VarStor> copyVariableNames = new HashMap<>(VariableNames);
        HashMap<String, FunctStor> coptMethodNames = new HashMap<>(MethodNames);
        Storage copyStorage = extendsClass;
//        //merge extends class with current storage for copy
//        if (extendsClass != null){
//            copyVariableNames.putAll(extendsClass.VariableNames);
//            coptMethodNames.putAll(extendsClass.MethodNames);
//        }
//        //no extends class for any copy, it will always be nullified and merged to current hashtables
        copyStorage = new Storage(copyVariableNames , coptMethodNames, copyStorage); //need to keep extends i think
        return copyStorage;
    }
}

class VarStor { //stores var info

    Token Type;
    Token AccessModifier;

    public VarStor(Token type_in, Token accessmodifier_in) {
        Type = type_in;
        AccessModifier = accessmodifier_in;
    }

    public VarStor() {
        Type = null;
        AccessModifier = null;
    }
}

class FunctStor { //store method stuff

    Token ReturnType;
    Token AccessModifier;
    String Classname; //name of the class the function is located
    ArrayList<VarStor> Parameters = new ArrayList<VarStor>(); //ordered list of paramteres stored as VarStor objs
    HashMap<String, VarStor> VariableNames; //stores all method vars declared inside it

    public FunctStor(Token Return_temp, Token AM_temp, String class_temp, ArrayList<VarStor> Params_temp, HashMap<String, VarStor> VN_temp) { //convert to tokens?? XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx
        ReturnType = Return_temp;
        AccessModifier = AM_temp;
        Classname = class_temp;
        Parameters = Params_temp;
        VariableNames = VN_temp;
    }

    public FunctStor() {

    }
}