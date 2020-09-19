package cn.alumik.parsetree.parser;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.symbol.AbstractSymbol;
import cn.alumik.parsetree.symbol.AbstractTerminalSymbol;

import java.util.ArrayList;
import java.util.List;

public class Production {

    private AbstractSymbol mFrom;

    private List<AbstractSymbol> mTo;

    protected Production() {
    }

    public Production(AbstractSymbol from, List<AbstractSymbol> to) {
        mFrom = from;
        mTo = to;
    }

    public Production(Production production) {
        mFrom = production.mFrom;
        mTo = new ArrayList<>();
        mTo.addAll(production.mTo);
    }

    public static Production fromString(String input, Grammar grammar) throws AnalysisException {
        final Production production = new Production();
        if (!input.contains("->")) {
            throw new AnalysisException(AnalysisException.ILL_FORMED_PRODUCTION, null);
        }
        final String[] parts = input.split("->");
        if (parts.length != 2) {
            throw new AnalysisException(AnalysisException.ILL_FORMED_PRODUCTION, null);
        }
        final String[] fromStr = parts[0].trim().split(" +");
        final String[] toStr = parts[1].trim().split(" +");
        if (fromStr.length != 1) {
            throw new AnalysisException(AnalysisException.ILL_FORMED_PRODUCTION_LEFT, null);
        }
        try {
            production.mFrom = grammar.getSymbolPool().getNonterminalSymbol(fromStr[0]);
        } catch (AnalysisException e) {
            throw new AnalysisException(AnalysisException.ILL_FORMED_PRODUCTION_LEFT, e);
        }
        production.mTo = new ArrayList<>();
        if (toStr.length == 1 && toStr[0].equals(AbstractTerminalSymbol.NULL)) {
            production.mTo.add(grammar.getSymbolPool().getTerminalSymbol(AbstractTerminalSymbol.NULL));
            return production;
        } else if (toStr.length > 0 && toStr[0].length() > 0) {
            for (final String string : toStr) {
                try {
                    production.mTo.add(grammar.getSymbolPool().getSymbol(string));
                } catch (AnalysisException e) {
                    throw new AnalysisException(String.format(AnalysisException.SYMBOL_NOT_EXIST, string), e);
                }
            }
            return production;
        } else {
            throw new AnalysisException(AnalysisException.ILL_FORMED_PRODUCTION_RIGHT, null);
        }
    }

    public AbstractSymbol from() {
        return mFrom;
    }

    public List<AbstractSymbol> to() {
        return mTo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Production) {
            final Production production = (Production) obj;
            if (!mFrom.equals(production.mFrom) || mTo.size() != production.mTo.size()) {
                return false;
            }
            for (int i = 0; i < mTo.size(); i++) {
                if (!mTo.get(i).equals(production.mTo.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("Production: ");
        result.append(mFrom.toString());
        result.append(" ->");
        for (final AbstractSymbol abstractSymbol : mTo) {
            result.append(" ");
            result.append(abstractSymbol.toString());
        }
        return result.toString();
    }

    @Override
    public int hashCode() {
        int hash = mFrom.hashCode();
        for (final AbstractSymbol abstractSymbol : mTo) {
            hash ^= abstractSymbol.hashCode();
        }
        return hash;
    }
}
