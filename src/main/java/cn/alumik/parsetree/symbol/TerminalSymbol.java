package cn.alumik.parsetree.symbol;

public class TerminalSymbol extends Symbol {

    public TerminalSymbol(AbstractTerminalSymbol abstractTerminalSymbol) {
        super(abstractTerminalSymbol.getName());
        setAbstractSymbol(abstractTerminalSymbol);
    }

    public TerminalSymbol(String value) {
        super(value);
    }
}
