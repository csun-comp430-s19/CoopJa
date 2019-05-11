package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PProgram {
    public ArrayList<PClassDeclaration> classDeclarationList;

    public PProgram(ArrayList<PClassDeclaration> classDeclarationList){
        this.classDeclarationList = classDeclarationList;
    }

    public String generateProgramString() throws CodeGenException {
        StringBuilder programString = new StringBuilder();
        programString.append("#include <stdio.h>\n#include <stdbool.h>\n\n");
        // Linked hasmap for helping with inheritance
        LinkedHashMap<String, PClassDeclaration> classMap = new LinkedHashMap<>();
        for (int i = 0; i < classDeclarationList.size(); i++){
            PClassDeclaration currentDeclrataion = classDeclarationList.get(i);
            classMap.put(currentDeclrataion.identifier.getTokenString(), currentDeclrataion);
            programString.append(currentDeclrataion.generateClassString(classMap));
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

        /*String foo = "public class foo{" +
                "public int favoriteNumber;" +
                "public int main(){" +
                "println(\"Hello World!\");" +
                "favoriteNumber = 9;" +
                "int foo2 = 1;" +
                "int foo3 = 2;" +
                "foo3 = 3;" +
                "return 0;" +
                "}" +
                "}";*/


        // Fizz Buzz example
        /*String foo = "public class test{\n" +
                "    public int moduloHack(int x, int n){\n" +
                "        int p;\n" +
                "        int q;\n" +
                "        int m;\n" +
                "        q = x/n;\n" +
                "        p = q*n;\n" +
                "        m = x - p;\n" +
                "        return m;\n" +
                "    }\n" +
                "    \n" +
                "    public int main(){\n" +
                "        for (int i = 0; i <= 45; i = i + 1;){\n" +
                "            if (moduloHack(i, 15) == 0){\n" +
                "                println(\"FizzBuzz\");\n" +
                "            } \n" +
                "            else{\n" +
                "                if (moduloHack(i, 3)){\n" +
                "                    println(\"Fizz\");\n" +
                "                }\n" +
                "                else{\n" +
                "                    if (moduloHack(i ,5)){\n" +
                "                        println(\"Buzz\");\n" +
                "                    }else{}\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return 0;\n" +
                "    }\n" +
                "}";
        /*String foo = "public class test{\n" +
                "    public int main(){\n" +
                "        if(true){\n" +
                "            println(\"Hello World\");\n" +
                "        }\n" +
                "        else{}\n" +
                "        return 0;\n" +
                "    }\n" +
                "}";*/
        // Object Testing
        String foo = "public class ClassTest{\n" +
                "    public int favoriteNumber;\n" +
                "    public int someOtherNumber;\n" +
                "    void setFavNumber(int number){\n" +
                "        favoriteNumber = number;\n" +
                "    }\n" +
                "    void guessFavNumber(int number){\n" +
                "        if (favoriteNumber == number){\n" +
                "            println(\"Correct\");\n" +
                "        }\n" +
                "        else{\n" +
                "            println(\"Incorrect\");\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test{\n" +
                "    public void printHelloAgain(){" +
                "        printf(\"Hello World 2!\\n\");" +
                "    }" +
                "    public int main(){\n" +
                "        printf(\"Hello World!\\nToday's number are %d %d %d\\n\", 7, 9, 15);\n" +
                "        ClassTest foo = new ClassTest;\n" +
                "        foo.setFavNumber(7);\n" +
                "        foo.guessFavNumber(7);\n" +
                "        foo.favoriteNumber = 6;\n" +
                "        foo.guessFavNumber(6);\n" +
                "        foo.someOtherNumber = 5;\n" +
                "        foo.favoriteNumber = foo.someOtherNumber;\n" +
                "        foo.guessFavNumber(5);\n" +
                "        printHelloAgain();" +
                "    }\n" +
                "}\n";

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
