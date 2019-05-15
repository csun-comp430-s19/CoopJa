package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.io.IOException;
import java.util.ArrayList;

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

        if (args.length <= 0) {
            System.err.println("You have not given a file");
            System.exit(-1);
        } else {
            System.out.println("Yes you have");
            System.out.println(args[0]);
        }




        //ArrayList<Token> tokenList = Token.tokenize(foo); //Tokenize
        //Input<Token> tokenListInput = new TokenParserInput(tokenList);
        //MainParser parsers = new MainParser();
        //PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse
    }




}
