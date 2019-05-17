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
        Typechecker tempTypeC = new Typechecker(); //create typechecker object
        tempTypeC.TypecheckMain(fooTester); //call typechecker with pprogram obj
        //note, when typechecker was static, a fix for the unit tests persisting was to add the line:
        //java.lang.System.gc();
        //which forces java's garbage collection to run
    }

    public void goodTest (String foo) throws Exception { //call when the test will succeed
        testNewTypeChecker(foo);
    }

    public void badTest (String foo) { //call when the test will fail, to handle exception properly for testing
        Exception myException = Assertions.assertThrows(TypeCheckerException.class, ()-> {testNewTypeChecker(foo);});
        myException.printStackTrace();
    }

    @Test
    public void testGoodAutoLarge() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "auto k;" +
                "auto p;" +
                "auto m;" +
                "auto n;" +
                "public void main() {" +
                "auto localvar = 7;" +
                "i = 0;" +
                "j = true;" +
                "k = \"Hello!\";" +
                "p = 15 / 3;" +
                "m = 1 < 2;" +
                "n = 99;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodAutotoAuto() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "public void main() {" +
                "i = 0;" +
                "j = i;" + //auto j given assignment (what was) auto i
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodAutoMultiMethod() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "public void main2() {" +
                "j = 9;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadAutotoAuto() {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "public void main() {" +
                "i = j;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodAutoMultiClass() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class two {" +
                "auto j;" +
                "public void main() {" +
                "j = 88;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadAutoNotResolvedMulti() {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "auto k;" +
                "public void main() {" +
                "i = 0;" +
                "i = i + 1;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadAutoNotResolved() {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodAutoMultiAssign() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "i = 5;" +
                "i = 7;" +
                "i = 100;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodAutoReplace1() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "auto j;" +
                "public void main() {" +
                "auto tempv = 7;" +
                "i = 0;" +
                "j = true;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodExtendsReplaceParent() throws Exception {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public void main() {" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadExtendsReplaceParent() {
        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public int main() {" + //child main() has different return type
                "return 0;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodReplaceParentMethod() throws Exception {
        String foo = "public class onehundredfiftyseven {" +
                "auto i;" +
                "public void mainREALLL() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class twohundred extends onehundredfiftyseven {" +
                "int testing;" +
                "public void mainREALLL() {" +
                "testing = 99;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testLargeReplaceParent() throws Exception {
        String foo = "public class one {" +
                "public void main1() {" +
                "}" +
                "public void main2() {" +
                "}" +
                "public void main3() {" +
                "}" +
                "public void main4() {" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public void main1() {" +
                "}" +
                "public void main2() {" +
                "}" +
                "public void main3() {" +
                "}" +
                "public void main4() {" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testReplaceMultipleClasses() throws Exception {
        String foo = "public class one {" +
                "public void main1() {" +
                "}" +
                "}" +
                "public class two extends one {" +
                "public void main1() {" +
                "}" +
                "}" +
                "public class parent {" +
                "public void coolMethod() {" +
                "int i = 0;" +
                "}" +
                "}" +
                "public class child extends parent {" +
                "public void coolMethod() {" +
                "int j = 0;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testSuccessfulMergeParentChild1() throws Exception {
        String foo = "public class one {" +
                "int test1;" +
                "}" +
                "public class two extends one {" +
                "int testing2;" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testSuccessfulMergeParentChild2() throws Exception {
        String foo = "public class one {" +
                //"int test1;" + //proves empty class list works
                "}" +
                "public class two extends one {" +
                //"int testing2;" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testRegularPass() throws Exception {
        String foo = "public class foo{public int foo4;}" +
                "public class foo6 extends foo{public int foo99;}" +
                "public class foo2{" +
                "public int foo3;" +
                "public int main(){" +
                "int yyy = foo.foo4; " +
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
        goodTest(foo);
    }

    @Test
    public void testProperMethodLocalScopeing() {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main(int one) {" + //int one declared
                "foo1 = 1;" +
                "one = 1;" + //successful "one" assignment
                "int foo2 = 0;" +
                "foo2 = 9;" +
                "}" +
                "" +
                "public void main2(int two){" +
                "one = 1;" + //one not found --> GOOD
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadImplicitExtends() {
        String foo = "public class foo extends foo2{public int foo4;}" +
                "public class foo2 {int fooGood;}";
        badTest(foo);
    }

    @Test
    public void testProperExtends() throws Exception {
        String foo = "public class foo {public int foo4;}" +
                "public class foo2 extends foo {int fooGood;}";
        goodTest(foo);
    }

    @Test
    public void testBadExtends() {
        String foo = "public class foo extends foo3 {public int foo4;}" +
                "public class foo2 extends foo {int fooGood;}";
        badTest(foo);
    }

    @Test
    public void testDuplicateVar() {
        String foo = "public class foo2{" +
                "public int foo3;" + //duplicate var
                "public int foo3;" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadVarAssignmentPlace() {
        String foo = "public class foo2{" +
                "public int foo3 = 0;" + //not allowed to give a variable an assignment in a class, only in a function
                "}";
        badTest(foo);
    }

    @Test
    public void testNameCollision() {
        String foo = "public class MainClass {public int foo; public int foo;}";
        badTest(foo);
    }

    @Test
    public void testValidJavaConvention() throws Exception { //this is valid Java convention, may or may not change, valid now
        String foo = "public class foo {public int foo;}";
        goodTest(foo);
    }

    @Test
    public void testGoodStuff1() throws Exception {
        String foo = "public class example {" +
                "public String cool;" +
                "public void method1(int one, int two) {" +
                "int three = 1;" +
                "cool = \"Cool1\" + \"yeah\";" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testParamCollisionM() {
        String foo = "public class example {" +
                "public int cool;" + //collide with this
                "public void method1(int cool, int cool) {" + //and these should collide
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testAll() throws Exception {
        String foo = "public class foo{public int foo4;}" +
                "public class foo6 extends foo{public int foo44;}" +
                "public class foo2 {" +//
                "public String foo3;" +
                "public String foo966;" +
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
    public void testGoodIfStatement() throws Exception {
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
    public void testBadIfStatement() {
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
    public void testGoodWhileStatement() throws Exception {
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( 3 < 5 ) {" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadWhileStatement() {
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( \"Phosphophyllite\" ){" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodForStatement() throws Exception {
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadForStatement() {
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i + 10; i=i+1;){" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadScopeWhile() {
        String foo = "public class foo2{" +
                "public int main(){" +
                "while( true ){" +
                "int i = 1;" +
                "}" +
                "i = 2;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadScopeIfStatement() {
        String foo = "public class foo2{" +
                "public int main(){" +
                "if (1 == 1){" +
                "int i;" +
                "}" +
                "else{" +
                "i = i + 1;" +
                "}" +
                "return 0;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodScopeForStatement() throws Exception {
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "i = 3;" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadScopeForStatement() {
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "}" +
                "i = 9;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodBooleanAssignment() throws Exception {
        String foo = "public class one {" +
                "boolean test;" +
                "public void main(){"+
                "test = true;" + //"true" literal
                "test = false;"+ //"false" literal
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadBooleanAssignment() {
        String foo = "public class one {" +
                "boolean test;" +
                "public void main(){"+
                "test = True;" + //"True" considered an Identifier
                //"test = False;"+ //not reached, but same idea
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testMethodSignatureType1() throws Exception {
        String foo = "public class testProg {" +
                "boolean bo1;" +
                "public int testInt;"+
                "public int main(int a, int b){"+
                "return 4;"+
                "}" +
                "public void test() {" +
                "bo1 = true;"+
                "testInt = main(4,2);"+
                "}"+
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodMethodSignature() throws Exception {
        String foo = "public class one {" +
                "public int testInt;"+
                "public int main(int a){"+
                "return 4;"+
                "}" +
                "public void test1() {" +
                "testInt = main(1);" +
                "}"+
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodIntAssignment() throws Exception {
        String foo = "public class foo2 {" +
                "public int foo3;" +
                "public void test() {" +
                "foo3 = (1 + 1) / 2;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadIntAssignment() {
        String foo = "public class foo2{" +
                "public int foo3;" +
                "public void main() {" +
                "foo3 = \"string thingy\";" +
                "}" +
                "}";
        badTest(foo);
    }


    @Test
    public void testBadBoolean() {
        String foo = "public class foo2{" +
                "public boolean foo3;" +
                "public boolean foo4;" +
                "public void main() {" +
                "foo4 = foo3 + 1;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodBoolean() throws Exception {
        String foo = "public class foo2 {" +
                "public boolean foo;" +
                "public void one() {" +
                "foo = false || true;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodComplexBoolean() throws Exception {
        String foo = "public class foo2{" +
                "public boolean foofi;" +
                "public void main() {" +
                "foofi = (true || 1 < 2) && (1==1+1);" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testGoodStringIntConcat() throws Exception {
        String foo = "public class foo2{" +
                "public String foo3;" + //String
                "public void main() {" +
                "foo3 = \"string thingy\" + 1;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadReDeclareVar() {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "int foo1 = 1;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadScope() {
        String foo = "public class one {" +
                "public void main(int one) {" +
                "one = 1;" +
                "}" +
                "" +
                "public void main2(){" +
                "one = 1;" + //var not declared
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testVarScope() throws Exception {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "foo1 = 1;" +
                "}" +
                "}";
        testNewTypeChecker(foo);
    }

    @Test
    public void testDontAllowReDeclare() {
        String foo = "public class one {" +
                "public void main(int one) {" +
                "}" +
                "" +
                "int main;" +
                "}";
        badTest(foo);
    }

    @Test
    public void testDontAllowAssignmentExceptInMethod1() {
        String foo = "public class one {" +
                "int i = 0;" +
                "}";
        badTest(foo);
    }

    @Test
    public void testDontAllowAssignmentExceptInMethod2() throws Exception {
        String foo = "public class one {" +
                "int i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadDefinitionString() { //bad way to define a string
        String foo = "public class one {" +
                "public string one;" + //it thinks "string" is an identifier of a class, that hasnt been defined -> fail
                "}";
        badTest(foo);
    }

    @Test
    public void testBadForwardRefExtends() {
        String foo = "public class one extends two {" + //two is not known yet
                "int foo1;" +
                "}" +
                "public class two {" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodDefinitionString() throws Exception { //good way to define a string
        String foo = "public class one {" +
                "public String one;" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testExtends() throws Exception {
        String foo = "public class one {" +
                "int foo1;" +
                "}" +
                "public class two extends one {" +
                "public void main() {" +
                "foo1 = 0;" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadParamCollision1() {
        String foo = "public class example {" +
                "public int cool;" +
                "public void method1(int cool) {" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadParamCollision2Self() {
        String foo = "public class example {" +
                "int cool;" +
                "public void method1(int x, int cool) {" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testParamNoCollision() throws Exception {
        String foo = "public class example {" +
                "public void method1(int cool, int cool1) {" +
                "cool = 1;" +
                "cool1 = 1;" +
                "}" +
                "public void method2() {" +
                "method1(1, 2);" + //proves method params (above) are actually stored correctly
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadParamCollision3() {
        String foo = "public class example {" +
                "public void method1(int cool) {" + //proves internal method var storing works
                "int cool;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadParamCollision4() {
        String foo = "public class example {" +
                "public void method1(int cool, int cool1, int cool) {" + //proves internal param storing works
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodIfBlockDeclarations() throws Exception {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "if(true){" +
                "int i;" +
                "i = 1;" +
                "}" +
                "else {" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadIfBlockDeclarations() {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "if(true){" +
                "int i;" +
                "}" +
                "else {" +
                "}" +
                "i = 2;" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodNestedIfBlockDeclarations() throws Exception {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "if(true){" +
                "int i;" +
                "if(true){" +
                "i = 2;" +
                "}" +
                "else{" +
                "}" +
                "}" +
                "else {" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadNestedIfBlockDeclarations() {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "if(true){" +
                "int i;" +
                "if(true){" +
                "int i = 2;" +
                "}" +
                "else{" +
                "}" +
                "}" +
                "else {" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testGoodWhileBlockDeclarations() throws Exception {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "while(true){" +
                "foo1 = 2;" +
                "}" +
                "}" +
                "}";
        goodTest(foo);
    }

    @Test
    public void testBadWhileBlockDeclarations() {
        String foo = "public class one {" +
                "int foo1;" +
                "public void main() {" +
                "while(true){" +
                "int foo1 = 2;" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }

    @Test
    public void testBadScopeForStatment() {
        String foo = "public class foo2{" +
                "public int main(){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "for(int i = 1; i < 10; i=i+1;){" +
                "}" +
                "}" +
                "}" +
                "}";
        badTest(foo);
    }

    //BUG IN THIS UNIT TEST, READ NOTES
    @Test
    public void testBadForwardRefExtends2() {
        String foo = "public class one extends two {" + //two is not known yet
                "int foo1;" +
                "}" +
                "public class two {" +
                "}";
        badTest(foo);
    }
}
