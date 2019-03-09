package main.java.CoopJa;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Statement {

    public Statement() {

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
        Utilities.printTokens(this.Conditions);
        System.out.println("----- If Statements -----");
        Utilities.printTokens(this.Statements);
        System.out.println("----- If Else Statements -----");
        Utilities.printTokens(this.ElseStmts);
    }
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
        Utilities.printTokens(this.Conditions);
        System.out.println("----- While Statements -----");
        Utilities.printTokens(this.Statements);
    }
}

class ForLoop extends Statement {

    ArrayList<Token> Condition1;
    ArrayList<Token> Condition2;
    ArrayList<Token> Condition3;
    ArrayList<Token> Statements;

    public ForLoop(ArrayList<Token> in_Cond1, ArrayList<Token> in_Cond2, ArrayList<Token> in_Cond3, ArrayList<Token> in_Stmts) {
        Condition1 = in_Cond1;
        Condition2 = in_Cond2;
        Condition3 = in_Cond3;
        Statements = in_Stmts;
    }

    public void printSelf() {
        System.out.println("----- For Condition 1 -----");
        Utilities.printTokens(this.Condition1);
        System.out.println("----- For Condition 2 -----");
        Utilities.printTokens(this.Condition2);
        System.out.println("----- For Condition 3 -----");
        Utilities.printTokens(this.Condition3);
        System.out.println("----- For Statements -----");
        Utilities.printTokens(this.Statements);
    }
}

class ExpressionCool extends Statement {

    ArrayList<ArrayList<Token>> Exp = new ArrayList<ArrayList<Token>>(1);
    int loc = 0;

    public ExpressionCool() {
        //add expressions as they are parsed
    }

    public void add(ArrayList<Token> input) {
        Exp.add(loc, input);
        loc++;
    }

    public void printSelf() {
        System.out.println("----- Expression -----");
        for (int i = 0; i < Exp.size(); i++) {
            int var1 = i + 1;
            System.out.println("----- Part " + var1 + " -----");
            Utilities.printTokens(Exp.get(i));
        }
    }
}