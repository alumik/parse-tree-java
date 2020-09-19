package cn.alumik.parsetree.symbol;

public class AbstractTerminalSymbol extends AbstractSymbol {

    public static final String NULL = "null";

    public static final String END = "$";

    public AbstractTerminalSymbol(String name) {
        setName(name);
    }

    public static AbstractTerminalSymbol Null() {
        return new AbstractTerminalSymbol(NULL);
    }

    public static AbstractTerminalSymbol End() {
        return new AbstractTerminalSymbol(END);
    }

    @Override
    public int getType() {
        return TERMINAL;
    }
}
