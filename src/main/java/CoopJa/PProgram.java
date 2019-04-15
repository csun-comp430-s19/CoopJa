package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;

public class PProgram {
    public ArrayList<PClassDeclaration> classDeclarationList;

    public PProgram(ArrayList<PClassDeclaration> classDeclarationList){
        this.classDeclarationList = classDeclarationList;
    }

    public String generateProgramString() throws CodeGenException {
        StringBuilder programString = new StringBuilder();
        programString.append("#include <stdio.h>\n\n");
        for (int i = 0; i < classDeclarationList.size(); i++){
            programString.append(classDeclarationList.get(i).generateClassString());
        }
        return programString.toString();
    }


    // Test
    //TODO: Delete this
    public static void main(String[] args){
        /*String foo = "public class foo2{" +
                "public int foo3 = 0;" +
                "public int main(){" +
                "foo3 = 1;" +
                "}" +
                "}";*/

        String foo = "public class foo{" +
                "public int favoriteNumber;" +
                "public int main(){" +
                "println(\"Hello World!\");" +
                "favoriteNumber = 9;" +
                "int foo2 = 1;" +
                "int foo3 = 2;" +
                "foo3 = 3;" +
                "}" +
                "}";

        ArrayList<Token> tokenList = Token.tokenize(foo);
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser();

        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow();
        try {
            String programString = fooTester.generateProgramString();
            System.out.printf("%s", programString);
        } catch (CodeGenException e) {
            e.printStackTrace();
        }
    }
}
