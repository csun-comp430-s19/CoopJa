package CoopJa;

public class CodeGenException extends Exception {
    public final static String UNIMPLEMENTED_EXPRESSION_TYPE = "Unimplemented expression type used: ";
    public final static String UNIMPLEMENTED_STATEMENT_TYPE = "Unimplemented statement type used: ";

    public CodeGenException(String error){
        super (error);
    }
}
