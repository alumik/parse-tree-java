package cn.alumik.parsetree.symbol;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.parser.Grammar;

import java.util.*;

public class SymbolPool {

    private Map<String, AbstractTerminalSymbol> mTerminalSymbols;

    private Map<String, AbstractNonterminalSymbol> mNonterminalSymbols;

    public SymbolPool(Set<String> terminalSymbols, Set<String> nonterminalSymbols)
            throws AnalysisException {
        final Map<String, String> keywords = Map.of(
                AbstractTerminalSymbol.NULL, "an empty string",
                AbstractTerminalSymbol.END, "the end of a production",
                Grammar.START_SYMBOL, "the start symbol of the augmented grammar");
        for (final String name : keywords.keySet()) {
            if (terminalSymbols.contains(name) || nonterminalSymbols.contains(name)) {
                throw new AnalysisException(
                        String.format(AnalysisException.INVALID_NAME, name, keywords.get(name)), null);
            }
        }
        initTerminalSymbols(terminalSymbols);
        initNonterminalSymbols(nonterminalSymbols);
    }

    private void initTerminalSymbols(Set<String> terminalSymbols) {
        mTerminalSymbols = new HashMap<>();
        for (final String name : terminalSymbols) {
            mTerminalSymbols.put(name, new AbstractTerminalSymbol(name));
        }
        mTerminalSymbols.put(AbstractTerminalSymbol.NULL, AbstractTerminalSymbol.Null());
        mTerminalSymbols.put(AbstractTerminalSymbol.END, AbstractTerminalSymbol.End());
    }

    private void initNonterminalSymbols(Set<String> nonterminalSymbols) {
        mNonterminalSymbols = new HashMap<>();
        for (final String name : nonterminalSymbols) {
            mNonterminalSymbols.put(name, new AbstractNonterminalSymbol(name));
        }
    }

    public Set<AbstractTerminalSymbol> getTerminalSymbols() {
        return new HashSet<>(mTerminalSymbols.values());
    }

    public AbstractTerminalSymbol getTerminalSymbol(String name) throws AnalysisException {
        if (mTerminalSymbols.containsKey(name)) {
            return mTerminalSymbols.get(name);
        }
        throw new AnalysisException(String.format(AnalysisException.TERMINAL_SYMBOL_NOT_EXIST, name), null);
    }

    public Set<String> getNonterminalSymbolNames() {
        return mNonterminalSymbols.keySet();
    }

    public Set<AbstractNonterminalSymbol> getNonterminalSymbols() {
        return new HashSet<>(mNonterminalSymbols.values());
    }

    public AbstractNonterminalSymbol getNonterminalSymbol(String name) throws AnalysisException {
        if (mNonterminalSymbols.containsKey(name)) {
            return mNonterminalSymbols.get(name);
        }
        throw new AnalysisException(String.format(AnalysisException.NONTERMINAL_SYMBOL_NOT_EXIST, name), null);
    }

    public void addNonterminalSymbol(AbstractNonterminalSymbol abstractNonterminalSymbol) {
        if (!mNonterminalSymbols.containsKey(abstractNonterminalSymbol.getName())) {
            mNonterminalSymbols.put(abstractNonterminalSymbol.getName(), abstractNonterminalSymbol);
        }
    }

    public AbstractSymbol getSymbol(String name) throws AnalysisException {
        if (mTerminalSymbols.containsKey(name)) {
            return mTerminalSymbols.get(name);
        } else if (mNonterminalSymbols.containsKey(name)) {
            return mNonterminalSymbols.get(name);
        }
        throw new AnalysisException(String.format(AnalysisException.SYMBOL_NOT_EXIST, name), null);
    }
}
