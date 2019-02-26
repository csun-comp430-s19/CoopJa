package main.java.CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class N_ParserExample_UnitTests {
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


}
