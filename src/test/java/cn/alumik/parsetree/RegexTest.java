package cn.alumik.parsetree;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.exception.ParsingException;
import cn.alumik.parsetree.lexer.Lexer;
import cn.alumik.parsetree.lexer.fsm.DFA;
import cn.alumik.parsetree.lexer.fsm.FSMState;
import cn.alumik.parsetree.lexer.fsm.NFA;
import cn.alumik.parsetree.parser.Parser;
import cn.alumik.parsetree.util.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RegexTest {

    private final Config mConfig = new Config("lexer.yml");

    private final Parser mParser = new Parser(mConfig);

    private final Lexer mLexer = new Lexer(mConfig, mParser);

    public RegexTest() throws ParsingException, AnalysisException {
    }

    private NFA makeNFA1() {
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
        final FSMState s10 = new FSMState(mLexer);

        s0.addTransition('\0', s1);
        s1.addTransition('\0', s5);
        s1.addTransition('\0', s6);
        s5.addTransition('a', s2);
        s6.addTransition('b', s3);
        s2.addTransition('\0', s4);
        s3.addTransition('\0', s4);
        s4.addTransition('\0', s7);
        s7.addTransition('a', s8);
        s8.addTransition('b', s9);
        s9.addTransition('b', s10);
        s0.addTransition('\0', s7);
        s4.addTransition('\0', s1);

        s10.setFinal(true);
        s10.addAcceptingRule("(a|b)*abb");

        final NFA nfa = new NFA(s0, mLexer);
        nfa.addFinalState(s10);
        return nfa;
    }

    private NFA makeNFA2() {
        final FSMState s0 = new FSMState(mLexer);
        final FSMState s1 = new FSMState(mLexer);
        final FSMState s2 = new FSMState(mLexer);
        final FSMState s3 = new FSMState(mLexer);
        final FSMState s4 = new FSMState(mLexer);
        final FSMState s5 = new FSMState(mLexer);
        final FSMState s6 = new FSMState(mLexer);
        final FSMState s7 = new FSMState(mLexer);
        final FSMState s8 = new FSMState(mLexer);

        s0.addTransition('\0', s1);
        s1.addTransition('a', s2);
        s0.addTransition('\0', s3);
        s3.addTransition('a', s4);
        s4.addTransition('b', s5);
        s5.addTransition('b', s6);
        s0.addTransition('\0', s7);
        s7.addTransition('a', s7);
        s7.addTransition('b', s8);
        s8.addTransition('b', s8);

        s2.setFinal(true);
        s6.setFinal(true);
        s8.setFinal(true);
        s2.addAcceptingRule("a");
        s6.addAcceptingRule("abb");
        s8.addAcceptingRule("a*b+");

        final NFA nfa = new NFA(s0, mLexer);
        nfa.addFinalState(s2);
        nfa.addFinalState(s6);
        nfa.addFinalState(s8);
        return nfa;
    }

    @Test
    public void testMatchString() throws IOException {
        final Map<String, String> acceptingRules = new LinkedHashMap<>();
        acceptingRules.put("(a|b)*abb", "(a|b)*abb");
        mLexer.setAcceptingRule(acceptingRules);

        final NFA nfa = makeNFA1();
        nfa.draw("out/1_nfa.png");

        final DFA dfa = new DFA(nfa);
        dfa.draw("out/1_dfa.png");

        assertEquals(new AbstractMap.SimpleEntry<>("", 1), dfa.match("abdsffgabb"));
        assertEquals(new AbstractMap.SimpleEntry<>("", 1), dfa.match("abab"));
        assertEquals(new AbstractMap.SimpleEntry<>("(a|b)*abb", 12), dfa.match("abbbababbabb"));
        assertEquals(new AbstractMap.SimpleEntry<>("(a|b)*abb", 3), dfa.match("abb"));
        assertEquals(new AbstractMap.SimpleEntry<>("(a|b)*abb", 6), dfa.match("abbabb"));
        assertEquals(new AbstractMap.SimpleEntry<>("(a|b)*abb", 4), dfa.match("aabbefg"));
    }

    @Test
    public void testMergeNFA() throws IOException {
        final Map<String, String> acceptingRules = new LinkedHashMap<>();
        acceptingRules.put("a", "a");
        acceptingRules.put("abb", "abb");
        acceptingRules.put("a*b+", "a*b+");
        mLexer.setAcceptingRule(acceptingRules);

        final NFA nfa = makeNFA2();
        nfa.draw("out/2_nfa.png");

        final DFA dfa = new DFA(nfa);
        dfa.draw("out/2_dfa.png");

        assertEquals(new AbstractMap.SimpleEntry<>("abb", 3), dfa.match("abb"));
        assertEquals(new AbstractMap.SimpleEntry<>("a*b+", 4), dfa.match("abbb"));
        assertEquals(new AbstractMap.SimpleEntry<>("a", 1), dfa.match("aefg"));
        assertEquals(new AbstractMap.SimpleEntry<>("", 1), dfa.match("efg"));
    }
}
