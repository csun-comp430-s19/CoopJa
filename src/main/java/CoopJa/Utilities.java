package main.java.CoopJa;

import java.util.ArrayList;

public class Utilities {
    public Utilities(){

    }
    public static void printTokens(ArrayList<Token> toPrint) {
        if (toPrint == null) {
            System.out.println("// Printing ArrayList //");
            System.err.println("ArrayList is Empty"); //NOTE EMPTY ARRAYLIST
            System.out.println();
            System.out.println("// End of List //");
        } else {
            System.out.println("// Printing ArrayList //");
            for (int i = 0; i < toPrint.size(); i++) {
                System.out.println(toPrint.get(i).getType().name());
            }
            System.out.println("// End of List //");
        }
    }
}
