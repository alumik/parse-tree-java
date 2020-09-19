package cn.alumik.parsetree.parser;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.symbol.AbstractNonterminalSymbol;
import cn.alumik.parsetree.symbol.AbstractSymbol;
import cn.alumik.parsetree.symbol.AbstractTerminalSymbol;
import cn.alumik.parsetree.symbol.SymbolPool;
import cn.alumik.parsetree.util.Config;

import java.util.*;

public class Grammar {

    public static final String START_SYMBOL = "_S";

    private AbstractNonterminalSymbol mStartSymbol;

    final private SymbolPool mSymbolPool;

    private List<Production> mProductions;

    private ParseTable mParseTable;

    public Grammar(Config config) throws AnalysisException {
        mSymbolPool = new SymbolPool(config.getTerminalSymbols(), config.getNonterminalSymbols());
        initProductions(config.getProductions(), config.getStartSymbol());
    }

    public Grammar(Set<String> terminalSymbols, Set<String> nonterminalSymbols, String startSymbol)
            throws AnalysisException {
        mSymbolPool = new SymbolPool(terminalSymbols, nonterminalSymbols);
        mStartSymbol = mSymbolPool.getNonterminalSymbol(startSymbol);
    }

    public AbstractNonterminalSymbol getStartSymbol() {
        return mStartSymbol;
    }

    public SymbolPool getSymbolPool() {
        return mSymbolPool;
    }

    public List<Production> getProductions() {
        return mProductions;
    }

    public void setProductions(List<? extends Production> productions) {
        this.mProductions = new ArrayList<>(productions);
    }

    public ParseTable getParseTable() {
        return mParseTable;
    }

    public void initParseTable() throws AnalysisException {
        initSymbolProductions();
        initSymbolFirstSet();
        final List<ParseState> stateList = new ArrayList<>();
        final Map<ParseState, Integer> stateMap = new HashMap<>();

        final ParseState startState = new ParseState(this);
        for (final Production production : mProductions) {
            if (production.from().equals(mStartSymbol)) {
                startState.addItem(new Item(production, mSymbolPool.getTerminalSymbol(AbstractTerminalSymbol.END)));
            }
        }
        startState.makeClosure();
        stateList.add(startState);
        stateMap.put(startState, 0);

        ParseTable parseTable = new ParseTable(this);
        for (int i = 0; i < stateList.size(); i++) {
            final Set<Item> items = stateList.get(i).getItems();
            final Map<AbstractSymbol, Set<Item>> groupedItems = new HashMap<>();
            for (final Item item : items) {
                if (item.isNotEnded()) {
                    if (!groupedItems.containsKey(item.getNextSymbol())) {
                        groupedItems.put(item.getNextSymbol(), new HashSet<>());
                    }
                    groupedItems.get(item.getNextSymbol()).add(item);
                } else {
                    if (!groupedItems.containsKey(mSymbolPool.getTerminalSymbol(AbstractTerminalSymbol.NULL))) {
                        groupedItems.put(mSymbolPool.getTerminalSymbol(AbstractTerminalSymbol.NULL), new HashSet<>());
                    }
                    groupedItems.get(mSymbolPool.getTerminalSymbol(AbstractTerminalSymbol.NULL)).add(item);
                }
            }
            for (final AbstractSymbol abstractSymbol : groupedItems.keySet()) {
                if (abstractSymbol.equals(mSymbolPool.getTerminalSymbol(AbstractTerminalSymbol.NULL))) {
                    for (final Item item : groupedItems.get(abstractSymbol)) {
                        parseTable.addTransition(i, item.getLookAhead(), item.getProduction());
                        if (item.getLookAhead().equals(mSymbolPool.getTerminalSymbol(AbstractTerminalSymbol.END))
                                && item.getProduction().from().equals(mStartSymbol)) {
                            parseTable.setAcceptState(i);
                        }
                    }
                } else {
                    final ParseState parseState = new ParseState(this);
                    for (final Item item : groupedItems.get(abstractSymbol)) {
                        parseState.addItem(item.getNextItem());
                    }
                    parseState.makeClosure();
                    if (!stateMap.containsKey(parseState)) {
                        stateMap.put(parseState, stateList.size());
                        stateList.add(parseState);
                    }
                    parseTable.addTransition(i, abstractSymbol, stateMap.get(parseState));
                }
            }
        }
        mParseTable = parseTable;
    }

    private void initProductions(List<String> prodStrList, String startSymbol) throws AnalysisException {
        mProductions = new ArrayList<>();
        for (final String prodStr : prodStrList) {
            mProductions.add(Production.fromString(prodStr, this));
        }
        augmentGrammar(startSymbol);
    }

    private void augmentGrammar(String startSymbol) throws AnalysisException {
        if (!mSymbolPool.getNonterminalSymbolNames().contains(startSymbol)) {
            throw new AnalysisException(AnalysisException.START_SYMBOL_NOT_TERMINAL, null);
        }
        final AbstractNonterminalSymbol oldStartSymbol = mSymbolPool.getNonterminalSymbol(startSymbol);
        final AbstractNonterminalSymbol newStartSymbol = new AbstractNonterminalSymbol(START_SYMBOL);
        mSymbolPool.addNonterminalSymbol(newStartSymbol);
        final List<AbstractSymbol> to = new ArrayList<>();
        to.add(oldStartSymbol);
        mProductions.add(0, new Production(newStartSymbol, to));
        mStartSymbol = newStartSymbol;
    }

    private void initSymbolProductions() {
        for (final Production production : mProductions) {
            final AbstractNonterminalSymbol from = (AbstractNonterminalSymbol) production.from();
            from.getProductions().add(production);
        }
    }

    private void initSymbolNullable() throws AnalysisException {
        Set<AbstractSymbol> tmpNullableSymbols = new HashSet<>();
        final Set<AbstractSymbol> nullableSymbols = new HashSet<>();
        tmpNullableSymbols.add(mSymbolPool.getTerminalSymbol(AbstractTerminalSymbol.NULL));

        final Map<AbstractSymbol, Set<Production>> symbolProductions = new HashMap<>();
        for (final Production production : mProductions) {
            final AbstractSymbol from = production.from();
            if (!symbolProductions.containsKey(from)) {
                symbolProductions.put(from, new HashSet<>());
            }
            symbolProductions.get(from).add(new Production(production));
        }

        while (!tmpNullableSymbols.isEmpty()) {
            final Set<AbstractSymbol> nextTmpNullableSymbols = new HashSet<>();
            for (final AbstractSymbol from : symbolProductions.keySet()) {
                for (final Production production : symbolProductions.get(from)) {
                    final List<AbstractSymbol> to = production.to();
                    for (final AbstractSymbol abstractSymbol : tmpNullableSymbols) {
                        to.remove(abstractSymbol);
                    }
                    if (to.size() <= 0) {
                        nextTmpNullableSymbols.add(from);
                        break;
                    }
                }
            }
            symbolProductions.keySet().removeAll(nextTmpNullableSymbols);
            nullableSymbols.addAll(nextTmpNullableSymbols);
            tmpNullableSymbols = nextTmpNullableSymbols;
        }
        for (final AbstractSymbol abstractSymbol : nullableSymbols) {
            ((AbstractNonterminalSymbol) abstractSymbol).setNullable(true);
        }
    }

    private void initSymbolFirstSet() throws AnalysisException {
        initSymbolNullable();
        final Map<AbstractNonterminalSymbol, Set<AbstractNonterminalSymbol>> connections = new HashMap<>();
        final Map<AbstractNonterminalSymbol, Set<AbstractTerminalSymbol>> firstSets = new HashMap<>();
        final Map<AbstractNonterminalSymbol, Set<AbstractTerminalSymbol>> tmpFirstSets = new HashMap<>();
        final AbstractTerminalSymbol nullSymbol = mSymbolPool.getTerminalSymbol(AbstractTerminalSymbol.NULL);
        for (final Production production : mProductions) {
            for (final AbstractSymbol abstractSymbol : production.to()) {
                final AbstractNonterminalSymbol from = (AbstractNonterminalSymbol) production.from();
                if (abstractSymbol.getType() == AbstractSymbol.NONTERMINAL) {
                    final AbstractNonterminalSymbol abstractNonterminalSymbol = (AbstractNonterminalSymbol) abstractSymbol;
                    if (!connections.containsKey(abstractNonterminalSymbol)) {
                        connections.put(abstractNonterminalSymbol, new HashSet<>());
                    }
                    connections.get(abstractNonterminalSymbol).add(from);
                    if (!(abstractNonterminalSymbol.isNullable())) {
                        break;
                    }
                } else if (!abstractSymbol.equals(nullSymbol)) {
                    final AbstractTerminalSymbol abstractTerminalSymbol = (AbstractTerminalSymbol) abstractSymbol;
                    if (!tmpFirstSets.containsKey(from)) {
                        tmpFirstSets.put(from, new HashSet<>());
                    }
                    tmpFirstSets.get(from).add(abstractTerminalSymbol);
                    break;
                } else {
                    break;
                }
            }
        }
        while (!tmpFirstSets.isEmpty()) {
            final Map<AbstractNonterminalSymbol, Set<AbstractTerminalSymbol>> newTmpFirstSets = new HashMap<>();
            for (final AbstractNonterminalSymbol abstractNonterminalSymbol : tmpFirstSets.keySet()) {
                if (!firstSets.containsKey(abstractNonterminalSymbol)) {
                    firstSets.put(abstractNonterminalSymbol, new HashSet<>());
                }
                firstSets.get(abstractNonterminalSymbol).addAll(tmpFirstSets.get(abstractNonterminalSymbol));
                if (connections.containsKey(abstractNonterminalSymbol)) {
                    for (final AbstractNonterminalSymbol target : connections.get(abstractNonterminalSymbol)) {
                        if (!newTmpFirstSets.containsKey(target)) {
                            newTmpFirstSets.put(target, new HashSet<>());
                        }
                        newTmpFirstSets.get(target).addAll(tmpFirstSets.get(abstractNonterminalSymbol));
                    }
                }
            }
            tmpFirstSets.clear();
            for (final AbstractNonterminalSymbol abstractNonterminalSymbol : newTmpFirstSets.keySet()) {
                if (firstSets.containsKey(abstractNonterminalSymbol)) {
                    newTmpFirstSets.get(abstractNonterminalSymbol).removeAll(firstSets.get(abstractNonterminalSymbol));
                }
                if (newTmpFirstSets.get(abstractNonterminalSymbol).size() > 0) {
                    tmpFirstSets.put(abstractNonterminalSymbol, newTmpFirstSets.get(abstractNonterminalSymbol));
                }
            }
        }
        for (final AbstractNonterminalSymbol abstractNonterminalSymbol : mSymbolPool.getNonterminalSymbols()) {
            if (!firstSets.containsKey(abstractNonterminalSymbol)) {
                firstSets.put(abstractNonterminalSymbol, new HashSet<>());
            }
            if (abstractNonterminalSymbol.isNullable()) {
                firstSets.get(abstractNonterminalSymbol).add(nullSymbol);
            }
            abstractNonterminalSymbol.setFirstSet(firstSets.get(abstractNonterminalSymbol));
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("Productions in this grammar:");
        for (final Production production : mProductions) {
            stringBuilder.append("\n");
            stringBuilder.append(production);
        }
        return stringBuilder.toString();
    }
}
