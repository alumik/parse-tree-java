package cn.alumik.parsetree.lexer;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.exception.ParsingException;
import cn.alumik.parsetree.lexer.fsm.DFA;
import cn.alumik.parsetree.lexer.fsm.FSMState;
import cn.alumik.parsetree.lexer.fsm.NFA;
import cn.alumik.parsetree.symbol.TerminalSymbol;
import cn.alumik.parsetree.util.Config;

import java.util.*;

public class Lexer {

    private final Config mConfig;

    private DFA mDfa;

    private Map<String, String> mAcceptingRules = new LinkedHashMap<>();

    private Set<String> mIgnoredSymbols = new HashSet<>();

    public Lexer(Config config) throws AnalysisException, ParsingException {
        mConfig = config;
        initAcceptingRules();
        initDFA();
    }

    public void setDfa(DFA dfa) {
        mDfa = dfa;
    }

    public Map<String, String> getAcceptingRules() {
        return mAcceptingRules;
    }

    public void setAcceptingRule(Map<String, String> acceptingRules) {
        mAcceptingRules = acceptingRules;
    }

    public Set<String> getIgnoredSymbols() {
        return mIgnoredSymbols;
    }

    public List<TerminalSymbol> lex(String input) throws ParsingException {
        final List<TerminalSymbol> lexResult = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder(input);
        while (stringBuilder.length() != 0) {
            final Map.Entry<String, Integer> result = mDfa.match(stringBuilder.toString());
            if (result.getKey().equals("")) {
                throw new ParsingException(ParsingException.LEXICAL_ANALYSIS_FAILED, null);
            }
            if (!mIgnoredSymbols.contains(result.getKey())) {
                final String value = stringBuilder.substring(0, result.getValue());
                System.out.println(result.getKey() + ": " + value);
                final TerminalSymbol terminalSymbol = new TerminalSymbol(value);
                lexResult.add(terminalSymbol);
            }
            stringBuilder.delete(0, result.getValue());
        }
        return lexResult;
    }

    private void initAcceptingRules() {
        mAcceptingRules = mConfig.getAcceptingRules();
        mIgnoredSymbols = mConfig.getIgnoredSymbols();
    }

    private void initDFA() throws ParsingException, AnalysisException {
        final List<NFA> nfas = new ArrayList<>();
        for (final Map.Entry<String, String> rule : mAcceptingRules.entrySet()) {
            final Regex regex = new Regex(rule.getKey(), rule.getValue(), this);
            nfas.add(regex.getNFA());
        }
        final FSMState startState = new FSMState(this);
        for (final NFA nfa : nfas) {
            startState.addTransition('\0', nfa.getStartState());
        }
        mDfa = new DFA(new NFA(startState, this));
    }
}
