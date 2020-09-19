package cn.alumik.parsetree.parser;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.symbol.AbstractNonterminalSymbol;
import cn.alumik.parsetree.symbol.AbstractSymbol;
import cn.alumik.parsetree.symbol.AbstractTerminalSymbol;

import java.util.*;

public class ParseState {

    private final Set<Item> mItems = new HashSet<>();

    private final Grammar mGrammar;

    public ParseState(Grammar grammar) {
        mGrammar = grammar;
    }

    public void addItem(Item item) {
        mItems.add(item);
    }

    public Set<Item> getItems() {
        return mItems;
    }

    public void makeClosure() {
        final List<Item> itemList = new ArrayList<>(mItems);
        for (int i = 0; i < itemList.size(); i++) {
            final Item item = itemList.get(i);
            if (item.isNotEnded()) {
                final AbstractSymbol abstractSymbol = item.getNextSymbol();
                if (abstractSymbol.getType() == AbstractSymbol.NONTERMINAL) {
                    final List<AbstractSymbol> lookAheadSymbols = new ArrayList<>();
                    for (int j = item.getDot() + 1; j < item.getProduction().to().size(); j++) {
                        lookAheadSymbols.add(item.getProduction().to().get(j));
                    }
                    lookAheadSymbols.add(item.getLookAhead());
                    final Set<AbstractTerminalSymbol> headList = getHeadSet(lookAheadSymbols);
                    for (final Production production : ((AbstractNonterminalSymbol) abstractSymbol).getProductions()) {
                        for (final AbstractTerminalSymbol lookAheadSymbol : headList) {
                            final Item newItem = new Item(production, lookAheadSymbol);
                            if (!mItems.contains(newItem)) {
                                mItems.add(newItem);
                                itemList.add(newItem);
                            }
                        }
                    }
                }
            }
        }
    }

    private Set<AbstractTerminalSymbol> getHeadSet(List<AbstractSymbol> abstractSymbols) {
        final Set<AbstractTerminalSymbol> headSet = new HashSet<>();
        for (final AbstractSymbol abstractSymbol : abstractSymbols) {
            if (abstractSymbol.getType() == AbstractSymbol.NONTERMINAL) {
                headSet.addAll(((AbstractNonterminalSymbol) abstractSymbol).getFirstSet());
                if (!((AbstractNonterminalSymbol) abstractSymbol).isNullable()) {
                    break;
                }
            } else {
                headSet.add((AbstractTerminalSymbol) abstractSymbol);
                if (!abstractSymbol.getName().equals(AbstractTerminalSymbol.NULL)) {
                    break;
                }
            }
        }
        try {
            headSet.remove(mGrammar.getSymbolPool().getTerminalSymbol(AbstractTerminalSymbol.NULL));
        } catch (AnalysisException e) {
            e.printStackTrace();
        }
        return headSet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParseState) {
            final ParseState parseState = (ParseState) obj;
            return mItems.size() == parseState.mItems.size() && mItems.containsAll(parseState.mItems);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (final Item item : mItems) {
            hash ^= item.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Items in this state:\n");
        for (final Item item : mItems) {
            stringBuilder.append(item);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
