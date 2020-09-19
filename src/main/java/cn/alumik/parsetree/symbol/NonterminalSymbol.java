package cn.alumik.parsetree.symbol;

public class NonterminalSymbol extends Symbol {

    public NonterminalSymbol(AbstractNonterminalSymbol abstractNonterminalSymbol) {
        super(abstractNonterminalSymbol.getName());
        setAbstractSymbol(abstractNonterminalSymbol);
    }

    public NonterminalSymbol(String value) {
        super(value);
    }
}
