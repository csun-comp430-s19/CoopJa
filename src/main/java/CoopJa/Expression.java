package main.java.CoopJa;

import javax.accessibility.AccessibleRole;
import java.util.ArrayList;

public class Expression {

    public Expression(){

    }

    public void printSelf(){}
}

class VariableExpression extends Expression{
    Token Variable;
    public VariableExpression(Token variableToken){
        Variable = variableToken;
    }

    public void printSelf() {
        System.out.println("----- Variable -----");
        System.out.println(Variable.getTokenString());
    }

    @Override
    public boolean equals(Object o){ //used to help the Unit Testing
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        VariableExpression expression = (VariableExpression) o;
        return expression.Variable.getTokenString().equals(this.Variable.getTokenString());
    }
}

class StringExpression extends Expression{
    Token TString;
    public StringExpression(Token stringToken){
        TString = stringToken;
    }

    public void printSelf() {
        System.out.println("----- String -----");
        System.out.println(TString.getTokenString());
    }
    @Override
    public boolean equals(Object o){ //used to help the Unit Testing
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        StringExpression expression = (StringExpression) o;
        return expression.TString.getTokenString().equals(this.TString.getTokenString());
    }
}

class NumExpression extends  Expression{
    Token Number;
    public NumExpression(Token number){
        Number = number;
    }
    public void printSelf() {
        System.out.println("----- Number -----");
        System.out.println(Number.getTokenString());
    }

    @Override
    public boolean equals(Object o){ //used to help the Unit Testing
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        NumExpression expression = (NumExpression) o;
        return expression.Number.getTokenString().equals(this.Number.getTokenString());
    }
}

class ArithmeticExpression extends  Expression{
    Expression Expression1;
    Token Operator;
    Expression Expression2;

    public ArithmeticExpression(Expression in_Exp1, Token in_Op, Expression in_Exp2){
        Expression1 = in_Exp1;
        Operator = in_Op;
        Expression2 = in_Exp2;
    }

    public void printSelf() {
        System.out.println("----- 1st Expression -----");
        this.Expression1.printSelf();
        System.out.println("----- Operator -----");
        System.out.println(Operator.getType().name());
        System.out.println("----- 2nd Expression -----");
        this.Expression2.printSelf();
    }

    @Override
    public boolean equals(Object o){ //used to help the Unit Testing
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        ArithmeticExpression expression = (ArithmeticExpression) o;
        if (!expression.Expression1.equals(this.Expression1)) return false;
        if (!expression.Operator.getTokenString().equals(this.Operator.getTokenString())) return false;
        if (!expression.Expression2.equals(this.Expression2)) return false;
        return true;
    }
}
