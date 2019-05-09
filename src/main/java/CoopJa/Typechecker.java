package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Typechecker {

    public static HashMap<String, Storage> ClassListAll = new HashMap(); //holds (Class Name, Storage Object (holds ArrayList<String> of names of Variables and Methods for the Class)
    public static String ClassString = ""; //keeps name of the currently typechecking class, used to find this class's Storage object from the ClassListAll var

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

        String foo = "public class one {" +
                "int test = 0;" +
                "public void main() {" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public void main(int test1) {" +
                //"int test = 0;" + //not detecting inside stuff
                "}" +
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
                Storage extendsObj = ClassListAll.get(extendscheck); //could do it better, add to Storage object an extends Storage obj to hold this XXXXXXXXXXXXXXXXX but would need to merge each method / var dec call
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
                    t_NEWVars = VariableDeclarationTypecheck(t_S, tempVar); //call VDT with this list of vars (Scope) and get new info
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
        if (exp instanceof PExpressionVariable) { //changed XXXXXXXXX CHANGE TO CHECK VARIABLES IN SCOPE XXXXXXXXXXXXXXXXXXXXXXXZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ~~~~~
            return ((PExpressionVariable) exp).variable.getType();
        }
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

    public static void typeCheckVariableDec(PVariableDeclaration varDec) throws TypeCheckerException {
        System.out.println("Checking Variable Declaration Body");
        if (varDec.assignment != null) { //assuming there is an expression to be checked
            Token.TokenType assignment = getType(varDec.assignment); //BODY
            System.out.println("Variable Declared as Type: " + varDec.variableType.getType());
            System.out.println("VarDec Assignment Type is: " + assignment);
            if (assignment == Token.TokenType.KEYWORD_STRING) {
                if (varDec.variableType.getType() != assignment) //string = not string
                    throw new TypeCheckerException("TypeCheck Error: Expected " +
                            varDec.variableType.getType() + " got " + assignment);
            }
            if (assignment == Token.TokenType.IDENTIFIER) {
                System.out.println("IDENTIFIER Dectected");
                //TBD DO IDENTIFIER STUFF XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            }
        } else {
            System.out.println("Empty VarDec Body");
        }
        System.out.println("Variable Declaration is Valid");
    }

    public static HashMap<String, VarStor> VariableDeclarationTypecheck(Storage map, PVariableDeclaration input) throws Exception { //take in map of all vars declared in scope, and the declaration stmt

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


        //passing in old map bc int new_var = new_var should throw new_var not declared.
        /////TEMP_unused_code_for_Expressions__VARDEC(input, containingClassMembers); ////XXXXXXXXXXXXXXXXXXXXXXXX fix, would resolve the body of the variable declaration (PExpression object)

        typeCheckVariableDec(input); //check variable declaration, is it valid?

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
                output = VariableDeclarationTypecheck(tempBuiltStor, input.variableDeclarations.get(i));
                combinedVars.putAll(output); //add new vars to combined vars list
                VarStor tempStor = output.get(input.identifier.getTokenString()); //just used to show how to get the VarStor obj
                tempFS.Parameters.add(i, tempStor); //add param to FunctStor object, ordered
                tempFunctionVars.put(input.variableDeclarations.get(i).identifier.getTokenString(), tempStor); //put param in method var storage ///DOES THIS HANDLE < main(int one, int one){} > ??XXXXXXXXXXXX
            }

        } else { //no method params
            System.out.println("Method has no Parameters");
        }

        ////XXXXXXXXXXXXXXXXXXXX need to add parameters to method var list

        HashMap<String, VarStor> methodBodyVars = new HashMap<String, VarStor>(); //store all method vars here
        if (tempFunctionVars != null) { //add params to method vars
            System.out.println("Method Params added to Variable List");
            methodBodyVars.putAll(tempFunctionVars);
        } else {
            System.out.println("Method had no variables, so none added to pre-check variable list");
        }

        if (input.statementList != null) {
            System.out.println("Method Declaration Body: Statement List");
            for (int k = 0; k < input.statementList.size(); k++) { //for all body stmts (PStmt)
                PStatement tempStmtExp = input.statementList.get(k);

                TEMP_unused_code_for_PStmts__PSTATEMENT(tempStmtExp);
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

    public static void TEMP_unused_code_for_PStmts__PSTATEMENT(PStatement tempStmtExp) {
        if (tempStmtExp instanceof PExpressionIdentifierReference) {
            System.out.println("Instance of PExpressionIdentifierReference");
            PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference) tempStmtExp;
            //1 token 1 pexpr
        }
        if (tempStmtExp instanceof PIdentifierReference) {
            System.out.println("Instance of PIdentifierReference");
            PIdentifierReference tempExp = (PIdentifierReference) tempStmtExp;
            //1 token 1 pstmt
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
        }
        if (tempStmtExp instanceof PVariableAssignment) {
            System.out.println("Instance of PVariableAssignment");
            PVariableAssignment tempExp = (PVariableAssignment) tempStmtExp;
            //1 token, 1 pexpr
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

//****************  EXPRESSION AND STATEMENT TYPECHECKER****************//
class ExpressionTypeChecker {
    public Scope classStorage = new Scope();
    private PProgram input;

    public ExpressionTypeChecker(PProgram input) {
        this.input = input;
    }

    public void typeCheck() throws TypeCheckerException {
        for (PClassDeclaration classDeclaration : input.classDeclarationList) {
            for (Object declaration : classDeclaration.declarationList) {
                if (declaration instanceof PVariableDeclaration) {
                    typeCheckVariableDec((PVariableDeclaration) declaration, classStorage);
                } else if (declaration instanceof PStatementFunctionDeclaration) {
                    typeCheckFunction((PStatementFunctionDeclaration) declaration, classStorage);
                }
            }
        }


    }

    private void typeCheckVariableDec(PVariableDeclaration varDec, Scope currentScope) throws TypeCheckerException {
        //add variables to hashmap, dont care if they are repeated that part is handled elsewhere
        currentScope.VariableNames.put(varDec.identifier.getTokenString(), varDec.variableType);
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

    //hehe varAss...
    private void typeCheckVariableAssignment(PVariableAssignment varAss, Scope currentScope) throws TypeCheckerException {
        //similar to typecheck VariableDec, however we have to look in the hashtable for the assignee
        //since we don't declare it here
        Token.TokenType assignment = getExpressionType(varAss.value, currentScope);
        Token assigneeToken = currentScope.VariableNames.get(varAss.identifier.getTokenString());
        if (assigneeToken == null)
            throw new TypeCheckerException(varAss.identifier.getTokenString() + " not declared");
        Token.TokenType assignee = assigneeToken.getType();
        if (assignment == Token.TokenType.KEYWORD_STRING) {//strings types name return as type identifiers rather than KEYWORD_STRING, this if handles that
            if (!assignee.equals("string"))
                throw new TypeCheckerException("TypeCheck Error: Expected " +
                        assignee + " got " + assignment);

        } else if (assignment != assignee) {
            throw new TypeCheckerException("TypeCheck Error: Expected " +
                    assignee + " got " + assignment);
        }
    }

    private void typeCheckIfStatement(PStatementIfStatement ifStatement, Scope currentScope) throws TypeCheckerException {
        Scope ifScope = currentScope.Copy();//if statement needs its own scope, anything declared inside stays inside
        Scope elseScope = currentScope.Copy();
        //check if expression is boolean
        if (getExpressionType(ifStatement.expression, ifScope) != Token.TokenType.KEYWORD_BOOLEAN)
            throw new TypeCheckerException("Expression in IF statement not a Boolean");
        //typecheck elements in if statement
        for (PStatement statement : ifStatement.statementList) {
            typeCheckStatement(statement, ifScope);
        }
        //typecheck elements in else statement
        for (PStatement statement : ifStatement.elseStatementList) {
            typeCheckStatement(statement, elseScope);
        }
    }

    private void typeCheckForStatement(PStatementForStatement forStatement, Scope currentScope) throws TypeCheckerException {
        Scope forScope = currentScope.Copy();//exclusive scope for the For Loop that wont interfere with anything outside
        if (!(forStatement.statement1 instanceof PVariableDeclaration))//make sure first statement is a variable declaration
            throw new TypeCheckerException("First Statement in For Loop Must be a variable declaration");
        typeCheckStatement(forStatement.statement1, forScope); //typecheck variable decleration
        if (getExpressionType(forStatement.expression, forScope) != Token.TokenType.KEYWORD_BOOLEAN) //typecheck continue expression
            throw new TypeCheckerException("For Loop Expression must be of type BOOLEAN");
        //we're going to ignore the third part of the for loop for now.....
        for (PStatement statement : forStatement.statementList) {
            typeCheckStatement(statement, forScope);
        }
    }

    private void typeCheckWhileStatement(PStatementWhileStatement whileStatement, Scope currentScope) throws TypeCheckerException {
        Scope whileScope = currentScope.Copy();
        if (getExpressionType(whileStatement.expression, whileScope) != Token.TokenType.KEYWORD_BOOLEAN)
            throw new TypeCheckerException("While Loop Expression must be of type BOOLEAN");
        for (PStatement statement : whileStatement.statementList) {
            typeCheckStatement(statement, whileScope);
        }
    }

    private void typeCheckFunction(PStatementFunctionDeclaration funcDec, Scope currentScope) throws TypeCheckerException {
        //add method names with their return types to the scope
        currentScope.MethodNames.put(funcDec.identifier.getTokenString(), funcDec.returnType);
        Scope functionStorage = currentScope.Copy();//data within this scope should not affect data outside its scope
        for (PStatement statement : funcDec.statementList) {
            typeCheckStatement(statement, currentScope);
        }
    }

    private void typeCheckStatement(PStatement statement, Scope currentScope) throws TypeCheckerException {
        if (statement instanceof PVariableDeclaration) {
            typeCheckVariableDec((PVariableDeclaration) statement, currentScope);
        }
        if (statement instanceof PVariableAssignment) {
            typeCheckVariableAssignment((PVariableAssignment) statement, currentScope);
        }
        if (statement instanceof PStatementIfStatement) {
            typeCheckIfStatement((PStatementIfStatement) statement, currentScope);
        }
        if (statement instanceof PStatementForStatement) {
            typeCheckForStatement((PStatementForStatement) statement, currentScope);
        }
        if (statement instanceof PStatementWhileStatement) {
            typeCheckWhileStatement((PStatementWhileStatement) statement, currentScope);
        }
    }

    //idea, recursive methodology
    public Token.TokenType getExpressionType(PExpression exp, Scope currentScope) throws TypeCheckerException {
        if (exp instanceof PExpressionAtomNumberLiteral)
            return Token.TokenType.KEYWORD_INT; //Expand here once we have more than just ints
        if (exp instanceof PExpressionAtomStringLiteral)
            return Token.TokenType.KEYWORD_STRING;
        if (exp instanceof PExpressionAtomBooleanLiteral)
            return Token.TokenType.KEYWORD_BOOLEAN; //technically not the "boolean" keyword, but lets use this for now
        if (exp instanceof PExpressionVariable) { //if variable was declared before refer to the hashmap
            return currentScope.VariableNames.get(((PExpressionVariable) exp).variable.getTokenString()).getType();
        }
        if (exp instanceof PExpressionBinOp) {
            //recursivly do both hands of the expressions
            Token.TokenType lhs = getExpressionType(((PExpressionBinOp) exp).lhs, currentScope);
            Token.TokenType rhs = getExpressionType(((PExpressionBinOp) exp).rhs, currentScope);
            if (lhs != rhs) {
                if ((lhs == Token.TokenType.KEYWORD_STRING && rhs == Token.TokenType.KEYWORD_INT) ||
                        (lhs == Token.TokenType.KEYWORD_INT && rhs == Token.TokenType.KEYWORD_STRING))
                    return Token.TokenType.KEYWORD_STRING; //concatinating an integer to a string
                else //anything else must fail
                    throw new TypeCheckerException("TypeCheck Error: Expected " +
                            lhs + " got " + rhs);
            }
            Token.TokenType output = lhs;//at this point we already detirmined lhs and rhs are the same type
            //check if the operator is the right type for the expression
            Token.TokenType operator = ((PExpressionBinOp) exp).operatorToken.getType();
            if (operator == Token.TokenType.SYMBOL_PLUS ||
                    operator == Token.TokenType.SYMBOL_MINUS ||
                    operator == Token.TokenType.SYMBOL_ASTERISK ||
                    operator == Token.TokenType.SYMBOL_SLASH) { //number operations
                if (output == Token.TokenType.KEYWORD_STRING)
                    return Token.TokenType.KEYWORD_STRING;
                if (output != Token.TokenType.KEYWORD_INT)
                    throw new TypeCheckerException("TypCheck Error: Wrong Operator Type");
            } else if (operator == Token.TokenType.SYMBOL_AMPERSAND ||
                    operator == Token.TokenType.SYMBOL_BAR) {
                if (output != Token.TokenType.KEYWORD_BOOLEAN) //at this point we already detirmined lhs and rhs are the same type
                    throw new TypeCheckerException("TypCheck Error: Wrong Operator Type");
            } else if (operator == Token.TokenType.SYMBOL_GREATERTHAN ||
                    operator == Token.TokenType.SYMBOL_GREATERTHANEQUAL ||
                    operator == Token.TokenType.SYMBOL_LESSTHAN ||
                    operator == Token.TokenType.SYMBOL_LESSTHANEQUAL ||
                    operator == Token.TokenType.SYMBOL_DOUBLEEQUALS ||
                    operator == Token.TokenType.SYMBOL_NOTEQUAL) {
                if (output != Token.TokenType.KEYWORD_INT) //in these cases the lhs rhs are ints and the output is boolean
                    throw new TypeCheckerException("TypCheck Error: Wrong Operator Type");
                output = Token.TokenType.KEYWORD_BOOLEAN;
            }
            //if the two sides match just return the type of one of the sides
            return output;
        }
        return null;
    }
}

class Scope {
    HashMap<String, Token> VariableNames; //name, object
    HashMap<String, Token> MethodNames;

    public Scope(HashMap<String, Token> vars, HashMap<String, Token> funct) {
        VariableNames = vars;
        MethodNames = funct;
    }

    public Scope() {
        VariableNames = new HashMap<String, Token>();
        MethodNames = new HashMap<String, Token>();
    }

    public Scope Copy() {
        HashMap<String, Token> copyVariableNames = new HashMap<String, Token>(VariableNames);
        HashMap<String, Token> copyMethodNames = new HashMap<String, Token>(MethodNames);
        Scope scopeCopy = new Scope(copyVariableNames, copyMethodNames);
        return scopeCopy;
    }
}