package cn.alumik.parsetree.parser;

import cn.alumik.parsetree.symbol.AbstractSymbol;
import cn.alumik.parsetree.symbol.AbstractTerminalSymbol;

public class Item {

    private int mDot = 0;

    private final Production mProduction;

    private final AbstractTerminalSymbol mLookAhead;

    public Item(Production production, AbstractTerminalSymbol lookAhead) {
        mProduction = production;
        mLookAhead = lookAhead;
    }

    public AbstractSymbol getNextSymbol() {
        return mProduction.to().get(mDot);
    }

    public int getDot() {
        return mDot;
    }

    public Production getProduction() {
        return mProduction;
    }

    public AbstractTerminalSymbol getLookAhead() {
        return mLookAhead;
    }

    public boolean isNotEnded() {
        return mDot < mProduction.to().size() && !mProduction.to().get(0).getName().equals(AbstractTerminalSymbol.NULL);
    }

    public Item getNextItem() {
        final Item item = new Item(mProduction, mLookAhead);
        item.mDot = mDot + 1;
        return item;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            final Item item = (Item) obj;
            return item.mDot == mDot && item.mProduction.equals(mProduction) && item.mLookAhead.equals(mLookAhead);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mProduction.hashCode() ^ mDot;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mProduction.from());
        stringBuilder.append(" ->");
        for (int i = 0; i < mDot; ++i) {
            stringBuilder.append(" ");
            stringBuilder.append(mProduction.to().get(i));
        }
        stringBuilder.append(" ·");
        for (int i = mDot; i < mProduction.to().size(); i++) {
            stringBuilder.append(" ");
            stringBuilder.append(mProduction.to().get(i));
        }
        stringBuilder.append(", ");
        stringBuilder.append(mLookAhead);
        return stringBuilder.toString();
    }
}
