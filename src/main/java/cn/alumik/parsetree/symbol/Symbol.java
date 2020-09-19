package cn.alumik.parsetree.symbol;

public class Symbol {

    private AbstractSymbol mAbstractSymbol;

    private String mValue;

    public Symbol(String value) {
        mValue = value;
    }

    public AbstractSymbol getAbstractSymbol() {
        return mAbstractSymbol;
    }

    public void setAbstractSymbol(AbstractSymbol abstractSymbol) {
        mAbstractSymbol = abstractSymbol;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol) {
            final Symbol symbol = (Symbol) obj;
            return mAbstractSymbol.equals(symbol.getAbstractSymbol()) && mValue.equals(symbol.mValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mAbstractSymbol.hashCode() ^ mValue.hashCode();
    }
}
