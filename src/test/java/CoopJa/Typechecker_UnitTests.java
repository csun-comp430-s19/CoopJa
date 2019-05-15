package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.Arrays;


public class Typechecker_UnitTests {

    public void testNewTypeChecker(String input) throws TypeCheckerException, Exception { //updated to contain good and bad test methods
        ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser(); //create MainParser object
        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
        System.out.println();
        Typechecker.TypecheckMain(fooTester); //call typechecker with parsed program obj
    }

    public void goodTest (String foo) throws Exception { //call when the test will succeed
        testNewTypeChecker(foo);
    }

    public void badTest (String foo) { //call when the test will fail, to handle exception properly for testing
        Exception myException = Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
        myException.printStackTrace();
    }

    public void old_testTypecheck(final String input) {
        try {
            ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
            Input<Token> tokenListInput = new TokenParserInput(tokenList);
            MainParser parsers = new MainParser(); //create MainParser object
            PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
            System.out.println();
            Typechecker.TypecheckMain(fooTester); //call typechecker with parsed program obj
        } catch (Exception e) {
            System.err.println("Error detected properly");
            System.err.println(e);
        }
    }

    @Test
    public void testRegularPass() {
        String foo = "public class foo{public int foo4 = 0;}" + //example string to be parsed
                "public class foo6 extends foo{public int foo4 = 1;}" +
                "public class foo2{" +
                //"public int foo3 = 0;" + //duplicate var able to be detected, not inside methods yet
                "public int foo3 = 0;" +
                "public int main(){" +
                "foo.foo4(); " +
                "foo3 = (1 + 9)*5;" +
                "for (int i = 0; i < 9; i = i+1;){" +
                "}" +
                "if (1 == 1){" +
                "int i = 0;" +
                "}" +
                "else{" +
                "int i = 1;" +
                "}" +
                "int i = 2;" +
                "return i;" +
                "}" +
                "}";
        ///x////testTypecheck(foo);
    }

    @Test
    public void testImplicitExtends() { //CHANGE LATER
        String foo = "public class foo extends foo2{public int foo4 = 0;}" +
                "public class foo2 {int fooGood = 0;}";
        ///x////testTypecheck(foo);
    }

    @Test
    public void testProperExtends() {
        String foo = "public class foo {public int foo4 = 0;}" +
                "public class foo2 extends foo {int fooGood = 0;}";
        ////x///testTypecheck(foo);
    }

    @Test
    public void testBadExtends() {
        String foo = "public class foo extends foo3 {public int foo4 = 0;}" +
                "public class foo2 extends foo {int fooGood = 0;}";
        ///x////testTypecheck(foo);
    }

    @Test
    public void testDuplicateVar() {
        String foo = "public class foo{public int foo4 = 0;}" + //example string to be parsed
                "public class foo6 extends foo{public int foo4 = 1;}" +
                "public class foo2{" +
                "public int foo3 = 0;" + //duplicate var able to be detected, not inside methods yet
                "public int foo3 = 0;" +
                "public int main(){" +
                "foo.foo4(); " +
                "foo3 = (1 + 9)*5;" +
                "for (int i = 0; i < 9; i = i+1;){" +
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
        ////x////testTypecheck(foo);
    }

    @Test
    public void testNameCollision() {
        String foo = "public class MainClass {public int foo = 0; public int foo = 0;}";
        ////x///testTypecheck(foo);
    }

    @Test
    public void testValidJavaConvention() { //this is valid Java convention, may or may not change, valid now
        String foo = "public class foo {public int foo = 0;}";
        ///x////testTypecheck(foo);
    }

    @Test
    public void testClassVarCollision() {
        String foo = "public class foo {" +
                "int foo = 0;" +
                "int foo = 0; }";
        ////x////testTypecheck(foo);
    }

    @Test
    public void testBadMethodParamVarname() {
        String foo = "public class example {" +
                "public String cool = \"Cool1\" + \"yeah\";" + ///string
                "public void method1(int one, int two) {" +
                "int three = 1;" +
                "}" +
                "}";
        ////x////testTypecheck(foo);
    }

//    public void testNewTypeChecker(String input) throws TypeCheckerException, Exception{
//            ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
//            Input<Token> tokenListInput = new TokenParserInput(tokenList);
//            MainParser parsers = new MainParser(); //create MainParser object
//            PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
//            System.out.println();
//            Typechecker.TypecheckMain(fooTester); //call typechecker with parsed program obj
//    }

    @Test
    public void testAll() throws Exception{
        String foo = "public class foo{public int foo4;}" +
                "public class foo6 extends foo{public int foo44;}" +
                "public class foo2 {" +//
                "public String foo3;" + //lower case "string" gives error "Class of Variable Type not defined", thinking its an identifier
                "public String foo966;" + //capital "String" gives error "TypeCheck Error: Expected KEYWORD_STRING got KEYWORD_STRING" (that old bug...)
                "public boolean foofi;" +
                "public int foo8;" +
                "public int bar;" +//
                "public int main(){" +
                "foo3 = 1 + \"string thingy\";" +
                "foofi = true || 1 < 2;" +
                "foo8 = 1;" +
                "bar = foo8;" +
                "int screaming = foo.foo4; " +
                "int foo67; " +
                "String foo9 = foo3;" +
                "foo67 = (1 + 9)*5;" +
                "while (foo67 < 60) {" +
                "foo67 = foo67 + 1;"+
                "}" +
                "for (int i = 0; i < 9; i = i+1;){" +
                "foo8 = foo8 + 5;" +
                "}" +
                "if (1 == 1){" +
                "int i = 0;" +
                "}" +
                "else{" +
                "int i = 1;" +
                "}" +
                "int i = 2;" +
                "return i;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodIfStatement() throws Exception { //updated
        String foo = "public class foo2{" +
                "public int main(){" +
                "if (1 == 1){" +
                "}" +
                "else{" +
                "}" +
                "return 1;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadIfStatement() { //updated
        String foo = "public class foo2{" +
                "public int main(){" +
                "if (1 + 1){" +
                "}" +
                "else{" +
                "}" +
                "return;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodWhileStatement() throws Exception { //updated
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( 3 < 5 ) {" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadWhileStatement() { //updated
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( \"Phosphophyllite\" ){" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodForStatement() throws Exception{
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "}" +
                "}" +
                "}";
        testNewTypeChecker(foo);
    }

    @Test
    public void testBadForStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i + 10; i=i+1;){" +
                "}" +
                "}" +
                "}";
        Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
    }

    @Test
    public void testBadScopeWhile(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( true ){" +
                "int i = 1;" +
                "}" +
                "i = 2;" +
                "}" +
                "}";
        Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
    }

    @Test
    public void testBadScopeIfStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "if (1 == 1){" +
                "int i;" +
                "}" +
                "else{" +
                "i = i + 1;" +
                "}" +
                "return;" +
                "}" +
                "}";
        Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
    }

    @Test
    public void testGoodScopeForStatement() throws Exception{
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "i = 3;" +
                "}" +
                "}" +
                "}";
        testNewTypeChecker(foo);
    }

    @Test
    public void testBadScopeForStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "}" +
                "i = 9;" +
                "}" +
                "}";
        Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
    }

    //************ UNIT TESTS originally in file "T_TypeCheck_UnitTests" ******************** (originally called "testTypeChecker()" test function, identitcal to "testTypecheck()")
    @Test
    public void testMethodDoubleDecker() {
        String testProg = "public "+
                "class testProg{"+
                "public int testInt = method(0);"+
                "}";
        ///x////testTypecheck(testProg);
    }

    @Test
    public void testMethod() {
        String testProg = "public "+
                "class testProg{"+
                "public int testInt = method(0);"+
                "}";

        ///x/////testTypecheck(testProg);
    }

    @Test
    public void testMethodSignatureType() {
        String testProg = "public class testProg {"+
                "public int main(int a, int b){"+
                "return 4;"+
                "}"+
                "public boolean b = True;"+
                "public int testInt = main(4,b);"+
                "}";
        //x///x////testTypecheck(testProg);
    }

    @Test
    public void testMethodSignature() {
        String testProg = "public class testProg {"+
                "public int main(int a){"+
                "return 4;"+
                "}"+
                "public int testInt = main();"+
                "}";
        /////////x//x///testTypecheck(testProg);
    }
}

    //************ EXPRESSION AND STATEMENT UNIT TESTS ********************
/*
    public ExpressionTypeChecker createTypechecker(final String input) {
        try {
            ArrayList<Token> tokenList = Token.tokenize(input); //tokenize example string
            Input<Token> tokenListInput = new TokenParserInput(tokenList);
            MainParser parsers = new MainParser(); //create MainParser object
            PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
            System.out.println();
            ExpressionTypeChecker typeChecker = new ExpressionTypeChecker(fooTester);
            return typeChecker;
        } catch (Exception e) {
            System.err.println("Unexpected Parser Error");
            System.err.println(e);
            return null;
        }
    }

    public void testWorkingTypeChecker(ExpressionTypeChecker typeChecker, String testName){
        try {
            typeChecker.typeCheck();
        }
        catch (TypeCheckerException e){
            System.out.println("test failed unexpectadly");
            System.out.println(e);
        }
    }


    @Test
    public void testAll() {
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
                "while (foo67 < 60) {" +
                "foo67 = foo67 + 1;"+
                "}" +
                "for (int i = 0; i < 9; i = i+1;){" +
                "foo8 = foo8 + 5;" +
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
        ExpressionTypeChecker typChecker = createTypechecker(foo);
        testWorkingTypeChecker(typChecker, "testAll");
    }

    @Test
    public  void testGoodIntAssignment(){
        String foo = "public class foo2{" +
                "public int foo3 = (1 + 1) / 2;" +
                "}";
        ExpressionTypeChecker typChecker = createTypechecker(foo);
        testWorkingTypeChecker(typChecker, "testGoodIntAssignment");
    }

    @Test
    public void testBadIntAssignment() {
        String foo = "public class foo2{" +
                "public int foo3 = \"string thingy\";" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        Assertions.assertThrows(TypeCheckerException.class, ()-> {typeChecker.typeCheck();});
    }


    @Test
    public void testBadBoolean() {
        String foo = "public class foo2{" +
                "public boolean foo3;" +
                "public boolean foo4 = foo3 + 1;" +
                "}";

        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        Assertions.assertThrows(TypeCheckerException.class, ()-> {typeChecker.typeCheck();});
    }


    @Test
    public void testGoodBoolean() {
        String foo = "public class foo2{" +
                "public boolean foo= false | true;" +
                "}";
        ExpressionTypeChecker typChecker = createTypechecker(foo);
        testWorkingTypeChecker(typChecker, "testGoodBoolean");
    }


    @Test
    public void testGoodComplexBoolean() {
        String foo = "public class foo2{" +
                "public boolean foofi = (true | 1 < 2) && (1==1+1);" +
                "}";
        ExpressionTypeChecker typChecker = createTypechecker(foo);
        testWorkingTypeChecker(typChecker, "testGoodComplexBoolean");
    }


    @Test
    public void testGoodStringIntConcat() {
        String foo = "public class foo2{" +
                "public string foo3 = \"string thingy\" + 1;" +
                "}";
        ExpressionTypeChecker typChecker = createTypechecker(foo);
        testWorkingTypeChecker(typChecker, "testGoodStringIntConcat");
    }

    @Test
    public void testGoodIfStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "if (1 == 1){" +
                "}" +
                "else{" +
                "}" +
                "return;" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        testWorkingTypeChecker(typeChecker, "testGoodIfStatement");
    }

    @Test
    public void testBadIfStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "if (1 + 1){" +
                "}" +
                "else{" +
                "}" +
                "return;" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        Assertions.assertThrows(TypeCheckerException.class, ()-> {typeChecker.typeCheck();});
    }

    @Test
    public void testGoodWhileStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( 3 < 5 ){" +
                "}" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        testWorkingTypeChecker(typeChecker, "testGoodWhileStatement");
    }

    @Test
    public void testBadWhileStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( \"Phosphophyllite\" ){" +
                "}" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        Assertions.assertThrows(TypeCheckerException.class, ()-> {typeChecker.typeCheck();});
    }

    @Test
    public void testGoodForStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "}" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        testWorkingTypeChecker(typeChecker, "testGoodForStatement");
    }

    @Test
    public void testBadForStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i + 10; i=i+1;){" +
                "}" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        Assertions.assertThrows(TypeCheckerException.class, ()-> {typeChecker.typeCheck();});
    }

    @Test
    public void testBadScopeWhile(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( true ){" +
                "int i = 1;" +
                "}" +
                "i = 2;" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        Assertions.assertThrows(TypeCheckerException.class, ()-> {typeChecker.typeCheck();});
    }

    @Test
    public void testBadScopeIfStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "if (1 == 1){" +
                "int i;" +
                "}" +
                "else{" +
                "i = i + 1;" +
                "}" +
                "return;" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        Assertions.assertThrows(NullPointerException.class, ()-> {typeChecker.typeCheck();});
    }

    @Test
    public void testGoodScopeForStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "i = 3;" +
                "}" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        testWorkingTypeChecker(typeChecker, "testGoodScopeForStatement");
    }

    @Test
    public void testBadScopeForStatement(){
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "}" +
                "i = 9;" +
                "}" +
                "}";
        ExpressionTypeChecker typeChecker = createTypechecker(foo);
        Assertions.assertThrows(TypeCheckerException.class, ()-> {typeChecker.typeCheck();});
    }

    //************ UNIT TESTS originally in file "T_TypeCheck_UnitTests" ******************** (originally called "testTypeChecker()" test function, identitcal to "testTypecheck()")
    @Test
    public void testMethodDoubleDecker() {
        String testProg = "public "+
                "class testProg{"+
                "public int testInt = method(0);"+
                "}";
        testTypecheck(testProg);
    }

    @Test
    public void testMethod() {
        String testProg = "public "+
                "class testProg{"+
                "public int testInt = method(0);"+
                "}";

        testTypecheck(testProg);
    }

    @Test
    public void testMethodSignatureType() {
        String testProg = "public class testProg {"+
                "public int main(int a, int b){"+
                "return 4;"+
                "}"+
                "public boolean b = True;"+
                "public int testInt = main(4,b);"+
                "}";
        testTypecheck(testProg);
    }

    @Test
    public void testMethodSignature() {
        String testProg = "public class testProg {"+
                "public int main(int a){"+
                "return 4;"+
                "}"+
                "public int testInt = main();"+
                "}";
        testTypecheck(testProg);
    }
}
*/