package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.HashMap;

public class M_Typecheck_Test {

    public static void main(String[] args) throws TypeCheckerException {


        String foo = "public class foo{public int foo4 = 0;}" +
                "public class foo6 extends foo{public int foo4 = 1;}" +
                "public class foo2{" +
                "public string foo3 = 1 + \"string thingy\";" +
                "public string foo966;" +
                "public boolean foofi = true | 1 < 2;" +
                "public int foo8 = 1;" +
                "public int bar = foo8;" +
                "public int main(){" +
                "foo.foo4(); " +
                "int foo67; " +
                "string foo9 = foo3;" +
                "foo67 = (1 + 9)*5;" +
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
        MExpressionTypeChecker cTypeChkr = new MExpressionTypeChecker(fooTester);
        cTypeChkr.typeCheck();


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

class MExpressionTypeChecker {
    public Scope classStorage = new Scope();
    private PProgram input;
    public MExpressionTypeChecker(PProgram input){
        this.input = input;
    }

    public void typeCheck() throws TypeCheckerException{
        for (PClassDeclaration classDeclaration : input.classDeclarationList) {
            for (Object declaration: classDeclaration.declarationList){
                if (declaration instanceof PVariableDeclaration){
                    typeCheckVariableDec((PVariableDeclaration)declaration, classStorage);
                }
                else if (declaration instanceof PStatementFunctionDeclaration){
                    typeCheckFunction((PStatementFunctionDeclaration)declaration, classStorage);
                }
            }
        }


    }

    private void typeCheckVariableDec(PVariableDeclaration varDec, Scope currentScope) throws TypeCheckerException{
        //add variables to hashmap, dont care if they are repeated that part is handled elsewhere
        currentScope.VariableNames.put(varDec.identifier.getTokenString(), varDec.variableType);
        if (varDec.assignment != null){ //assuming there is an expression to be checked
            Token.TokenType assignment = getExpressionType(varDec.assignment, currentScope); //BODY
            if (assignment == Token.TokenType.KEYWORD_STRING){//strings types name return as type identifiers rather than KEYWORD_STRING, this if handles that
                if (!varDec.variableType.getTokenString().equals("string"))
                    throw new TypeCheckerException("TypeCheck Error: Expected " +
                        varDec.variableType.getType() + " got " + assignment);

            }
            else if (assignment != varDec.variableType.getType()) {//compare types with assignment
                throw new TypeCheckerException("TypeCheck Error: Expected " +
                        varDec.variableType.getType() + " got " + assignment);
            }
        }
    }

    //hehe varAss...
    private void typeCheckVariableAssignment(PVariableAssignment varAss, Scope currentScope) throws TypeCheckerException{
        //similar to typecheck VariableDec, however we have to look in the hashtable for the assignee
        //since we don't declare it here
        Token.TokenType assignment = getExpressionType(varAss.value, currentScope);
        Token assigneeToken = currentScope.VariableNames.get(varAss.identifier.getTokenString());
        if (assigneeToken == null)
            throw new TypeCheckerException(varAss.identifier.getTokenString() + " not declared");
        Token.TokenType assignee = assigneeToken.getType();
        if (assignment == Token.TokenType.KEYWORD_STRING){//strings types name return as type identifiers rather than KEYWORD_STRING, this if handles that
            if (!assignee.equals("string"))
                throw new TypeCheckerException("TypeCheck Error: Expected " +
                        assignee + " got " + assignment);

        }
        else if (assignment != assignee){
            throw new TypeCheckerException("TypeCheck Error: Expected " +
                        assignee + " got " + assignment);
        }
    }

    private void typeCheckIfStatement (PStatementIfStatement ifStatement, Scope currentScope) throws TypeCheckerException{
        Scope ifScope = currentScope.Copy();//if statement needs its own scope, anything declared inside stays inside
        Scope elseScope = currentScope.Copy();
        //check if expression is boolean
        if (getExpressionType(ifStatement.expression, ifScope) != Token.TokenType.KEYWORD_BOOLEAN)
            throw new TypeCheckerException("Expression in IF statement not a Boolean");
        //typecheck elements in if statement
        for (PStatement statement: ifStatement.statementList){
            typeCheckStatement(statement, ifScope);
        }
        //typecheck elements in else statement
        for (PStatement statement: ifStatement.elseStatementList){
            typeCheckStatement(statement, elseScope);
        }
    }

    private void typeCheckFunction(PStatementFunctionDeclaration funcDec, Scope currentScope) throws TypeCheckerException{
        //add method names with their return types to the scope
        currentScope.MethodNames.put(funcDec.identifier.getTokenString(), funcDec.returnType);
        Scope functionStorage = currentScope.Copy();//data within this scope should not affect data outside its scope
        for (PStatement statement : funcDec.statementList){
            typeCheckStatement(statement, currentScope);
        }
    }

    private void typeCheckStatement(PStatement statement, Scope currentScope) throws TypeCheckerException{
        if (statement instanceof  PVariableDeclaration){
            typeCheckVariableDec((PVariableDeclaration) statement, currentScope);
        }
        if (statement instanceof PVariableAssignment){
            typeCheckVariableAssignment((PVariableAssignment) statement, currentScope);
        }
        if (statement instanceof  PStatementIfStatement){
            typeCheckIfStatement((PStatementIfStatement) statement, currentScope);
        }
    }

    //idea, recursive methodology
    public Token.TokenType getExpressionType(PExpression exp, Scope currentScope) throws TypeCheckerException{
        if (exp instanceof PExpressionAtomNumberLiteral)
            return Token.TokenType.KEYWORD_INT; //Expand here once we have more than just ints
        if (exp instanceof PExpressionAtomStringLiteral)
            return Token.TokenType.KEYWORD_STRING;
        if (exp instanceof PExpressionAtomBooleanLiteral)
            return Token.TokenType.KEYWORD_BOOLEAN; //technically not the "boolean" keyword, but lets use this for now
        if (exp instanceof PExpressionVariable){ //if variable was declared before refer to the hashmap
            return currentScope.VariableNames.get(((PExpressionVariable) exp).variable.getTokenString()).getType();
        }
        if (exp instanceof PExpressionBinOp){
            //recursivly do both hands of the expressions
            Token.TokenType lhs = getExpressionType(((PExpressionBinOp) exp).lhs, currentScope);
            Token.TokenType rhs = getExpressionType(((PExpressionBinOp) exp).rhs, currentScope);
            if (lhs != rhs){
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
                operator == Token.TokenType.SYMBOL_SLASH){ //number operations
                if(output != Token.TokenType.KEYWORD_INT)
                    throw new TypeCheckerException("TypCheck Error: Wrong Operator Type");
            }
            else if (operator == Token.TokenType.SYMBOL_AMPERSAND||
                operator == Token.TokenType.SYMBOL_BAR){
                if(output != Token.TokenType.KEYWORD_BOOLEAN) //at this point we already detirmined lhs and rhs are the same type
                    throw new TypeCheckerException("TypCheck Error: Wrong Operator Type");
            }
            else if (operator == Token.TokenType.SYMBOL_GREATERTHAN ||
                    operator == Token.TokenType.SYMBOL_GREATERTHANEQUAL ||
                    operator == Token.TokenType.SYMBOL_LESSTHAN ||
                    operator == Token.TokenType.SYMBOL_LESSTHANEQUAL ||
                    operator == Token.TokenType.SYMBOL_DOUBLEEQUALS ||
                    operator == Token.TokenType.SYMBOL_NOTEQUAL){
                if(output != Token.TokenType.KEYWORD_INT) //in these cases the lhs rhs are ints and the output is boolean
                    throw new TypeCheckerException("TypCheck Error: Wrong Operator Type");
                output = Token.TokenType.KEYWORD_BOOLEAN;
            }
            //if the two sides match just return the type of one of the sides
            return output;
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
        VariableNames = new HashMap<String,Token>();
        MethodNames = new HashMap<String,Token>();
    }

    public Scope Copy(){
        HashMap<String,Token> copyVariableNames = new HashMap<String, Token>(VariableNames);
        HashMap<String,Token> copyMethodNames = new HashMap<String, Token>(MethodNames);
        Scope scopeCopy = new Scope(copyVariableNames, copyMethodNames);
        return  scopeCopy;
    }
}
