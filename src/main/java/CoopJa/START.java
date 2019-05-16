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
            "public void main() {" +
            "print(\"CoopJa\");" +
            "print(\"Java to C Compiler\");" +
            "print(\"by:\");" +
            "print(\"Nicholas Araklisianos\");" +
            "print(\"Miguel Cruz\");" +
            "print(\"Jacob Poersch\");" +
            "print(\"Carlos Sandoval\");" +
            "print(\"\");" +
            "}" +
            "}";

    public static void main(String args[]) throws IOException {

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

        //now run stuff

        System.out.println("Program test");
        System.out.println(ProgramString);



        //ArrayList<Token> tokenList = Token.tokenize(foo); //Tokenize
        //Input<Token> tokenListInput = new TokenParserInput(tokenList);
        //MainParser parsers = new MainParser();
        //PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse

        //Typechecker tempTypeC = new Typechecker();
        //PProgram FixedProgram = tempTypeC.TypecheckMain(fooTester); //Typecheck

        //*****use "FixedProgram" var, the one returned from typechecker to run codegen

				/* Generate the C File */
				// try
				// {
					// String generatedProgramString = fooTester.generateProgramString();
					// System.out.println("\nGenerated Program:");
					// System.out.printf("%s", generatedProgramString);
				// }
				// catch (CodeGenException e)
				// {
					// ;
				// }
				/* Save & Execute as a C File.
						If it doesn't print to console on its own,
						try piping output to stdout console?
				*/
    }




}
