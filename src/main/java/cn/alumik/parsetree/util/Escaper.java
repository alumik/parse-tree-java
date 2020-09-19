package cn.alumik.parsetree.util;

import cn.alumik.parsetree.symbol.AbstractTerminalSymbol;

public class Escaper {

    public static String unescape(Character escaped) {
        return switch (escaped) {
            case '\t' -> "\\\\t";
            case '\r' -> "\\\\r";
            case '\n' -> "\\\\n";
            case '\f' -> "\\\\f";
            case '\\' -> "\\\\";
            case '\0' -> AbstractTerminalSymbol.NULL;
            default -> escaped.toString();
        };
    }
}
