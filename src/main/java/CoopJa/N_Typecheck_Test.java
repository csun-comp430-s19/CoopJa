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
                "public int foo3 = 0;" + //duplicate var able to be detected, not inside methods yet
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



//            System.out.println("Class Access Modifier Type: " + tempClass.accessModifier.getType() + " " + tempClass.accessModifier.getTokenString());
//            System.out.println("Class Identifier (Name): " + tempClass.identifier.getType() + " " + tempClass.identifier.getTokenString());
//            System.out.print("Class Extends a Class?: ");
//            if (tempClass.extendsIdentifier != null) {
//                System.out.println("yes " + tempClass.extendsIdentifier.getType() + " " + tempClass.extendsIdentifier.getTokenString());
//            } else {
//                System.out.println("no");
//            }




            ArrayList<PDeclaration> tempDeclar = tempClass.declarationList;
            System.out.println("Class #" + x + " Declarations Amount: " + tempDeclar.size());




            System.out.println("Declarations Begin: ");

            for (int j = 0; j < tempDeclar.size(); j++){

                int y = j + 1;

                if (tempDeclar.get(j) instanceof PVariableDeclaration) {
                    System.out.println("Declaration #" + y + " is instance of PVariableDeclaration");
                    PVariableDeclaration tempVar = (PVariableDeclaration)tempDeclar.get(j);

                    VariableDeclarationTypecheck(tempVar);



//                    if (tempVar.accessModifier != null) {
//                        System.out.println("Declaration Access Modifier Type: " + tempVar.accessModifier.getType() + " " + tempVar.accessModifier.getTokenString());
//                    } else {
//                        System.out.println("Declaration Access Modifier Type: NONE");
//                    }
//                    System.out.println("Declaration Variable Type: " + tempVar.variableType.getType() + " " + tempVar.variableType.getTokenString());
//                    System.out.println("Declaration Identifier Type: " + tempVar.identifier.getType() + " " + tempVar.identifier.getTokenString());
//                    System.out.println("Declaration Body: ");
//                    if (tempVar.assignment != null) {
//
//                        if (tempVar.assignment instanceof PExpressionStub) {
//                            System.out.println("Instance of PExpressionStub");
//                            PExpressionStub tempExp = (PExpressionStub)tempVar.assignment;
//                            //1 token
//                        }
//                        if (tempVar.assignment instanceof PExpressionBinOp) {
//                            System.out.println("Instance of PExpressionBinOp");
//                            PExpressionBinOp tempExp = (PExpressionBinOp)tempVar.assignment;
//                            //2 pexpressions 1 token
//                        }
//                        if (tempVar.assignment instanceof PExpressionIdentifierReference) {
//                            System.out.println("Instance of PExpressionIdentifierReference");
//                            PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference)tempVar.assignment;
//                            //1 token 1 pexpr
//                        }
//                        if (tempVar.assignment instanceof PExpressionVariable) {
//                            System.out.println("Instance of PExpressionVariable");
//                            PExpressionVariable tempExp = (PExpressionVariable)tempVar.assignment;
//                            //1 token
//                        }
//                        if (tempVar.assignment instanceof PStatementFunctionCall) {
//                            System.out.println("Instance of PStatementFunctionCall");
//                            PStatementFunctionCall tempExp = (PStatementFunctionCall)tempVar.assignment;
//                            //1 Token , 1 ArrayList<PExpression>
//                        }
//                        if (tempVar.assignment instanceof PExpressionAtomBooleanLiteral) {
//                            System.out.println("Instance of PExpressionAtomBooleanLiteral");
//                            PExpressionAtomBooleanLiteral tempExp = (PExpressionAtomBooleanLiteral)tempVar.assignment;
//                            //1 token
//                        }
//                        if (tempVar.assignment instanceof PExpressionAtomNullLiteral) {
//                            System.out.println("Instance of PExpressionAtomNullLiteral");
//                            PExpressionAtomNullLiteral tempExp = (PExpressionAtomNullLiteral)tempVar.assignment;
//                            //1 token
//                        }
//                        if (tempVar.assignment instanceof PExpressionAtomNumberLiteral) {
//                            System.out.println("Instance of PExpressionAtomNumberLiteral");
//                            PExpressionAtomNumberLiteral tempExp = (PExpressionAtomNumberLiteral)tempVar.assignment;
//                            //1 token
//                        }
//                        if (tempVar.assignment instanceof PExpressionAtomObjectConstruction) {
//                            System.out.println("Instance of PExpressionAtomObjectConstruction");
//                            PExpressionAtomObjectConstruction tempExp = (PExpressionAtomObjectConstruction)tempVar.assignment;
//                            //1 token
//                        }
//                        if (tempVar.assignment instanceof PExpressionAtomStringLiteral) {
//                            System.out.println("Instance of PExpressionAtomStringLiteral");
//                            PExpressionAtomStringLiteral tempExp = (PExpressionAtomStringLiteral)tempVar.assignment;
//                            //1 token
//                        }
//                        if (tempVar.assignment instanceof PIdentifierReference) {
//                            System.out.println("Instance of PIdentifierReference");
//                            PIdentifierReference tempExp = (PIdentifierReference)tempVar.assignment;
//                            //1 Token , 1 PStatement
//                        }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//                    } else {
//                        System.out.println("Declaration PExpression assignment Empty");
//                    }
                }

                if (tempDeclar.get(j) instanceof PStatementFunctionDeclaration) {
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

                            System.out.println();

                            if (tempFunc.statementList.get(k) instanceof PExpressionIdentifierReference) {
                                System.out.println("Instance of PExpressionIdentifierReference");
                                PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference)tempFunc.statementList.get(k);
                                //1 token 1 pexpr
                            }
                            if (tempFunc.statementList.get(k) instanceof PIdentifierReference) {
                                System.out.println("Instance of PIdentifierReference");
                                PIdentifierReference tempExp = (PIdentifierReference)tempFunc.statementList.get(k);
                                //1 token 1 pstmt
                            }
                            if (tempFunc.statementList.get(k) instanceof PStatementBreak) {
                                System.out.println("Instance of PStatementBreak");
                                PStatementBreak tempExp = (PStatementBreak)tempFunc.statementList.get(k);
                                //1 token
                            }
                            if (tempFunc.statementList.get(k) instanceof PStatementForStatement) {
                                System.out.println("Instance of PStatementForStatement");
                                PStatementForStatement tempExp = (PStatementForStatement)tempFunc.statementList.get(k);
                                //1 PStatement ,  1 PExpression,  1 PStatement , 1 ArrayList<PStatement>
                            }
                            if (tempFunc.statementList.get(k) instanceof PStatementFunctionCall) {
                                System.out.println("Instance of PStatementFunctionCall");
                                PStatementFunctionCall tempExp = (PStatementFunctionCall)tempFunc.statementList.get(k);
                                //1 Token , 1 ArrayList<PExpression>
                            }
                            if (tempFunc.statementList.get(k) instanceof PStatementFunctionDeclaration) {
                                System.out.println("Instance of PStatementFunctionDeclaration");
                                PStatementFunctionDeclaration tempExp = (PStatementFunctionDeclaration)tempFunc.statementList.get(k);
                                //handled above
                            }
                            if (tempFunc.statementList.get(k) instanceof PStatementIfStatement) {
                                System.out.println("Instance of PStatementIfStatement");
                                PStatementIfStatement tempExp = (PStatementIfStatement)tempFunc.statementList.get(k);
                                //1 PExpression , 1 ArrayList<PStatement> , 1 ArrayList<PStatement>
                            }
                            if (tempFunc.statementList.get(k) instanceof PStatementPrintln) {
                                System.out.println("Instance of PStatementPrintln");
                                PStatementPrintln tempExp = (PStatementPrintln)tempFunc.statementList.get(k);
                                //1 token
                            }
                            if (tempFunc.statementList.get(k) instanceof PStatementReturn) {
                                System.out.println("Instance of PStatementReturn");
                                PStatementReturn tempExp = (PStatementReturn)tempFunc.statementList.get(k);
                                //1 pexpr
                            }
                            if (tempFunc.statementList.get(k) instanceof PStatementWhileStatement) {
                                System.out.println("Instance of PStatementWhileStatement");
                                PStatementWhileStatement tempExp = (PStatementWhileStatement)tempFunc.statementList.get(k);
                                //1 PExpression , 1 ArrayList<PStatement>
                            }
                            if (tempFunc.statementList.get(k) instanceof PVariableAssignment) {
                                System.out.println("Instance of PVariableAssignment");
                                PVariableAssignment tempExp = (PVariableAssignment)tempFunc.statementList.get(k);
                                //1 token, 1 pexpr
                            }
                            if (tempFunc.statementList.get(k) instanceof PVariableDeclaration) {
                                System.out.println("Instance of PVariableDeclaration");
                                PVariableDeclaration tempExp = (PVariableDeclaration)tempFunc.statementList.get(k);
                                //already handled
                            }
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

    } //end Main() //////////////////////////////////////PASS MAP AROUND TO MAINTAIN SCOPE XXXXXXXXXXXXXXXX

    public static void VariableDeclarationTypecheck(PVariableDeclaration input) throws Exception {

        AccessModifierTypecheck(input.accessModifier, false);
        if (input.accessModifier != null) { //this if/else could be removed, mostly for visual output
            System.out.println("Declaration Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());
        } else {
            System.out.println("Declaration Access Modifier Type: NONE");
        }

        ///////////////////////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX check
        //class check (object of class?) if so, token = IDENTIFIER
        //valid primitive types (tokens): KEYWORD_INT,KEYWORD_DOUBLE,KEYWORD_CHAR,KEYWORD_BOOLEAN,KEYWORD_STRING,KEYWORD_AUTO
        System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
        //add name to list
        System.out.println("Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());

        //VarDeclarCheck(input); //check if var has already been declared




        ////////////////MORE TO DO XXXXXXXXXXXXX below

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

/*    public static void VarDeclarCheck(Token type, Token name) throws Exception { //MAKE ANOTHER FOR FUNCTIONS XXXXXXXXXXXXXXXXXXXXX

        Storage tempStor = ClassListAll.get(ClassString); //get list ////XXXXXXXXXXX MERGE with other method already, do all Var Dec stuff there, pass map in to maintain scope

        if (tempStor.VariableNames.containsKey(name)) { //collision
            throw new Exception("Variable Declaration Error: Variable with same name already defined");
        } else { //store new var
            tempStor.VariableNames.put(name, lol);
            ClassListAll.put(ClassString, tempStor); //this updates the key/value in hashmap
        }
    }*/


    public static void ClassTypecheck(PClassDeclaration input) throws TypeCheckerException {

        AccessModifierTypecheck(input.accessModifier, true);
        System.out.println("Class Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());

        System.out.println("Class Identifier (Name): " + input.identifier.getType() + " " + input.identifier.getTokenString());
        ClassString = input.identifier.getTokenString();

        System.out.print("Class Extends a Class?: ");
        if (input.extendsIdentifier != null) {
            if (ClassListAll.containsKey(input.extendsIdentifier.getTokenString())) {
                System.out.println("yes " + input.extendsIdentifier.getType() + " " + input.extendsIdentifier.getTokenString());
            } else { //class extends class that does not exist (yet)
                throw new TypeCheckerException("Class Error: Class Extends Class that does not exist");
            }
        } else {
            System.out.println("no");
        }
    }


    public static void AccessModifierTypecheck(Token input, boolean isClass) throws TypeCheckerException {
        //access modifier required in class, but will fail at parser level if not there
        if (isClass) {
            if (input.getType() == Token.TokenType.KEYWORD_PUBLIC || input.getType() == Token.TokenType.KEYWORD_PRIVATE || input.getType() == Token.TokenType.KEYWORD_PROTECTED) {
                //good
            } else {
                throw new TypeCheckerException("Class Typecheck Error: Class Access Modifier Invalid");
            }
        } else { //not class, access modifier may be blank
            if (input == null || input.getType() == Token.TokenType.KEYWORD_PUBLIC || input.getType() == Token.TokenType.KEYWORD_PRIVATE || input.getType() == Token.TokenType.KEYWORD_PROTECTED) {
                //good
            } else {
                throw new TypeCheckerException("Declaration Error: Access Modifier Invalid");
            }
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

    public VarStor(Token t_in, Token am_in) {
        Type = t_in;
        AccessModifier = am_in;
    }
}

class FunctStor { //store method stuff

    Token ReturnType;
    Token AccessModifier;
    ArrayList<VarStor> Parameters; //ordered list of paramteres stored as VarStor objs
    HashMap<String,VarStor> VariableNames; //stores all method vars declared inside it

    public FunctStor(Token Return_temp, Token AM_temp, ArrayList<VarStor> Params_temp, HashMap<String,VarStor> VN_temp) { //convert to tokens?? XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx
        ReturnType = Return_temp;
        AccessModifier = AM_temp;
        Parameters = Params_temp;
        VariableNames = VN_temp;
    }
}