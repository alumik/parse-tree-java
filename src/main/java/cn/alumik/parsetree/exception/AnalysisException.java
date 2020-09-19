package cn.alumik.parsetree.exception;

public class AnalysisException extends Exception {

    public static final String INVALID_NAME =
            "`%s` is a keyword in ParseTree for %s. Please choose another symbol name.";

    public static final String TERMINAL_SYMBOL_NOT_EXIST =
            "Terminal symbol `%s` doesn't exist.";

    public static final String NONTERMINAL_SYMBOL_NOT_EXIST =
            "Nonterminal symbol `%s` doesn't exist.";

    public static final String SYMBOL_NOT_EXIST =
            "Symbol `%s` doesn't exist.";

    public static final String START_SYMBOL_NOT_TERMINAL =
            "The start symbol must be a terminal symbol.";

    public static final String ILL_FORMED_PRODUCTION =
            "A production should be divided into 2 parts by `->`, like `A -> c B d`.";

    public static final String ILL_FORMED_PRODUCTION_LEFT =
            "There must be 1 and only 1 nonterminal symbol in the left part of a production.";

    public static final String ILL_FORMED_PRODUCTION_RIGHT =
            "The right part of a production must not be empty. Set it to `null` if you want to input an empty production.";

    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
