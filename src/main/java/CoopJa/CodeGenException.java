package CoopJa;

public class CodeGenException extends Exception {
    public final static String UNIMPLEMENTED_EXPRESSION_TYPE = "Unimplemented expression type used: ";
    public final static String UNIMPLEMENTED_STATEMENT_TYPE = "Unimplemented statement type used: ";
    public final static String REDECLARATION_VARIABLE = "Attempted to redeclare a variable";
    public final static String REDECLARATION_FUNCTION = "Attempted to redeclare a function";
    public final static String REDECLARATION = "Attempted to redeclare something";
    public final static String UNKNOWN_DECLARATION = "Unknown Declaration type";

    public CodeGenException(String error){
        super (error);
    }
}
