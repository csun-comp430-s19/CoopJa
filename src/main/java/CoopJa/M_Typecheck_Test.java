package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.HashMap;

public class M_Typecheck_Test {

    public static void main(String[] args) throws TypeCheckerException {


        String foo = "public class foo{public int foo4 = 0;}" +
                "public class foo6 extends foo{public int foo4 = 1;}" +
                "public class foo2{" +
                "public int foo3 = 0 + 1;" +
                "public int bar;" + //to test no assignment
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

        for (PClassDeclaration classDeclaration : fooTester.classDeclarationList) {
            //ClassTypecheckExpression(classDeclaration);
            ClassExpressionTypechecker cTypeChkr = new ClassExpressionTypechecker(classDeclaration);
            cTypeChkr.typeCheck();
        }

    }

/*    public static void ClassTypecheckExpression(PClassDeclaration input) throws TypeCheckerException{
        Storage classStorage = new Storage();
        for (Object declaration: input.declarationList){
            if (declaration instanceof PVariableDeclaration){

            }
            else if (declaration instanceof PStatementFunctionDeclaration){

            }
        }
    }*/
}

class ClassExpressionTypechecker {
    public Scope classStorage = new Scope();
    private PClassDeclaration input;
    public ClassExpressionTypechecker(PClassDeclaration input){
        this.input = input;
    }

    public void typeCheck() throws TypeCheckerException{
        for (Object declaration: input.declarationList){
            if (declaration instanceof PVariableDeclaration){
                typeCheckVariableDec((PVariableDeclaration)declaration);
            }
            else if (declaration instanceof PStatementFunctionDeclaration){
                typeCheckFunction((PStatementFunctionDeclaration)declaration);
            }
        }
    }

    private void typeCheckVariableDec(PVariableDeclaration varDec) throws TypeCheckerException{
        //add variables to hashmap, dont care if they are repeated that part is handled elsewhere
        classStorage.VariableNames.put(varDec.identifier.getTokenString(), varDec.variableType);
        if (varDec.assignment != null){ //assuming there is an expression to be checked
            getType(varDec.assignment);//compare types with assignment
        }

    }

    private void typeCheckFunction(PStatementFunctionDeclaration funcDec) throws TypeCheckerException{
        //add method names with their return types to the scope
        classStorage.MethodNames.put(funcDec.identifier.getTokenString(), funcDec.returnType);
    }

    //idea, recursive methodology
    public Token.TokenType getType(PExpression exp){
        if (exp instanceof PExpressionAtomNumberLiteral)
            return Token.TokenType.NUMBER; //Expand here once we have more than just ints
        if (exp instanceof PExpressionAtomStringLiteral)
            return Token.TokenType.STRING;
        if (exp instanceof PExpressionAtomBooleanLiteral)
            return Token.TokenType.KEYWORD_BOOLEAN; //technically not the "boolean" keyword, but lets use this for now
        if (exp instanceof PExpressionBinOp){

        }
        return null;
    }
}

class Scope{
    HashMap<String,Token> VariableNames; //name, object
    HashMap<String,Token> MethodNames;

    public Scope(HashMap<String,Token> vars, HashMap<String,Token> funct) {
        VariableNames = vars;
        MethodNames = funct;
    }

    public Scope() {
        VariableNames = new HashMap();
        MethodNames = new HashMap();
    }
}
