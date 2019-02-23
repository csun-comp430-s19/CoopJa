package main.java.CoopJa;

import java.util.ArrayList;

public class Statement {

    public Statement() {

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

    public void printSelf() {

    }

}

class IfStmt extends Statement {

    ArrayList<Token> Conditions;
    ArrayList<Token> Statements;
    ArrayList<Token> ElseStmts;

    public IfStmt(ArrayList<Token> in_Cond, ArrayList<Token> in_Stmts, ArrayList<Token> in_ElseStmts) {
        Conditions = in_Cond;
        Statements = in_Stmts;
        ElseStmts = in_ElseStmts;
    }

    public void printSelf() {
        System.out.println("----- If Conditions -----");
        printTokens(this.Conditions);
        System.out.println("----- If Statements -----");
        printTokens(this.Statements);
        System.out.println("----- If Else Statements -----");
        printTokens(this.ElseStmts);
    }
}

class ForStmt extends Statement {

}

class WhileLoop extends Statement {

    ArrayList<Token> Conditions;
    ArrayList<Token> Statements;

    public WhileLoop(ArrayList<Token> in_Cond, ArrayList<Token> in_Stmts) {
        Conditions = in_Cond;
        Statements = in_Stmts;
    }

    public void printSelf() {
        System.out.println("----- While Conditions -----");
        printTokens(this.Conditions);
        System.out.println("----- While Statements -----");
        printTokens(this.Statements);
    }
}