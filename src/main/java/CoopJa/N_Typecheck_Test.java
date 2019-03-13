package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

public class N_Typecheck_Test {

    public static void main(String[] args){
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
                "if (1){" +
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

        System.out.println("");

        ArrayList<PClassDeclaration> classlist = new ArrayList<PClassDeclaration>(1);

        for (int i = 0; i < fooTester.classDeclarationList.size(); i++) {
            classlist.add(i, fooTester.classDeclarationList.get(i));
        }

        System.out.println("Class list loaded");

        for (int i = 0; i < classlist.size(); i++) {
            PClassDeclaration tempClass = classlist.get(i);
            int x = i + 1;
            System.out.println("Current Class: #" + x);
            System.out.println("Class Access Modifier Type: " + tempClass.accessModifier.getType() + " " + tempClass.accessModifier.getTokenString());
            System.out.println("Class Identifier Type: " + tempClass.identifier.getType() + " " + tempClass.identifier.getTokenString());
            System.out.print("Class Extends a Class?: ");
            if (tempClass.extendsIdentifier != null) {
                System.out.println("yes " + tempClass.extendsIdentifier.getType() + " " + tempClass.extendsIdentifier.getTokenString());
            } else {
                System.out.println("no");
            }

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
                        System.out.println("PExpression assignment STILL NEED TO DO XXXXXXX");
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
                    } else {
                        System.out.println("Declaration ArrayList<PVariableDeclaration> variableDeclarations Empty");
                    }
                    System.out.println("Declaration Body: Statement List");
                    if (tempFunc.statementList != null) {
                        System.out.println("ArrayList<PStatement> statementList STILL NEED TO DO XXXXXXX");
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

    }

}
