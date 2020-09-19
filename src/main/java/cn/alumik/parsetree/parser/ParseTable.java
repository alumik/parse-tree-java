package cn.alumik.parsetree.parser;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.symbol.AbstractSymbol;
import cn.alumik.parsetree.symbol.AbstractTerminalSymbol;

import java.util.*;

public class ParseTable {

    private final Map<Integer, Map<AbstractSymbol, Transition>> mTableMap = new HashMap<>();

    private int mAcceptState;

    private final Grammar mGrammar;

    public ParseTable(Grammar grammar) {
        mGrammar = grammar;
    }

    public Map<Integer, Map<AbstractSymbol, Transition>> getTable() {
        return mTableMap;
    }

    public int getAcceptState() {
        return mAcceptState;
    }

    public void setAcceptState(int acceptState) {
        this.mAcceptState = acceptState;
    }

    public void addTransition(int stateIndex, AbstractSymbol abstractSymbol, int nextStateIndex) {
        Transition transition;
        if (abstractSymbol.getType() == AbstractSymbol.NONTERMINAL) {
            transition = new Transition(Transition.GOTO, nextStateIndex);
        } else {
            transition = new Transition(Transition.SHIFT, nextStateIndex);
        }
        if (!mTableMap.containsKey(stateIndex)) {
            mTableMap.put(stateIndex, new HashMap<>());
        }
        mTableMap.get(stateIndex).put(abstractSymbol, transition);
    }

    public void addTransition(int stateIndex, AbstractSymbol abstractSymbol, Production production) {
        final Transition transition = new Transition(production, mGrammar.getProductions().indexOf(production));
        if (!mTableMap.containsKey(stateIndex)) {
            mTableMap.put(stateIndex, new HashMap<>());
        }
        mTableMap.get(stateIndex).put(abstractSymbol, transition);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Set<AbstractSymbol> abstractSymbolSet = new HashSet<>();
        for (final int i : mTableMap.keySet()) {
            abstractSymbolSet.addAll(mTableMap.get(i).keySet());
        }
        final List<Integer> sortedStateIndices = new ArrayList<>(mTableMap.keySet());
        Collections.sort(sortedStateIndices);
        final List<AbstractSymbol> abstractSymbolList = new ArrayList<>(abstractSymbolSet);
        final List<AbstractSymbol> abstractTerminalSymbols =
                new ArrayList<>(mGrammar.getSymbolPool().getTerminalSymbols());
        final List<AbstractSymbol> abstractNonterminalSymbols =
                new ArrayList<>(mGrammar.getSymbolPool().getNonterminalSymbols());
        final List<AbstractSymbol> listForOrder = new ArrayList<>();
        listForOrder.addAll(abstractTerminalSymbols);
        listForOrder.addAll(abstractNonterminalSymbols);
        abstractSymbolList.sort(Comparator.comparingInt(listForOrder::indexOf));
        for (final AbstractSymbol abstractSymbol : abstractSymbolList) {
            stringBuilder.append("\t");
            stringBuilder.append(abstractSymbol.getName());
        }
        stringBuilder.append("\n");
        for (final int i : sortedStateIndices) {
            stringBuilder.append(i);
            for (final AbstractSymbol abstractSymbol : abstractSymbolList) {
                stringBuilder.append("\t");
                if (mTableMap.get(i).containsKey(abstractSymbol)) {
                    try {
                        if (mAcceptState == i && abstractSymbol.equals(mGrammar.getSymbolPool().getTerminalSymbol(AbstractTerminalSymbol.END))) {
                            stringBuilder.append("acc");
                        } else {
                            stringBuilder.append(mTableMap.get(i).get(abstractSymbol));
                        }
                    } catch (AnalysisException e) {
                        e.printStackTrace();
                    }
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
