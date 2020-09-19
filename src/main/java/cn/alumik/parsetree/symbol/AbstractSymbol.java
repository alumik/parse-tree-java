package cn.alumik.parsetree.symbol;

public abstract class AbstractSymbol {

    public static final int TERMINAL = 0x01;

    public static final int NONTERMINAL = 0xff;

    private String mName;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public abstract int getType();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractSymbol) {
            final AbstractSymbol abstractSymbol = (AbstractSymbol) obj;
            return getType() == abstractSymbol.getType() && mName.equals(abstractSymbol.mName);
        }
        return false;
    }

    @Override
    public String toString() {
        return (getType() == TERMINAL ? "Terminal Symbol: " : "Nonterminal Symbol: ") + mName;
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}
