package main.java.CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class AllParserUnitTests {
    // Nick's
    public void assertParsesIf(final String input, IfStmt expected) {
        try {
            ArrayList<Token> tokenlisttest = Token.tokenize(input);
            IfStmt recieved = N_ParserExample.if_dealwith(tokenlisttest);
            Assertions.assertEquals(recieved.Conditions, expected.Conditions,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
            Assertions.assertEquals(recieved.Statements, expected.Statements,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
            Assertions.assertEquals(recieved.ElseStmts, expected.ElseStmts,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
        } catch (Exception e) {
            Assertions.assertTrue(
                    expected == null,
                    ("N_Parser Unknown ERROR: \"" + input + "\": " + e.getMessage()));
        }
    }

    public void assertParsesWhile(final String input, WhileLoop expected) {
        try {
            ArrayList<Token> tokenlisttest = Token.tokenize(input);
            WhileLoop recieved = N_ParserExample.while_dealwith(tokenlisttest);
            Assertions.assertEquals(recieved.Conditions, expected.Conditions,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
            Assertions.assertEquals(recieved.Statements, expected.Statements,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
        } catch (Exception e) {
            Assertions.assertTrue(
                    expected == null,
                    ("N_Parser Unknown ERROR: \"" + input + "\": " + e.getMessage()));
        }
    }

    public void assertParsesFor(final String input, ForLoop expected) {
        try {
            ArrayList<Token> tokenlisttest = Token.tokenize(input);
            ForLoop recieved = N_ParserExample.for_dealwith(tokenlisttest);
            Assertions.assertEquals(recieved.Condition1, expected.Condition1,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
            Assertions.assertEquals(recieved.Condition2, expected.Condition2,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
            Assertions.assertEquals(recieved.Condition3, expected.Condition3,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
            Assertions.assertEquals(recieved.Statements, expected.Statements,
                    "N_Parser ERROR: Recieved " + recieved + "\n Expected " + expected);
        } catch (Exception e) {
            Assertions.assertTrue(
                    expected == null,
                    ("N_Parser Unknown ERROR: \"" + input + "\": " + e.getMessage()));
        }
    }

    @Test
    public void testIfStmtRegular() {
        assertParsesIf("if(1){2}else{3}", new IfStmt(
                new ArrayList<Token>(Arrays.asList(new Token("1"))),
                new ArrayList<Token>(Arrays.asList(new Token("2"))),
                new ArrayList<Token>(Arrays.asList(new Token("3")))
        ));
    }

    @Test
    public void testIfStmtMoreStuff() {
        assertParsesIf("if(1+1){2}else{3}", new IfStmt(
                new ArrayList<Token>(Arrays.asList(new Token("1"),new Token("+"),new Token("1"))),
                new ArrayList<Token>(Arrays.asList(new Token("2"))),
                new ArrayList<Token>(Arrays.asList(new Token("3")))
        ));
    }

    @Test
    public void testIfStmtParen() {
        assertParsesIf("if((paren)){good}else{notgood}", new IfStmt(
                new ArrayList<Token>(Arrays.asList(new Token("("), new Token("paren"),new Token(")"))),
                new ArrayList<Token>(Arrays.asList(new Token("good"))),
                new ArrayList<Token>(Arrays.asList(new Token("notgood")))
        ));
    }

    @Test
    public void testIfStmtIllegalChar() {
        assertParsesIf("if({}){2}else{3}", null); //bad input does not parse
    }

    @Test
    public void testIfStmtWithoutElse() {
        assertParsesIf("if(1){2}", null); //bad input does not parse
    }

    @Test
    public void testIfStmtParenError() {
        assertParsesIf("if()1){2}else{3}", null); //bad input does not parse
    }

    @Test
    public void testIfStmtCurlyError1() {
        assertParsesIf("if(1)2{2}else{3}", null); //bad input does not parse
    }

    @Test
    public void testIfStmtCurlyError2() {
        assertParsesIf("if(1){2else{3}", null); //bad input does not parse
    }

    @Test
    public void testWhileLoopRegular() {
        assertParsesWhile("while(1){2}", new WhileLoop(
                new ArrayList<Token>(Arrays.asList(new Token("1"))),
                new ArrayList<Token>(Arrays.asList(new Token("2")))
        ));
    }

    @Test
    public void testWhileLoopMoreStuff() {
        assertParsesWhile("while(1+2){2}", new WhileLoop(
                new ArrayList<Token>(Arrays.asList(new Token("1"),new Token("+"),new Token("2"))),
                new ArrayList<Token>(Arrays.asList(new Token("2")))
        ));
    }

    @Test
    public void testWhileLoopVar() {
        assertParsesWhile("while(nicevariable){more}", new WhileLoop(
                new ArrayList<Token>(Arrays.asList(new Token("nicevariable"))),
                new ArrayList<Token>(Arrays.asList(new Token("more")))
        ));
    }

    @Test
    public void testWhileLoopParen() {
        assertParsesWhile("while((paren)){good}", new WhileLoop(
                new ArrayList<Token>(Arrays.asList(new Token("("), new Token("paren"),new Token(")"))),
                new ArrayList<Token>(Arrays.asList(new Token("good")))
        ));
    }

    @Test
    public void testWhileLoopIllegalChar() {
        assertParsesWhile("while({}){2}", null); //bad input does not parse
    }

    @Test
    public void testForLoopRegular() {
        assertParsesFor("for(1;2;3){4}", new ForLoop(
                new ArrayList<Token>(Arrays.asList(new Token("1"))),
                new ArrayList<Token>(Arrays.asList(new Token("2"))),
                new ArrayList<Token>(Arrays.asList(new Token("3"))),
                new ArrayList<Token>(Arrays.asList(new Token("4")))
        ));
    }

    @Test
    public void testForLoopMoreStuff() {
        assertParsesFor("for(1+2;2;3){4}", new ForLoop(
                new ArrayList<Token>(Arrays.asList(new Token("1"),new Token("+"),new Token("2"))),
                new ArrayList<Token>(Arrays.asList(new Token("2"))),
                new ArrayList<Token>(Arrays.asList(new Token("3"))),
                new ArrayList<Token>(Arrays.asList(new Token("4")))
        ));
    }

    @Test
    public void testForLoopVar() {
        assertParsesFor("for(coolvar;2;3){4}", new ForLoop(
                new ArrayList<Token>(Arrays.asList(new Token("coolvar"))),
                new ArrayList<Token>(Arrays.asList(new Token("2"))),
                new ArrayList<Token>(Arrays.asList(new Token("3"))),
                new ArrayList<Token>(Arrays.asList(new Token("4")))
        ));
    }

    @Test
    public void testForLoopEmpty() {
        assertParsesFor("for(;;){4}", null); //bad input does not parse
    }

    @Test
    public void testForLoopTooManySemi() {
        assertParsesFor("for(1;2;3;4){4}", null); //bad input does not parse
    }

    @Test
    public void testForLoopEmpty3rdCond() {
        assertParsesFor("for(1;2;){4}", null); //bad input does not parse
    }

    @Test
    public void testForLoopMissingStmt() {
        assertParsesFor("for(1;2){4}", null); //bad input does not parse
    }

    // Miguel's
    public void assertParses(final String input, Expression expected){
        try{
            final Expression recieved = ExpressionParser.Exp_dealwith(Token.tokenize(input));
            Assertions.assertEquals(recieved, expected,
                    "Expression Parser failed, got: " + recieved + " \n expected : " + expected );
        } catch (Exception e){
            Assertions.assertTrue(
                    expected == null,
                    ("Unexpected Parser failure for input \"" + input + "\": " + e.getMessage()));
        }
    }

    @Test
    public void testParserSingleDigitInteger(){
        assertParses("0", new NumExpression(new Token("0")));
    }

    @Test
    public void testParserSingleVariable(){
        assertParses("x", new VariableExpression(new Token("x")));
    }

    @Test
    public void testParser3TokenExpression(){
        assertParses("1+2", new ArithmeticExpression(
                new NumExpression(new Token("1")),
                new Token("+"),
                new NumExpression(new Token("2"))
        ));
    }

    @Test
    public void testParserArithmeticPrecedence(){
        assertParses("1-2/3", new ArithmeticExpression(
                new NumExpression(new Token("1")),
                new Token("-"),
                new ArithmeticExpression(
                        new NumExpression(new Token("2")),
                        new Token("/"),
                        new NumExpression(new Token("3"))
                )
        ));
    }

    @Test
    public void testParserArithmeticPrecedenceWithParens(){
        assertParses("(1-2)/3", new ArithmeticExpression(
                new ArithmeticExpression(
                        new NumExpression(new Token("1")),
                        new Token("-"),
                        new NumExpression(new Token("2"))
                ),
                new Token("/"),
                new NumExpression(new Token("3"))
        ));
    }

    // Jacob's
    // AccessMod
    @Test
    public void testAccessTypes(){
        // Test the 3 datatype tokens
        // public
        AccessModifier testAccessMod = new AccessModifier(new Token("public"));
        Assertions.assertEquals(testAccessMod.token.getType(), Token.TokenType.KEYWORD_PUBLIC);
        //private
        testAccessMod = new AccessModifier(new Token("private"));
        Assertions.assertEquals(testAccessMod.token.getType(), Token.TokenType.KEYWORD_PRIVATE);
        //protected
        testAccessMod = new AccessModifier(new Token("protected"));
        Assertions.assertEquals(testAccessMod.token.getType(), Token.TokenType.KEYWORD_PROTECTED);
    }

    // Types
    @Test
    public void testTypeTesting(){
        // Test int, double, char, boolean, string, auto, void, some generic identifier (we'll do foo)
        String[] dataTypeStrings = {"int", "double", "char", "boolean", "String", "auto", "void", "foo"};
        Token.TokenType[] dataTypeTokenTypes = {Token.TokenType.KEYWORD_INT, Token.TokenType.KEYWORD_DOUBLE,
                Token.TokenType.KEYWORD_CHAR, Token.TokenType.KEYWORD_BOOLEAN, Token.TokenType.KEYWORD_STRING,
                Token.TokenType.KEYWORD_AUTO, Token.TokenType.KEYWORD_VOID, Token.TokenType.VARIABLENAME};
        for (int i = 0; i < dataTypeStrings.length; i++){
            Assertions.assertEquals(dataTypeTokenTypes[i], new VarType(new Token(dataTypeStrings[i])).token.getType());
        }
    }

    // Assignment checking (check if identifiers can be assigned things)
    @Test
    public void testAssignments(){
        // Test the following things
        // foo = 12412
        // foo = foo2
        String test1 = "foo = 12412";
        String test2 = "foo = foo2";


        // First one
        ArrayList<Token> tokenList = Token.tokenize(test1);
        ParseAssignment testAssignment = DeclarationParser.doParseAssignment(tokenList, 0, tokenList.size()-1);
        // Compare identifier
        Assertions.assertEquals("foo", testAssignment.identifier.token.getTokenString());
        // Compare assignment token type
        Assertions.assertEquals(Token.TokenType.NUMBER, testAssignment.assignment.assignedTokens.get(0).getType());

        // 2nd one (sorry about the boilerplate)
        tokenList = Token.tokenize(test2);
        testAssignment = DeclarationParser.doParseAssignment(tokenList, 0, tokenList.size()-1);
        // Compare identifier
        Assertions.assertEquals("foo", testAssignment.identifier.token.getTokenString());
        // Compare assignment token type
        Assertions.assertEquals(Token.TokenType.VARIABLENAME, testAssignment.assignment.assignedTokens.get(0).getType());
    }

    // Var Declaration Checking
    @Test
    public void testDeclaration(){
        // Testing these strings of ascending complexity
        // int foo
        // public int foo
        // public int foo = 5
        // int foo = 5
        String testString;
        ArrayList<Token> testTokens;
        ParseDeclaration testParseDeclaration;

        // 1
        testString = "int foo";
        testTokens = Token.tokenize(testString);
        testParseDeclaration = DeclarationParser.doParseDeclaration(testTokens, 0, testTokens.size()-1);
        // Access null
        Assertions.assertEquals(null, testParseDeclaration.accessModifier.token);
        // Type string int
        Assertions.assertEquals("int", testParseDeclaration.varType.token.getTokenString());
        // Identifier string foo
        Assertions.assertEquals("foo", testParseDeclaration.parseAssignment.identifier.token.getTokenString());

        //2
        testString = "public int foo";
        testTokens = Token.tokenize(testString);
        testParseDeclaration = DeclarationParser.doParseDeclaration(testTokens, 0, testTokens.size()-1);
        // Access null
        Assertions.assertEquals("public", testParseDeclaration.accessModifier.token.getTokenString());
        // Type string int
        Assertions.assertEquals("int", testParseDeclaration.varType.token.getTokenString());
        // Identifier string foo
        Assertions.assertEquals("foo", testParseDeclaration.parseAssignment.identifier.token.getTokenString());

        //3
        testString = "public int foo = 5";
        testTokens = Token.tokenize(testString);
        testParseDeclaration = DeclarationParser.doParseDeclaration(testTokens, 0, testTokens.size()-1);
        // Access null
        Assertions.assertEquals("public", testParseDeclaration.accessModifier.token.getTokenString());
        // Type string int
        Assertions.assertEquals("int", testParseDeclaration.varType.token.getTokenString());
        // Identifier string foo
        Assertions.assertEquals("foo", testParseDeclaration.parseAssignment.identifier.token.getTokenString());
        // Value of 5
        Assertions.assertEquals("5", testParseDeclaration.parseAssignment.assignment.assignedTokens.get(0).getTokenString());

        //4
        testString = "int foo = 5";
        testTokens = Token.tokenize(testString);
        testParseDeclaration = DeclarationParser.doParseDeclaration(testTokens, 0, testTokens.size()-1);
        // Access null
        Assertions.assertEquals(null, testParseDeclaration.accessModifier.token);
        // Type string int
        Assertions.assertEquals("int", testParseDeclaration.varType.token.getTokenString());
        // Identifier string foo
        Assertions.assertEquals("foo", testParseDeclaration.parseAssignment.identifier.token.getTokenString());
        // Value of 5
        Assertions.assertEquals("5", testParseDeclaration.parseAssignment.assignment.assignedTokens.get(0).getTokenString());
    }
    // Func Declaration Checking
    @Test
    public void testFuncDeclarationCheck(){
        // Testing:
        // public int foo()
        // public int foo(int 1, double 5)

        String testString;
        ArrayList<Token> tokens;
        FunctionDeclaration testFunctionDeclaration;

        // 1
        // Should have a simple declaration with no parameters
        testString = "public int foo()";
        tokens = Token.tokenize(testString);
        testFunctionDeclaration = FunctionDeclaration.doParse(tokens, 0, tokens.size()-1);
        // Check access mod
        Assertions.assertEquals("public",testFunctionDeclaration.functionDeclare.accessModifier.token.getTokenString());
        // Check type
        Assertions.assertEquals("int",testFunctionDeclaration.functionDeclare.varType.token.getTokenString());
        // Check identifier
        Assertions.assertEquals("foo",testFunctionDeclaration.functionDeclare.parseAssignment.identifier.token.getTokenString());
        // Make sure parameter size is 0
        Assertions.assertEquals(0, testFunctionDeclaration.functionParameters.size());

        // 2
        // Should have a declaration similar to before, but with 2 parameters
        testString = "public int foo(int foo1, double foo5)";
        tokens = Token.tokenize(testString);
        testFunctionDeclaration = FunctionDeclaration.doParse(tokens, 0, tokens.size()-1);
        // Check access mod
        Assertions.assertEquals("public",testFunctionDeclaration.functionDeclare.accessModifier.token.getTokenString());
        // Check type
        Assertions.assertEquals("int",testFunctionDeclaration.functionDeclare.varType.token.getTokenString());
        // Check identifier
        Assertions.assertEquals("foo",testFunctionDeclaration.functionDeclare.parseAssignment.identifier.token.getTokenString());
        // Make sure parameter size is 0
        Assertions.assertEquals(2, testFunctionDeclaration.functionParameters.size());
        // 1st paramemter
        Assertions.assertEquals("int", testFunctionDeclaration.functionParameters.get(0).varType.token.getTokenString());
        Assertions.assertEquals("foo1", testFunctionDeclaration.functionParameters.get(0).parseAssignment.identifier.token.getTokenString());
        // 2nd parameter
        Assertions.assertEquals("double", testFunctionDeclaration.functionParameters.get(1).varType.token.getTokenString());
        Assertions.assertEquals("foo5", testFunctionDeclaration.functionParameters.get(1).parseAssignment.identifier.token.getTokenString());
    }
}
