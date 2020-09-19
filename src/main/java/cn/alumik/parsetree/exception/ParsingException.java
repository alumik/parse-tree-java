package cn.alumik.parsetree.exception;

public class ParsingException extends Exception {

    public static final String PARSING_ERROR =
            "An error occurred when parsing the %s-th symbol `%s`.";

    public static final String PARSING_NOT_COMPLETE =
            "The parsing process is not complete. Remaining symbols are: %s.";

    public static final String LEXICAL_ANALYSIS_FAILED =
            "Lexical analysis failed.";

    public static final String INVALID_REGEX =
            "Some regular expressions are invalid.";

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
