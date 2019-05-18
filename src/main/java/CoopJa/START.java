package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class START {

    public static String DefaultProgramString = "" +
            "public class CoopJa {" +
            "public int main() {" +
            "println(\"CoopJa\");" +
            "println(\"Java to C Compiler\");" +
            "println(\"by:\");" +
            "println(\"Nicholas Araklisianos\");" +
            "println(\"Miguel Cruz\");" +
            "println(\"Jacob Poersch\");" +
            "println(\"Carlos Sandoval\");" +
            "}" +
            "}";

    public static void main(String args[]) throws IOException, TypeCheckerException {

        String ProgramString;

        if (args.length <= 0) {
            System.out.println("You have not given a file. So we will run the Default Program String.");
            ProgramString = DefaultProgramString;
        } else {
            String filename = args[0];
            System.out.println("You have given file named: " + filename);
            StringBuilder ProgInput = new StringBuilder();
            BufferedReader reader = null;
            try {
                File file = new File(filename);
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    ProgInput.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ProgramString = ProgInput.toString();
        }

        ArrayList<Token> tokenList = Token.tokenize(ProgramString); //Tokenize
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser();
        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse

        Typechecker tempTypeC = new Typechecker();
        PProgram FixedProgram = tempTypeC.TypecheckMain(fooTester); //Typecheck

        /* Generate the C File */
        try {
            String generatedProgramString = FixedProgram.generateProgramString();
            System.out.println("\nGenerated Program:");
            System.out.printf("%s", generatedProgramString);
        } catch (CodeGenException e) {

        }
    }


}
