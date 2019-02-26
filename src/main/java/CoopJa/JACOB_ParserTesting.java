package main.java.CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class JACOB_ParserTesting {
    // NOTE: The parser is not 100% finished
    // There are a lot of unconnected bits and pieces right now
    // The unit tests are mostly to testing for parser *logic*, the parser will definitely evolve as need arises

    // Testing (by order of complexity):
    // Access Mod Testing (see if all 3 access mod tokens are detected) (done)
    // Type Testing  (see if all types are detected) (done)
    // Assignment checking (check if identifiers can be assigned things) (done)
    // Var Declaration Checking (done)
    // Func Declaration Checking

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
