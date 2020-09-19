package cn.alumik.parsetree;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.exception.ParsingException;
import cn.alumik.parsetree.lexer.Lexer;
import cn.alumik.parsetree.lexer.fsm.DFA;
import cn.alumik.parsetree.lexer.fsm.FSMState;
import cn.alumik.parsetree.lexer.fsm.NFA;
import cn.alumik.parsetree.parser.Parser;
import cn.alumik.parsetree.symbol.AbstractTerminalSymbol;
import cn.alumik.parsetree.symbol.TerminalSymbol;
import cn.alumik.parsetree.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LexerTest {

    private final Config mConfig = new Config("lexer.yml");

    private final Parser mParser = new Parser(mConfig);

    private final Lexer mLexer = new Lexer(mConfig, mParser);

    private final ByteArrayOutputStream mOutContent = new ByteArrayOutputStream();

    private final PrintStream mOriginalOut = System.out;

    public LexerTest() throws ParsingException, AnalysisException {
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(mOutContent));
    }

    @Before
    public void setupLexer() {
        mLexer.setDfa(new DFA(makeNFA()));
    }

    @After
    public void restoreStreams() {
        System.setOut(mOriginalOut);
    }

    private NFA makeNFA() {
        final FSMState s0 = new FSMState(mLexer);
        final FSMState s1 = new FSMState(mLexer);
        final FSMState s2 = new FSMState(mLexer);
        final FSMState s3 = new FSMState(mLexer);
        final FSMState s4 = new FSMState(mLexer);
        final FSMState s5 = new FSMState(mLexer);
        final FSMState s6 = new FSMState(mLexer);
        final FSMState s7 = new FSMState(mLexer);
        final FSMState s8 = new FSMState(mLexer);
        final FSMState s9 = new FSMState(mLexer);

        s0.addTransition('\0', s1);
        s1.addTransition('a', s2);
        s0.addTransition('\0', s3);
        s3.addTransition('a', s4);
        s4.addTransition('b', s5);
        s5.addTransition('c', s6);
        s0.addTransition('\0', s7);
        s7.addTransition('a', s8);
        s8.addTransition('b', s9);

        s2.setFinal(true);
        s5.setFinal(true);
        s6.setFinal(true);
        s9.setFinal(true);
        s2.addAcceptingRule("A");
        s5.addAcceptingRule("AB2");
        s6.addAcceptingRule("ABC");
        s9.addAcceptingRule("AB1");

        final NFA nfa = new NFA(s0, mLexer);
        nfa.addFinalState(s2);
        nfa.addFinalState(s5);
        nfa.addFinalState(s6);
        nfa.addFinalState(s9);
        return nfa;
    }


    @Test(expected = ParsingException.class)
    public void testLexWithPredefinedDFA_ExceptionFront() throws ParsingException, AnalysisException {
        mLexer.lex("babbebabc");
    }

    @Test(expected = ParsingException.class)
    public void testLexWithPredefinedDFA_ExceptionBack() throws ParsingException, AnalysisException {
        mLexer.lex("aababcabefg");
    }

    @Test
    public void testLexWithIgnoredSymbolsAndPredefinedDFA() throws ParsingException, AnalysisException {
        final List<TerminalSymbol> result = new ArrayList<>();
        TerminalSymbol terminalSymbol1 = new TerminalSymbol(new AbstractTerminalSymbol("AB2"));
        terminalSymbol1.setValue("ab");
        result.add(terminalSymbol1);
        TerminalSymbol terminalSymbol2 = new TerminalSymbol(new AbstractTerminalSymbol("ABC"));
        terminalSymbol2.setValue("abc");
        result.add(terminalSymbol2);
        TerminalSymbol terminalSymbol3 = new TerminalSymbol(new AbstractTerminalSymbol("AB2"));
        terminalSymbol3.setValue("ab");
        result.add(terminalSymbol3);
        TerminalSymbol terminalSymbol4 = new TerminalSymbol(new AbstractTerminalSymbol("AB2"));
        terminalSymbol4.setValue("ab");
        result.add(terminalSymbol4);

        assertEquals(result, mLexer.lex("aabaabcabaab"));
        assertEquals(
                "AB2: ab\nABC: abc\nAB2: ab\nAB2: ab\n",
                mOutContent.toString().replace("\r", ""));
    }
}
