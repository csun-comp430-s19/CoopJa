package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class N_Typecheck_Test {

    public static HashMap<String,Storage> ClassListAll = new HashMap(); //holds (Class Name, Storage Object (holds ArrayList<String> of names of Variables and Methods for the Class)

    public static void main(String[] args) throws Exception {


        String foo = "public class foo{public int foo4 = 0;}" +
                "public class foo6 extends foo{public int foo4 = 1;}" +
                "public class foo2{" +
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

        for (int i = 0; i < fooTester.classDeclarationList.size(); i++) {
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
                    if (tempVar.accessModifier != null) {
                        System.out.println("Declaration Access Modifier Type: " + tempVar.accessModifier.getType() + " " + tempVar.accessModifier.getTokenString());
                    } else {
                        System.out.println("Declaration Access Modifier Type: NONE");
                    }
                    System.out.println("Declaration Variable Type: " + tempVar.variableType.getType() + " " + tempVar.variableType.getTokenString());
                    System.out.println("Declaration Identifier Type: " + tempVar.identifier.getType() + " " + tempVar.identifier.getTokenString());
                    System.out.println("Declaration Body: ");
                    if (tempVar.assignment != null) {

                        if (tempVar.assignment instanceof PExpressionStub) {
                            System.out.println("Instance of PExpressionStub");
                            PExpressionStub tempExp = (PExpressionStub)tempVar.assignment;
                            //1 token
                        }
                        if (tempVar.assignment instanceof PExpressionBinOp) {
                            System.out.println("Instance of PExpressionBinOp");
                            PExpressionBinOp tempExp = (PExpressionBinOp)tempVar.assignment;
                            //2 pexpressions 1 token
                        }
                        if (tempVar.assignment instanceof PExpressionIdentifierReference) {
                            System.out.println("Instance of PExpressionIdentifierReference");
                            PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference)tempVar.assignment;
                            //1 token 1 pexpr
                        }
                        if (tempVar.assignment instanceof PExpressionVariable) {
                            System.out.println("Instance of PExpressionVariable");
                            PExpressionVariable tempExp = (PExpressionVariable)tempVar.assignment;
                            //1 token
                        }
                        if (tempVar.assignment instanceof PStatementFunctionCall) {
                            System.out.println("Instance of PStatementFunctionCall");
                            PStatementFunctionCall tempExp = (PStatementFunctionCall)tempVar.assignment;
                            //1 Token , 1 ArrayList<PExpression>
                        }
                        if (tempVar.assignment instanceof PExpressionAtomBooleanLiteral) {
                            System.out.println("Instance of PExpressionAtomBooleanLiteral");
                            PExpressionAtomBooleanLiteral tempExp = (PExpressionAtomBooleanLiteral)tempVar.assignment;
                            //1 token
                        }
                        if (tempVar.assignment instanceof PExpressionAtomNullLiteral) {
                            System.out.println("Instance of PExpressionAtomNullLiteral");
                            PExpressionAtomNullLiteral tempExp = (PExpressionAtomNullLiteral)tempVar.assignment;
                            //1 token
                        }
                        if (tempVar.assignment instanceof PExpressionAtomNumberLiteral) {
                            System.out.println("Instance of PExpressionAtomNumberLiteral");
                            PExpressionAtomNumberLiteral tempExp = (PExpressionAtomNumberLiteral)tempVar.assignment;
                            //1 token
                        }
                        if (tempVar.assignment instanceof PExpressionAtomObjectConstruction) {
                            System.out.println("Instance of PExpressionAtomObjectConstruction");
                            PExpressionAtomObjectConstruction tempExp = (PExpressionAtomObjectConstruction)tempVar.assignment;
                            //1 token
                        }
                        if (tempVar.assignment instanceof PExpressionAtomStringLiteral) {
                            System.out.println("Instance of PExpressionAtomStringLiteral");
                            PExpressionAtomStringLiteral tempExp = (PExpressionAtomStringLiteral)tempVar.assignment;
                            //1 token
                        }
                        if (tempVar.assignment instanceof PIdentifierReference) {
                            System.out.println("Instance of PIdentifierReference");
                            PIdentifierReference tempExp = (PIdentifierReference)tempVar.assignment;
                            //1 Token , 1 PStatement
                        }

















                    } else {
                        System.out.println("Declaration PExpression assignment Empty");
                    }
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

    } //end Main()

    public static void ClassTypecheck(PClassDeclaration input) throws Exception {

        AccessModifierTypecheck(input.accessModifier, true); //not really useful here, but will be later, used to cause exception if error
        System.out.println("Class Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());

        System.out.println("Class Identifier (Name): " + input.identifier.getType() + " " + input.identifier.getTokenString());

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
        //access modifier required in class, but will fail at parser level
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

}

class Storage {

    HashMap<String,String> VariableNames;
    HashMap<String,String> MethodNames;

    public Storage(HashMap<String,String> vars, HashMap<String,String> funct) {
        VariableNames = vars;
        MethodNames = funct;
    }

    public Storage() {
        VariableNames = new HashMap();
        MethodNames = new HashMap();
    }
}
