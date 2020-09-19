package cn.alumik.parsetree.lexer;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.exception.ParsingException;
import cn.alumik.parsetree.lexer.fsm.FSMState;
import cn.alumik.parsetree.lexer.fsm.NFA;
import cn.alumik.parsetree.parser.Grammar;
import cn.alumik.parsetree.parser.Transition;
import cn.alumik.parsetree.symbol.*;

import java.util.*;

abstract class AbstractRegex {

    private final String mAcceptingRule;

    private final String mRegex;

    protected final Lexer mLexer;

    private NFA mNfa;

    private Grammar mGrammar;

    public AbstractRegex(String acceptingRule, String regex, Lexer lexer) throws ParsingException, AnalysisException {
        initGrammar();
        mAcceptingRule = acceptingRule;
        mRegex = regex;
        mLexer = lexer;
        initNFA();
    }

    public String getRegex() {
        return mRegex;
    }

    public NFA getNFA() {
        return mNfa;
    }

    public Grammar getGrammar() {
        return mGrammar;
    }

    public void setGrammar(Grammar grammar) {
        mGrammar = grammar;
    }

    private void initNFA() throws ParsingException, AnalysisException {
        final Map<Integer, Map<AbstractSymbol, Transition>> parseTable = mGrammar.getParseTable().getTable();
        final List<Symbol> symbols = getSymbols();
        final AbstractSymbol startSymbol = mGrammar.getStartSymbol();

        final Stack<Integer> stateStack = new Stack<>();
        final Stack<Symbol> symbolStack = new Stack<>();
        final Stack<NFA> nfaStack = new Stack<>();

        stateStack.push(0);
        int index = 0;

        while (index != symbols.size() - 1 || !symbols.get(index).getAbstractSymbol().equals(startSymbol)) {
            final int currentState = stateStack.peek();
            final Symbol currentSymbol = index < symbols.size() ? symbols.get(index)
                    : new TerminalSymbol(mGrammar.getSymbolPool().getTerminalSymbol(AbstractTerminalSymbol.END));
            final Transition transition = parseTable.get(currentState).get(currentSymbol.getAbstractSymbol());

            if (transition == null) {
                throw new ParsingException(
                        String.format(ParsingException.PARSING_ERROR, index + 1, currentSymbol), null);
            } else {
                switch (transition.getAction()) {
                    case Transition.SHIFT:
                        nfaStack.push(new NFA(mLexer));
                        symbolStack.push(currentSymbol);
                    case Transition.GOTO:
                        stateStack.push(transition.getNextState());
                        index++;
                        break;
                    case Transition.REDUCE:
                        final RegexProduction regexProduction = (RegexProduction) transition.getReduceProduction();
                        final List<NFA> nodes = new ArrayList<>();
                        final List<Symbol> children = new ArrayList<>();
                        for (final AbstractSymbol ignored : regexProduction.to()) {
                            stateStack.pop();
                            nodes.add(nfaStack.pop());
                            children.add(symbolStack.pop());
                        }
                        Collections.reverse(nodes);
                        Collections.reverse(children);
                        nfaStack.push(regexProduction.getNFA(nodes, children));
                        final Symbol newSymbol =
                                new NonterminalSymbol((AbstractNonterminalSymbol) regexProduction.from());
                        symbolStack.push(newSymbol);
                        index--;
                        symbols.set(index, newSymbol);
                }
            }
        }
        if (nfaStack.size() != 1) {
            throw new ParsingException(String.format(ParsingException.PARSING_NOT_COMPLETE, nfaStack), null);
        } else {
            mNfa = nfaStack.pop();
            for (final FSMState state : mNfa.getFinalStates()) {
                state.addAcceptingRule(mAcceptingRule);
            }
        }
    }

    protected abstract void initGrammar() throws AnalysisException;

    protected abstract List<Symbol> getSymbols() throws ParsingException, AnalysisException;
}
