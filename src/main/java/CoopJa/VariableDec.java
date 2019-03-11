package main.java.CoopJa;

public class VariableDec implements Declaration
{
    //name of the variable
    String name;
    //Type of the variable, from declaration.
    //Should be one of the base types (e.g. not variable type)
    Type type;
    
    public VariableDec(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }
}    
        