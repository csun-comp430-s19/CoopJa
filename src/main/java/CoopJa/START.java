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
                    System.out.println(line);
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
            System.out.println("Final Input:");
            System.out.println(ProgInput);
            ProgramString = ProgInput.toString();
        }

        //now run stuff

        System.out.println("Program test");
        System.out.println(ProgramString);





        //ArrayList<Token> tokenList = Token.tokenize(foo); //Tokenize
        //Input<Token> tokenListInput = new TokenParserInput(tokenList);
        //MainParser parsers = new MainParser();
        //PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse
    }




}
