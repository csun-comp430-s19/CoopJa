package main.java.CoopJa;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.parser.Combinators;
import org.typemeta.funcj.parser.*;

import java.util.ArrayList;

import static org.typemeta.funcj.parser.Parser.pure;

public class MainParser {
    private static Functions.Predicate<Token> typePredicate(Token.TokenType type){
        return Functions.Predicate.of((Token t) -> t.getType() == type);
    }

    public static <T> ArrayList<T> IListtoArrayList(IList<T> list){
        ArrayList<T> returnList = new ArrayList<>();
        for (; !list.isEmpty(); list = list.tail()) {
            returnList.add(list.head());
        }
        return returnList;
    }


    // Parser Declarations

    // Single Token Parsers
    // Variable Name
    public Parser<Token, Token> identifierParser;
    // Objects
    //public Parser<Token, Token> objectNameParser;   // Might be redundant
    // All datatypes keywords
    public Parser<Token, Token> varTypeParser;
    public Parser<Token,Token> varObjTypeParser;

    // Access Types
    public Parser<Token, Token> accessTypeParser;

    // All operators
    public Parser<Token, Token> operatorParser;

    // Semicolon
    public Parser<Token, Token> semicolonParser;

    // Colon
    public Parser<Token, Token> commaParser;

    // Parenthesis
    public Parser<Token, Token> leftParenParser;
    public Parser<Token, Token> rightParenParser;

    // More complex parsers
    // Expressions
    // Main Expression
    public Parser <Token, PExpression> expressionParser;
    public Ref <Token, PExpression> expressionRef = Parser.ref();


    // STMTs (statements)
    // Main Statement
    public Parser <Token, PStatement> statementParser;
    public Ref <Token, PStatement> statementRef = Parser.ref();

    // Variable Assignment
    public Parser <Token, PVariableAssignment> variableAssignmentParser;
    // Variable Declaration
    public Parser <Token, PStatement> variableDeclarationParserTest1;

    // Identifier Statement Parse Decision Tree
    public Parser<Token, Functions.F2<Token, Token, PStatement>> identifierDeclarationDisambiguator;
    public Parser<Token, PStatement> identifierDecisionTree;

    // Access Type Variable Declaration Decision Tree
    public Parser<Token, PStatement> accessTypeDecisionTree;

    // VarType Identifier etc
    public Parser<Token, PStatement> varTypeDecisionTree;

    // Statement list builder, parses out as many statements as it can find beween { and }
    public Ref<Token, ArrayList<PStatement>> statementListParser = Parser.ref();

    // Simple break statement
    public Parser<Token, PStatementBreak> breakStatementParser;

    // Return statement /w possible return expression
    public Parser<Token, PStatementReturn> returnStatementParser;

    // println() statement
    public Parser<Token, PStatementPrintln> printlnParser;

    // if statement parser
    public Ref<Token, PStatementIfStatement> ifStatementParser = Parser.ref();
    public Ref<Token, PStatementWhileStatement> whileStatementParser = Parser.ref();
    public Ref<Token, PStatementForStatement> forStatementParser = Parser.ref();

    // Class Declaration parser
    public Parser<Token, PClassDeclaration> classDeclarationParser;

    


    // TODO: Expression Crap
    // Parser for parsing out an expression into a special list
    public Parser<Token, ArrayList<PExpressionParserElement>> expressionLargeParser;
    public Parser<Token, PExpressionParserElement> expressionOperatorParser;
    public Parser<Token, PExpressionParserElement> expressionAtomParser;




    private void initParsers(){
        // Parsers
        // Single Token Parsers
        // Identifier
        identifierParser = Combinators.satisfy("Identifier", typePredicate(Token.TokenType.IDENTIFIER));
        //objectNameParser = Combinators.satisfy("Object Name", typePredicate(Token.TokenType.OBJECTNAME));

        // All datatypes keywords
        varTypeParser = Combinators.choice(
                Combinators.satisfy("int type", typePredicate(Token.TokenType.KEYWORD_INT)),
                Combinators.satisfy("double type", typePredicate(Token.TokenType.KEYWORD_DOUBLE)),
                Combinators.satisfy("char type", typePredicate(Token.TokenType.KEYWORD_CHAR)),
                Combinators.satisfy("boolean type", typePredicate(Token.TokenType.KEYWORD_BOOLEAN)),
                Combinators.satisfy("string type", typePredicate(Token.TokenType.KEYWORD_STRING)),
                Combinators.satisfy("auto type", typePredicate(Token.TokenType.KEYWORD_AUTO))
                //Combinators.satisfy("object type", typePredicate(Token.TokenType.OBJECTNAME))
        );

        varObjTypeParser = varTypeParser.or(identifierParser);

        // Access Types
        accessTypeParser = Combinators.choice(
                Combinators.satisfy("public", typePredicate(Token.TokenType.KEYWORD_PUBLIC)),
                Combinators.satisfy("private", typePredicate(Token.TokenType.KEYWORD_PRIVATE)),
                Combinators.satisfy("protected", typePredicate(Token.TokenType.KEYWORD_PROTECTED))
        );

        // All operators
        operatorParser = Combinators.choice(
                Combinators.satisfy("plus", typePredicate(Token.TokenType.SYMBOL_PLUS)),
                Combinators.satisfy("minus", typePredicate(Token.TokenType.SYMBOL_MINUS)),
                Combinators.satisfy("multiply", typePredicate(Token.TokenType.SYMBOL_ASTERISK)),
                Combinators.satisfy("divide", typePredicate(Token.TokenType.SYMBOL_SLASH)),
                Combinators.satisfy("less than", typePredicate(Token.TokenType.SYMBOL_LESSTHAN)),
                Combinators.satisfy("greater than", typePredicate(Token.TokenType.SYMBOL_GREATERTHAN)),
                Combinators.satisfy("less than or equal", typePredicate(Token.TokenType.SYMBOL_LESSTHANEQUAL)),
                Combinators.satisfy("greater than or equal", typePredicate(Token.TokenType.SYMBOL_GREATERTHANEQUAL)),
                Combinators.satisfy("is equal", typePredicate(Token.TokenType.SYMBOL_DOUBLEEQUALS)),
                Combinators.satisfy("not equal", typePredicate(Token.TokenType.SYMBOL_NOTEQUAL)),
                Combinators.satisfy("logical or", typePredicate(Token.TokenType.SYMBOL_DOUBLEBAR)),
                Combinators.satisfy("bitwise or", typePredicate(Token.TokenType.SYMBOL_BAR)),
                Combinators.satisfy("logical and", typePredicate(Token.TokenType.SYMBOL_DOUBLEAMPERSAND)),
                Combinators.satisfy("bitwise and", typePredicate(Token.TokenType.SYMBOL_AMPERSAND)),
                Combinators.satisfy("xor", typePredicate(Token.TokenType.SYMBOL_CARET)),
                Combinators.satisfy("shift right", typePredicate(Token.TokenType.SYMBOL_SHIFTRIGHT)),
                Combinators.satisfy("shift left", typePredicate(Token.TokenType.SYMBOL_SHIFTLEFT))
                //Combinators.satisfy("increment", typePredicate(Token.TokenType.SYMBOL_DOUBLEPLUS)),
                //Combinators.satisfy("decrement", typePredicate(Token.TokenType.SYMBOL_DOUBLEMINUS))
        );

        // Semicolon
        semicolonParser = Combinators.satisfy("Semicolon", typePredicate(Token.TokenType.SYMBOL_SEMICOLON));

        // Comma
        commaParser = Combinators.satisfy("Semicolon", typePredicate(Token.TokenType.SYMBOL_COMMA));

        // Parenthesis parser
        leftParenParser = Combinators.satisfy("paren left", typePredicate(Token.TokenType.SYMBOL_LEFTPAREN));
        rightParenParser = Combinators.satisfy("paren right", typePredicate(Token.TokenType.SYMBOL_RIGHTPAREN));


        // Pratically everything below this is dealing with difficult statements in some way
        // Variable Assignment
        // ident = expression
        variableAssignmentParser = identifierParser
                .andL(Combinators.satisfy("Equals", typePredicate(Token.TokenType.SYMBOL_EQUALS)))
                .and(expressionRef)
                .map(a -> b -> new PVariableAssignment(a, b));

        // Variable Declaration
        // accessMod type ident = expression
        // accessMod type ident
        // type ident = expression
        // type ident
        // This method doesn't work because of confusing parser logic!
        /*variableDeclarationParserTest1 = Combinators.choice(
                // 1
                accessTypeParser.and(varTypeParser).and(identifierParser)
                        .andL(Combinators.satisfy("Equals", typePredicate(Token.TokenType.SYMBOL_EQUALS)))
                        .and(expressionRef).map(a -> b -> c -> d -> new PVariableDeclaration(a, b, c, d)),
                // 2
                accessTypeParser.and(varTypeParser).and(identifierParser).map(a -> b -> c -> new PVariableDeclaration(a,b,c,null)),
                // 3
                varTypeParser.and(identifierParser)
                        .andL(Combinators.satisfy("Equals", typePredicate(Token.TokenType.SYMBOL_EQUALS)))
                        .and(expressionRef).map(a -> b -> c -> new PVariableDeclaration(null, a, b, c)),
                // 4
                varTypeParser.and(identifierParser).map(a -> b -> new PVariableDeclaration(null, a, b, null))
        );*/

        /*variableDeclarationParserTest1 = Combinators.choice(
                // 1 & 2
                accessTypeParser.and(varTypeParser).and(identifierParser)
                        .and((Combinators.satisfy("Equals", typePredicate(Token.TokenType.SYMBOL_EQUALS))
                                .andR(expressionRef)).or(pure(null))).map(a -> b -> c -> d -> new PVariableDeclaration(a, b, c, d)),
                // 3 & 4
                varTypeParser.and(identifierParser)
                        .and((Combinators.satisfy("Equals", typePredicate(Token.TokenType.SYMBOL_EQUALS))
                                .andR(expressionRef)).or(pure(null))).map(b -> c -> d -> new PVariableDeclaration(null, b, c, d))
        );*/

        // Parses assignment statements starting from '='
        Parser<Token, PExpression> assignmentParser = (Combinators.satisfy("Equals", typePredicate(Token.TokenType.SYMBOL_EQUALS))
                .andR(expressionRef)).or(pure(null));

        // Mostly a test, not actually used
        /*variableDeclarationParserTest1 = Combinators.choice(
                // 1 & 2
                accessTypeParser.and(varTypeParser).and(identifierParser)
                        .and(assignmentParser).map(a -> b -> c -> d -> new PVariableDeclaration(a, b, c, d)),
                // 3 & 4
                varTypeParser.and(identifierParser)
                        .and(assignmentParser).map(b -> c -> d -> new PVariableDeclaration(null, b, c, d))
        );*/
        // TODO: Parses between an identifier with different declaration types: declaring with an optional expression, declaring a function, or just doing nothing (which is sometimes valid)
        // Starts with an identifier, then either an =, a (, or return null
        identifierDeclarationDisambiguator = identifierParser.and(Combinators.choice(
                // Should handle possible function declaration here
                assignmentParser.map(a -> (Functions.F3<Token, Token, Token, PVariableDeclaration>)(x,y,z) -> new PVariableDeclaration(x, y, z, a)),
                // I'm sorry if you somehow made it far and THIS is what finally loses you
                leftParenParser.andR(varObjTypeParser.and(identifierParser).map(a -> b -> new PVariableDeclaration(null, a, b, null)).sepBy(commaParser))
                        .andL(rightParenParser).and(statementListParser)
                        .map(a -> b -> (Functions.F3<Token, Token, Token, PStatementFunctionDeclaration>)(x,y,z) -> new PStatementFunctionDeclaration(x, y, z, IListtoArrayList(a), b))  // Function declaration
                //-> (Functions.F3<Token, Token, Token, PStatementFunctionDeclaration>)(x,y,z) -> new PStatementFunctionDeclaration(null, null, null, null, null)
            )
        ).map(a -> b -> (Functions.F2<Token, Token, PStatement>)(x, y) -> b.apply(x,y,a));

        // TODO: Parses any "statement" beginning with an "access modifier", which is always a declaration
        // Begins with access type and then a datatype
        accessTypeDecisionTree = accessTypeParser.and(varObjTypeParser).and(
                identifierDeclarationDisambiguator
        ).map(a -> b -> c -> c.apply(a,b)); // a = access type token, b = datatype token, c = lambda expression to apply these to

        // TODO:  Parses any "statement" beginning with an identifier
        identifierDecisionTree = identifierParser.and(Combinators.choice(
                //identifierParser.and(assignmentParser).map(c -> d -> (Functions.F<Token, PVariableDeclaration>)(x) -> new PVariableDeclaration(null, x, c, d)), // Declaration of an object instance
                identifierDeclarationDisambiguator.map(a -> (Functions.F<Token, PStatement>)(x) -> a.apply(null,x)),   // Declaration of an object or a function, no access modifier

                assignmentParser.map(a -> (Functions.F<Token, PVariableAssignment>)(x) -> new PVariableAssignment(x,a)),   // Assigning an identifier to a value

                leftParenParser.andR(expressionRef.sepBy(commaParser)).andL(rightParenParser).map(a -> (Functions.F<Token, PStatement>)(x) -> new PStatementFunctionCall(x, IListtoArrayList(a)) ),  // Function call

                Combinators.fail()  // Ths is meant to check for a function call, but right now it's a stub
                )
        ).map(a -> b -> b.apply(a));

        varTypeDecisionTree = varTypeParser.and(
                identifierDeclarationDisambiguator.map(a -> (Functions.F<Token, PStatement>)(x) -> a.apply(null,x))
        ).map(a -> b -> b.apply(a));

        // basic break statement
        breakStatementParser = Combinators.satisfy("break token", typePredicate(Token.TokenType.KEYWORD_BREAK)).map(a -> new PStatementBreak(a));

        // Return statement
        returnStatementParser = Combinators.satisfy("Return token", typePredicate(Token.TokenType.KEYWORD_RETURN)).andR(expressionRef.or(pure(null))).map(a -> new PStatementReturn(a));

        // println parser
        printlnParser = Combinators.satisfy("println token", typePredicate(Token.TokenType.KEYWORD_PRINTLN))
                .andR(leftParenParser)
                .andR(Combinators.satisfy("string", typePredicate(Token.TokenType.STRING)))
                .andL(rightParenParser)
                .map(a -> new PStatementPrintln(a));

        // Hopefully this stuff is less difficult
        // General Statements
        // Will essentially find the first token in a statement and work from there
        /*statementParser = Combinators.choice(
                accessTypeDecisionTree.andL(semicolonParser),   // Tokens beginning with access
                identifierDecisionTree.andL(semicolonParser),   // Tokens beginning with an identifier
                varTypeDecisionTree.andL(semicolonParser),
                ifStatementParser,
                whileStatementParser,
                forStatementParser
        );   // Stub */

        statementParser = Combinators.choice(
                accessTypeDecisionTree.andL(semicolonParser),   // Tokens beginning with access
                identifierDecisionTree.andL(semicolonParser),   // Tokens beginning with an identifier
                varTypeDecisionTree.andL(semicolonParser),
                ifStatementParser,
                whileStatementParser,
                forStatementParser
        ).or(Combinators.choice(
                breakStatementParser.andL(semicolonParser),
                returnStatementParser.andL(semicolonParser),
                printlnParser.andL(semicolonParser)
        ));


        // Statement list builder
        // { stmt* }
        statementListParser.set(Combinators.satisfy("Curly Open", typePredicate(Token.TokenType.SYMBOL_LEFTCURLY))
                .andR(statementParser.manyTill(Combinators.satisfy("Curly Close", typePredicate(Token.TokenType.SYMBOL_RIGHTCURLY))))
                .map(MainParser::IListtoArrayList));

        // If statement
        ifStatementParser.set(Combinators.satisfy("if token", typePredicate(Token.TokenType.KEYWORD_IF))
                .andR(leftParenParser)
                .andR(expressionRef)
                .andL(rightParenParser)
                .and(statementListParser)
                .map(a -> b -> new PStatementIfStatement(a, b)));

        // While statement (literally a boiler plate of the above, but I don't want to create another intermediary parser so whatever)
        whileStatementParser.set(Combinators.satisfy("while token", typePredicate(Token.TokenType.KEYWORD_WHILE))
                .andR(leftParenParser)
                .andR(expressionRef)
                .andL(rightParenParser)
                .and(statementListParser)
                .map(a -> b -> new PStatementWhileStatement(a, b)));

        // For statement
        forStatementParser.set(Combinators.satisfy("For token", typePredicate(Token.TokenType.KEYWORD_FOR))
                .andR(leftParenParser)
                // Let's not screw this one up
                // java for loops are allowed to keep some parts blank, but let's not assume that our language
                .andR(statementParser)
                .and(expressionRef).andL(semicolonParser)
                .and(statementParser)
                .andL(rightParenParser)
                .and(statementListParser)
                .map (a -> b -> c -> d -> new PStatementForStatement(a, b, c, d)));

        // Class declaration parser
        classDeclarationParser = accessTypeParser.
                andL(Combinators.satisfy("class token", typePredicate(Token.TokenType.KEYWORD_CLASS)))
                .and(identifierParser)
                .map(a -> b -> new PClassDeclaration(a, b, null));

        // Expression Crap
        expressionOperatorParser = operatorParser.map(a -> new PExpressionOperator(a));
        expressionAtomParser = Combinators.satisfy("Number", typePredicate(Token.TokenType.NUMBER)).map(PExpressionAtomLiterals::new);

        //expressionLargeParser = expressionAtomParser.sepBy(expressionOperatorParser).map(MainParser::IListtoArrayList);
        expressionLargeParser = expressionAtomParser.or(expressionOperatorParser).many().map(MainParser::IListtoArrayList);

        // General expressions
        expressionParser = Combinators.satisfy("Number", typePredicate(Token.TokenType.NUMBER)).map(PExpressionStub::new);  // Stub

        //statementParser = Combinators.fail();   // Stub

        // Establish ALL references now (?)
        expressionRef.set(expressionParser);
        statementRef.set(statementParser);

    }


    public MainParser(){
        initParsers();
    }



    // Not very good testing main class
    public static void main(String[] args){
        //String foo = "for(i = 0; 1; i = 1;){public int foo = 5; int foo2 = 6; public int foo3; int foo4; foo4 = 5;}";
        String foo = "public foo3 foo(int x, char y, foo z){int foo2 = 0;};";
        ArrayList<Token> tokenList = Token.tokenize(foo);
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser();

        // Testing bullshit
        int MainParser = 0;
        MainParser j = null;
        MainParser = 0;

        //PVariableDeclaration fooDeclaration = (PVariableDeclaration)parsers.statementParser.apply(tokenListInput).getOrThrow();
        PStatement fooTester = parsers.statementParser.parse(tokenListInput).getOrThrow();
    }
}
