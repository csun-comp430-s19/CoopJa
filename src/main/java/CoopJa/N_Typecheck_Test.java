package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.HashMap;

public class N_Typecheck_Test {

    public static HashMap<String,Storage> ClassListAll = new HashMap(); //holds (Class Name, Storage Object (holds ArrayList<String> of names of Variables and Methods for the Class)
    public static String ClassString = "";

    public static void main(String[] args) throws Exception {


        String foo = "public class foo{public int foo4 = 0;}" +
                "public class foo6 extends foo{public int foo4 = 1;}" +
                "public class foo2{" +
                //"public int foo3 = 0;" + //duplicate var able to be detected, not inside methods yet
                "public int foo3 = 0;" +
                "public int main(){" +
                "foo.foo4(); " +
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

        ArrayList<Token> tokenList = Token.tokenize(foo);
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser();
        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow();
        System.out.println();

        ArrayList<PClassDeclaration> classlist = new ArrayList<PClassDeclaration>(1);

        for (int i = 0; i < fooTester.classDeclarationList.size(); i++) { //load classes
            classlist.add(i, fooTester.classDeclarationList.get(i));
            ClassListAll.put(fooTester.classDeclarationList.get(i).identifier.getTokenString(), new Storage());
        }

        System.out.println("Class list loaded");

        for (int i = 0; i < classlist.size(); i++) {
            PClassDeclaration tempClass = classlist.get(i);
            int x = i + 1;
            System.out.println("Current Class: #" + x);

            ClassTypecheck(tempClass);



            ArrayList<PDeclaration> tempDeclar = tempClass.declarationList;
            System.out.println("Class #" + x + " Declarations Amount: " + tempDeclar.size());




            System.out.println("Declarations Begin: ");

            for (int j = 0; j < tempDeclar.size(); j++){

                int y = j + 1;

                if (tempDeclar.get(j) instanceof PVariableDeclaration) { //handle class variable declarations
                    System.out.println("Declaration #" + y + " is instance of PVariableDeclaration");
                    PVariableDeclaration tempVar = (PVariableDeclaration)tempDeclar.get(j);

                    Storage t_S = ClassListAll.get(ClassString); //pull out Storage obj of the current class
                    HashMap<String,VarStor> t_VS = t_S.VariableNames; //pull the vars out of the Storage object

                    t_VS = VariableDeclarationTypecheck(t_VS, tempVar); //call VDT with this list of vars (Scope) and overwrite it with VDT return

                    t_S.VariableNames = t_VS; //replace Storage object var list with updated copy

                    ClassListAll.put(ClassString, t_S); //replace the old Storage obj by adding it back to class hashmap with class string

                }

                if (tempDeclar.get(j) instanceof PStatementFunctionDeclaration) { //TBD...XXXXXXX
                    System.out.println("Declaration #" + y + " is instance of PStatementFunctionDeclaration");
                    PStatementFunctionDeclaration tempFunc = (PStatementFunctionDeclaration)tempDeclar.get(j);
                    if (tempFunc.accessModifier != null) {
                        System.out.println("Declaration Access Modifier Type: " + tempFunc.accessModifier.getType() + " " + tempFunc.accessModifier.getTokenString());
                    } else {
                        System.out.println("Declaration Access Modifier Type: NONE");
                    }
                    System.out.println("Declaration Return Type: " + tempFunc.returnType.getType() + " " + tempFunc.returnType.getTokenString());
                    System.out.println("Declaration Identifier Type: " + tempFunc.identifier.getType() + " " + tempFunc.identifier.getTokenString());
                    System.out.println("Declaration Body: Variable Declarations");
                    if (tempFunc.variableDeclarations != null) {
                        System.out.println("ArrayList<PVariableDeclaration> variableDeclarations STILL NEED TO DO XXXXXXX");
                        System.out.println("done above------------ XXXXXXXX");
                    } else {
                        System.out.println("Declaration ArrayList<PVariableDeclaration> variableDeclarations Empty");
                    }
                    System.out.println("Declaration Body: Statement List");
                    if (tempFunc.statementList != null) {
                        System.out.println("ArrayList<PStatement> statementList STILL NEED TO DO XXXXXXX");
                        System.out.println("NEED TO ADD LOOP TO DO ALL ARRAYLIST OF PSTATEMENTS XXXXXXX");

                        for (int k = 0; k < tempFunc.statementList.size(); k++) {

                            PStatement tempStmtExp = tempFunc.statementList.get(k);

                            TEMP_unused_code_for_PStmts__PSTATEMENT(tempStmtExp);

                        }

                    } else {
                        System.out.println("Declaration ArrayList<PStatement> statementList Empty");
                    }
                }
                System.out.println("End Declaration #" + y);
                System.out.println();
            }

            System.out.println("End of Class #" + x);
            System.out.println();
        }

    } //end Main()

    public static HashMap<String,VarStor> VariableDeclarationTypecheck(HashMap<String,VarStor> map, PVariableDeclaration input) throws Exception { //take in map of all vars declared in scope, and the declaration

        AccessModifierTypecheck(input.accessModifier, false);
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
            //do AUTO stuff later XXXXXXX
            System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
        } else if (input.variableType.getType() == Token.TokenType.IDENTIFIER) { //is the type of the var a Class? (token = IDENTIFIER)
            System.out.println("Variable Declared of a Class");
            if (ClassListAll.containsKey(input.variableType.getTokenString())) { //check all class list for name
                System.out.println("Class Found");
                System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
            } else { //class not declared yet
                throw new Exception("Variable Declaration Error: Class of Variable Type not defined");
            }
        } else {
            throw new Exception("Variable Declaration Error: Variable Type unrecognized");
        }

        if (map.containsKey(input.identifier.getTokenString())) { //check if var already exists in scope
            throw new Exception("Variable Declaration Error: Variable with same name already defined in scope");
        } else { //if not, add it as a new var
            VarStor tempVS = new VarStor(input.variableType, input.accessModifier);
            map.put(input.identifier.getTokenString(), tempVS);
            System.out.println("Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
        }

        TEMP_unused_code_for_Expressions__VARDEC(input); ////XXXXXXXXXXXXXXXXXXXXXXXX fix

        return map;

    }

    public static void ClassTypecheck(PClassDeclaration input) throws Exception {

        AccessModifierTypecheck(input.accessModifier, true);
        System.out.println("Class Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());

        System.out.println("Class Identifier (Name): " + input.identifier.getType() + " " + input.identifier.getTokenString());
        ClassString = input.identifier.getTokenString();

        System.out.print("Class Extends a Class?: ");
        if (input.extendsIdentifier != null) {
            if (ClassListAll.containsKey(input.extendsIdentifier.getTokenString())) {
                System.out.println("yes " + input.extendsIdentifier.getType() + " " + input.extendsIdentifier.getTokenString());
            } else { //class extends class that does not exist (yet)
                throw new Exception("Class Error: Class Extends Class that does not exist");
            }
        } else {
            System.out.println("no");
        }

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

    public static void TEMP_unused_code_for_Expressions__VARDEC(PVariableDeclaration input) {
        System.out.println("Declaration Body: ");
        if (input.assignment != null) {

            if (input.assignment instanceof PExpressionStub) {
                System.out.println("Instance of PExpressionStub");
                PExpressionStub tempExp = (PExpressionStub)input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionBinOp) {
                System.out.println("Instance of PExpressionBinOp");
                PExpressionBinOp tempExp = (PExpressionBinOp)input.assignment;
                //2 pexpressions 1 token
            }
            if (input.assignment instanceof PExpressionIdentifierReference) {
                System.out.println("Instance of PExpressionIdentifierReference");
                PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference)input.assignment;
                //1 token 1 pexpr
            }
            if (input.assignment instanceof PExpressionVariable) {
                System.out.println("Instance of PExpressionVariable");
                PExpressionVariable tempExp = (PExpressionVariable)input.assignment;
                //1 token
            }
            if (input.assignment instanceof PStatementFunctionCall) {
                System.out.println("Instance of PStatementFunctionCall");
                PStatementFunctionCall tempExp = (PStatementFunctionCall)input.assignment;
                //1 Token , 1 ArrayList<PExpression>
            }
            if (input.assignment instanceof PExpressionAtomBooleanLiteral) {
                System.out.println("Instance of PExpressionAtomBooleanLiteral");
                PExpressionAtomBooleanLiteral tempExp = (PExpressionAtomBooleanLiteral)input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionAtomNullLiteral) {
                System.out.println("Instance of PExpressionAtomNullLiteral");
                PExpressionAtomNullLiteral tempExp = (PExpressionAtomNullLiteral)input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionAtomNumberLiteral) {
                System.out.println("Instance of PExpressionAtomNumberLiteral");
                PExpressionAtomNumberLiteral tempExp = (PExpressionAtomNumberLiteral)input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionAtomObjectConstruction) {
                System.out.println("Instance of PExpressionAtomObjectConstruction");
                PExpressionAtomObjectConstruction tempExp = (PExpressionAtomObjectConstruction)input.assignment;
                //1 token
            }
            if (input.assignment instanceof PExpressionAtomStringLiteral) {
                System.out.println("Instance of PExpressionAtomStringLiteral");
                PExpressionAtomStringLiteral tempExp = (PExpressionAtomStringLiteral)input.assignment;
                //1 token
            }
            if (input.assignment instanceof PIdentifierReference) {
                System.out.println("Instance of PIdentifierReference");
                PIdentifierReference tempExp = (PIdentifierReference)input.assignment;
                //1 Token , 1 PStatement
            }
        } else {
            System.out.println("Declaration PExpression assignment Empty");
        }
    }

    public static void TEMP_unused_code_for_PStmts__PSTATEMENT(PStatement tempStmtExp) {
        if (tempStmtExp instanceof PExpressionIdentifierReference) {
            System.out.println("Instance of PExpressionIdentifierReference");
            PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference)tempStmtExp;
            //1 token 1 pexpr
        }
        if (tempStmtExp instanceof PIdentifierReference) {
            System.out.println("Instance of PIdentifierReference");
            PIdentifierReference tempExp = (PIdentifierReference)tempStmtExp;
            //1 token 1 pstmt
        }
        if (tempStmtExp instanceof PStatementBreak) {
            System.out.println("Instance of PStatementBreak");
            PStatementBreak tempExp = (PStatementBreak)tempStmtExp;
            //1 token
        }
        if (tempStmtExp instanceof PStatementForStatement) {
            System.out.println("Instance of PStatementForStatement");
            PStatementForStatement tempExp = (PStatementForStatement)tempStmtExp;
            //1 PStatement ,  1 PExpression,  1 PStatement , 1 ArrayList<PStatement>
        }
        if (tempStmtExp instanceof PStatementFunctionCall) {
            System.out.println("Instance of PStatementFunctionCall");
            PStatementFunctionCall tempExp = (PStatementFunctionCall)tempStmtExp;
            //1 Token , 1 ArrayList<PExpression>
        }
        if (tempStmtExp instanceof PStatementFunctionDeclaration) {
            System.out.println("Instance of PStatementFunctionDeclaration");
            PStatementFunctionDeclaration tempExp = (PStatementFunctionDeclaration)tempStmtExp;
            //handled above
        }
        if (tempStmtExp instanceof PStatementIfStatement) {
            System.out.println("Instance of PStatementIfStatement");
            PStatementIfStatement tempExp = (PStatementIfStatement)tempStmtExp;
            //1 PExpression , 1 ArrayList<PStatement> , 1 ArrayList<PStatement>
        }
        if (tempStmtExp instanceof PStatementPrintln) {
            System.out.println("Instance of PStatementPrintln");
            PStatementPrintln tempExp = (PStatementPrintln)tempStmtExp;
            //1 token
        }
        if (tempStmtExp instanceof PStatementReturn) {
            System.out.println("Instance of PStatementReturn");
            PStatementReturn tempExp = (PStatementReturn)tempStmtExp;
            //1 pexpr
        }
        if (tempStmtExp instanceof PStatementWhileStatement) {
            System.out.println("Instance of PStatementWhileStatement");
            PStatementWhileStatement tempExp = (PStatementWhileStatement)tempStmtExp;
            //1 PExpression , 1 ArrayList<PStatement>
        }
        if (tempStmtExp instanceof PVariableAssignment) {
            System.out.println("Instance of PVariableAssignment");
            PVariableAssignment tempExp = (PVariableAssignment)tempStmtExp;
            //1 token, 1 pexpr
        }
        if (tempStmtExp instanceof PVariableDeclaration) {
            System.out.println("Instance of PVariableDeclaration");
            PVariableDeclaration tempExp = (PVariableDeclaration)tempStmtExp;
            //already handled
        }
    }
}

class Storage {

    HashMap<String,VarStor> VariableNames; //name, object
    HashMap<String,FunctStor> MethodNames;

    public Storage(HashMap<String,VarStor> vars, HashMap<String,FunctStor> funct) {
        VariableNames = vars;
        MethodNames = funct;
    }

    public Storage() {
        VariableNames = new HashMap();
        MethodNames = new HashMap();
    }
}

class VarStor { //stores var info

    Token Type;
    Token AccessModifier;

    public VarStor(Token type_in, Token accessmodifier_in) {
        Type = type_in;
        AccessModifier = accessmodifier_in;
    }
}

class FunctStor { //store method stuff

    Token ReturnType;
    Token AccessModifier;
    String Classname; //name of the class the function is located
    ArrayList<VarStor> Parameters; //ordered list of paramteres stored as VarStor objs
    HashMap<String,VarStor> VariableNames; //stores all method vars declared inside it

    public FunctStor(Token Return_temp, Token AM_temp, String class_temp, ArrayList<VarStor> Params_temp, HashMap<String,VarStor> VN_temp) { //convert to tokens?? XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx
        ReturnType = Return_temp;
        AccessModifier = AM_temp;
        Classname = class_temp;
        Parameters = Params_temp;
        VariableNames = VN_temp;
    }

    public FunctStor() {

    }
}