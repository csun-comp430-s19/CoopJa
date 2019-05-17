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

    @Test //only duplicate test right now //only working
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
    public void CodeGenAuto11133331() throws IOException, TypeCheckerException {
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
                "}","Auto Type");
    }

    @Test
    public void CodeGenAuto112222133331() throws IOException, TypeCheckerException {
        TestCodeGenOutput("public class parent {" +
                "public int main(){" +
                "println(\"Rose Windmill\");" +
                "}" +
                "}" +
                "public class child {" +
                "public int main() {" +
                "println(\"hello\");" +
                "}" +
                "}" +
                "public class one {" +
                "public int main() {" +
                "child test = new child;" +
                "test.main();" +
                "}" +
                "}","Auto Type");
    }

}
