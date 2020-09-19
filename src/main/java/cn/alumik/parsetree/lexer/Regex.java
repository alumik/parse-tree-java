package cn.alumik.parsetree.lexer;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.exception.ParsingException;
import cn.alumik.parsetree.lexer.fsm.FSMState;
import cn.alumik.parsetree.lexer.fsm.NFA;
import cn.alumik.parsetree.parser.Grammar;
import cn.alumik.parsetree.parser.Production;
import cn.alumik.parsetree.symbol.AbstractTerminalSymbol;
import cn.alumik.parsetree.symbol.Symbol;
import cn.alumik.parsetree.symbol.TerminalSymbol;

import java.util.*;

public class Regex extends AbstractRegex {

    private static final Set<String> TERMINAL_SYMBOLS = new HashSet<>(
            Arrays.asList("|", "(", ")", "*", "+", "[", "]", "-", "char", "^", "."));

    private static final Set<String> NONTERMINAL_SYMBOLS = new HashSet<>(
            Arrays.asList("E", "T", "F", "Fx", "Fxs"));

    private static final String START_SYMBOL = "E";

    public Regex(String acceptingRule, String regex, Lexer lexer) throws ParsingException, AnalysisException {
        super(acceptingRule, regex, lexer);
    }

    @Override
    protected void initGrammar() throws AnalysisException {
        final List<RegexProduction> regexRules = new ArrayList<>();
        final Grammar grammar = new Grammar(TERMINAL_SYMBOLS, NONTERMINAL_SYMBOLS, START_SYMBOL);

        regexRules.add(new RegexProduction(Production.fromString("E -> E | T", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final FSMState startState = new FSMState(mLexer);
                final FSMState finalState = new FSMState(mLexer);
                finalState.setFinal(true);
                startState.addTransition('\0', nodes.get(0).getStartState());
                startState.addTransition('\0', nodes.get(2).getStartState());
                for (final FSMState state : nodes.get(0).getFinalStates()) {
                    state.setFinal(false);
                    state.addTransition('\0', finalState);
                }
                for (final FSMState state : nodes.get(2).getFinalStates()) {
                    state.setFinal(false);
                    state.addTransition('\0', finalState);
                }
                final NFA nfa = new NFA(startState, mLexer);
                nfa.addFinalState(finalState);
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("E -> T", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(0);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("T -> T F", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final FSMState startState = new FSMState(mLexer);
                final FSMState finalState = new FSMState(mLexer);
                finalState.setFinal(true);
                startState.addTransition('\0', nodes.get(0).getStartState());
                for (final FSMState state : nodes.get(0).getFinalStates()) {
                    state.setFinal(false);
                    state.addTransition('\0', nodes.get(1).getStartState());
                }
                for (final FSMState state : nodes.get(1).getFinalStates()) {
                    state.setFinal(false);
                    state.addTransition('\0', finalState);
                }
                final NFA nfa = new NFA(startState, mLexer);
                nfa.addFinalState(finalState);
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("T -> F", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(0);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> ( E )", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(1);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> F *", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final FSMState startState = new FSMState(mLexer);
                final FSMState finalState = new FSMState(mLexer);
                finalState.setFinal(true);
                startState.addTransition('\0', nodes.get(0).getStartState());
                for (final FSMState state : nodes.get(0).getFinalStates()) {
                    state.addTransition('\0', nodes.get(0).getStartState());
                    state.addTransition('\0', finalState);
                    state.setFinal(false);
                }
                startState.addTransition('\0', finalState);
                final NFA nfa = new NFA(startState, mLexer);
                nfa.addFinalState(finalState);
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> F +", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final FSMState startState = new FSMState(mLexer);
                final FSMState finalState = new FSMState(mLexer);
                finalState.setFinal(true);
                startState.addTransition('\0', nodes.get(0).getStartState());
                for (final FSMState state : nodes.get(0).getFinalStates()) {
                    state.addTransition('\0', nodes.get(0).getStartState());
                    state.addTransition('\0', finalState);
                    state.setFinal(false);
                }
                final NFA nfa = new NFA(startState, mLexer);
                nfa.addFinalState(finalState);
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> Fx", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(0);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fx -> .", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final FSMState startState = new FSMState(mLexer);
                for (char c = 1; c < 127; ++c) {
                    startState.addTransition(c, nodes.get(0).getStartState());
                }
                nodes.get(0).getStartState().setFinal(true);
                final NFA nfa = new NFA(startState, mLexer);
                nfa.addFinalState(nodes.get(0).getStartState());
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fx -> char", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final FSMState startState = new FSMState(mLexer);
                startState.addTransition((children.get(0).getValue()).charAt(0), nodes.get(0).getStartState());
                nodes.get(0).getStartState().setFinal(true);
                final NFA nfa = new NFA(startState, mLexer);
                nfa.addFinalState(nodes.get(0).getStartState());
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fx -> char - char", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final char tmpStartChar = (children.get(0).getValue()).charAt(0);
                final char tmpEndChar = (children.get(2).getValue()).charAt(0);
                final char startChar = (char) Math.min(tmpStartChar, tmpEndChar);
                final char endChar = (char) Math.max(tmpStartChar, tmpEndChar);

                final FSMState startState = new FSMState(mLexer);
                for (char c = startChar; c <= endChar; c++) {
                    startState.addTransition(c, nodes.get(0).getStartState());
                }
                nodes.get(0).getStartState().setFinal(true);
                final NFA nfa = new NFA(startState, mLexer);
                nfa.addFinalState(nodes.get(0).getStartState());
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fxs -> Fxs Fx", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final NFA nfa = nodes.get(0);
                final FSMState startState = nfa.getStartState();
                startState.getTransitions().putAll(nodes.get(1).getStartState().getTransitions());
                for (final FSMState state : nodes.get(1).getFinalStates()) {
                    for (final FSMState state1 : nodes.get(0).getFinalStates()) {
                        state.addTransition('\0', state1);
                        state.setFinal(false);
                    }
                }
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fxs -> Fx", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(0);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> [ Fxs ]", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(1);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> [ ^ Fxs ]", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                final FSMState startState = new FSMState(mLexer);
                final Set<Character> chars = new HashSet<>();
                for (char c = 1; c < 127; ++c) {
                    chars.add(c);
                }
                chars.removeAll(nodes.get(2).getStartState().getTransitions().keySet());
                final FSMState nextState = nodes.get(2).getStartState().getTransitions().values()
                        .iterator().next().iterator().next();
                for (final char c : chars) {
                    startState.addTransition(c, nextState);
                }
                final NFA nfa = new NFA(startState, mLexer);
                nfa.setFinalStates(nodes.get(2).getFinalStates());
                return nfa;
            }
        });

        grammar.setProductions(regexRules);
        grammar.initParseTable();
        setGrammar(grammar);
    }

    @Override
    protected List<Symbol> getSymbols() throws ParsingException, AnalysisException {
        final List<Symbol> symbols = new ArrayList<>();
        final String regex = getRegex();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            switch (c) {
                case '\\':
                    i++;
                    if (i >= regex.length()) {
                        throw new ParsingException(ParsingException.INVALID_REGEX, null);
                    }
                    c = regex.charAt(i);
                    switch (c) {
                        case '-', '+', '*', '|', '[', ']', '(', '^', '.', ')' -> symbols.add(makeSymbol(c));
                        case 'r' -> symbols.add(makeSymbol('\r'));
                        case 'n' -> symbols.add(makeSymbol('\n'));
                        case 't' -> symbols.add(makeSymbol('\t'));
                        case 'f' -> symbols.add(makeSymbol('\f'));
                        case '\\' -> symbols.add(makeSymbol('\\'));
                    }
                    break;
                case '-':
                case '+':
                case '*':
                case '|':
                case '[':
                case ']':
                case '(':
                case '^':
                case '.':
                case ')':
                    final AbstractTerminalSymbol abstractTerminalSymbol = getGrammar()
                            .getSymbolPool()
                            .getTerminalSymbol(String.valueOf(c));
                    final TerminalSymbol terminalSymbol = new TerminalSymbol(abstractTerminalSymbol);
                    symbols.add(terminalSymbol);
                    break;
                case '\n':
                case '\r':
                    break;
                default:
                    symbols.add(makeSymbol(c));
            }
        }
        return symbols;
    }

    private TerminalSymbol makeSymbol(char c) throws AnalysisException {
        final AbstractTerminalSymbol abstractTerminalSymbol =
                getGrammar().getSymbolPool().getTerminalSymbol("char");
        final TerminalSymbol terminalSymbol = new TerminalSymbol(String.valueOf(c));
        terminalSymbol.setAbstractSymbol(abstractTerminalSymbol);
        return terminalSymbol;
    }
}
