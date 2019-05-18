package CoopJa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.typemeta.funcj.parser.Input;

import java.io.*;
import java.util.ArrayList;

public class CG_UT1 {

    public String ParseToProgramString(String inputString) throws TypeCheckerException {
        ArrayList<Token> tokenList = Token.tokenize(inputString);
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser();
        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow();
        Typechecker typecheckobj = new Typechecker();
        PProgram NewProg = typecheckobj.TypecheckMain(fooTester);

        try {
            String programString = NewProg.generateProgramString();
            System.out.printf( "%s","\n" + programString + "\n");
            return programString;
        } catch (CodeGenException e) {
            e.printStackTrace();
            return "Failed To Generate Code";
        }
    }

    public void TestCodeGenOutput(String codeString, String expectedOutput) throws IOException, TypeCheckerException {
        String cCodeOutput =  CompileAndRuncCode(codeString);
        Assertions.assertTrue(cCodeOutput.equals(expectedOutput), "Output and Input Differ" +
                "\nExpected: \n" + expectedOutput + "\nGot: \n" + cCodeOutput);
    }
    //similar to generateCFile in J_CodeGen but takes in the CoopJa code instead of the raw c code.
    public String CompileAndRuncCode(String codeString) throws IOException, TypeCheckerException {
        String cFile = ParseToProgramString(codeString);
        String fileName = "UnitTestFile.c";
        try {
            File outputFile = new File(fileName);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            else{
                outputFile.delete();
                outputFile.createNewFile();
            }

            Writer writer = new FileWriter(outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(cFile);
            bufferedWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }
        J_CodeGen_ExpressionTest.COMPILER(fileName);
        return J_CodeGen_ExpressionTest.RUNFILE("UnitTestFile.exe");
    }

    @Test
    public void CodeGenAuto() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class test {" +
                "auto i;" +
                "public int main(){" +
                "i = 100;" +
                "printf(\"%d\", i);" +
                "return 0;" +
                "}" +
                "}","100");
    }

    @Test
    public void CodeGenAutoInt() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class test {" +
                "auto i;" +
                "auto j;" +
                "auto k;" +
                "auto hello;" +
                "public int main(){" +
                "i = \"Auto Type\";" +
                "j = true;" +
                "k = 123;" +
                "hello = j;" +
                "printf(\"%d\", k);" +
                "}" +
                "}","123");
    }

    @Test
    public void CodeGenAutoString() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class test {" +
                "auto i;" +
                "auto j;" +
                "auto k;" +
                "auto hello;" +
                "public int main(){" +
                "i = \"Auto Type\";" +
                "j = true;" +
                "k = 123;" +
                "hello = j;" +
                "printf(\"%s\", i);" +
                "}" +
                "}","Auto Type");
    }

    @Test
    public void CodeGenAutoStringReassigntoStringVar() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class test {" +
                "auto i;" +
                "auto j;" +
                "public int main(){" +
                "i = \"Auto Type\";" +
                "j = \"Auto Type2\";" +
                "i = j;" +
                "printf(\"%s\", i);" +
                "}" +
                "}","Auto Type2");
    }

    @Test
    public void CodeGenAutoStringReassigntoStringLiteral() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class test {" +
                "auto i;" +
                "public int main(){" +
                "i = \"Auto Type\";" +
                "i = \"Auto Type2\";" +
                "printf(\"%s\", i);" +
                "}" +
                "}","Auto Type2");
    }

    ///////////////////////

    @Test
    public void CodeGenObjectIntReturn() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class Person1 {" +
                "public int returnint(){" + //same method
                "return 26;" +
                "}" +
                "}" +
                "public class Person2 {" +
                "public int returnint() {" + //same method
                "return 99;" +
                "}" +
                "}" +
                "public class one {" +
                "public int main() {" +
                "Person1 aaaa = new Person1;" +
                "printf(\"%d\", aaaa.returnint());" +
                "Person2 bbbb = new Person2;" +
                "printf(\"%d\", bbbb.returnint());" +
                "}" +
                "}","2699");
    }

    @Test
    public void C11111odeGenParentChild9999() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class parent {" +
                "int i;" +
                "}" +
                "public class child extends parent {" +
                "public int main() {" + //same method
                "i = 222;" +
                "printf(\"%d\", i);" +
                "}" +
                "}","222");
    }

    ////////////////////////



    @Test
    public void CodeGenParentOverwriteChildMethod() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class parent {" +
                "public int printself(){" + //same method
                "println(\"Parent\");" +
                "}" +
                "}" +
                "public class child extends parent {" +
                "public int printself() {" + //same method
                "println(\"Child\");" +
                "}" +
                "}" +
                "public class one {" +
                "public int main() {" +
                "child Child1 = new child;" +
                "Child1.printself();" +
                "parent Parent1 = new parent;" +
                "Parent1.printself();" +
                "}" +
                "}","ChildParent");
    }

    @Test
    public void CodeGenParentChildIntReturn() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class parent {" +
                "public int returnint(){" + //same method
                "return 0;" +
                "}" +
                "}" +
                "public class child extends parent {" +
                "public int returnint() {" + //same method
                "return 50;" +
                "}" +
                "}" +
                "public class one {" +
                "public int main() {" +
                "parent Parent1 = new parent;" +
                "printf(\"%d\", Parent1.returnint());" +
                "child Child1 = new child;" +
                "printf(\"%d\", Child1.returnint());" +
                "}" +
                "}","050");
    }

    @Test
    public void CodeGenParentChild9999() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class parent {" +
                "int i;" +
                "}" +
                "public class child extends parent {" +
                "public int main() {" + //same method
                "i = 222;" +
                "printf(\"%d\", i);" +
                "}" +
                "}","222");
    }






}
