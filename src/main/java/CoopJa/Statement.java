package main.java.CoopJa;

import java.util.ArrayList;

public class Statement {

    public Statement() {

    }

    public void printTokens(ArrayList<Token> toPrint) { //may or may not leave in
        System.out.println("// Printing ArrayList //");
        for (int i = 0; i < toPrint.size(); i++) {
            System.out.println(toPrint.get(i).getType().name());
        }
        System.out.println("// End of List //");
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
}

class ForStmt extends Statement {

}

class WhileLoop extends Statement {

}