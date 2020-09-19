package cn.alumik.parsetree;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.exception.ParsingException;
import cn.alumik.parsetree.lexer.Lexer;
import cn.alumik.parsetree.parser.Parser;
import cn.alumik.parsetree.symbol.AbstractNonterminalSymbol;
import cn.alumik.parsetree.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ParseTreeAppTest {

    private Lexer mLexer;

    private Parser mParser;

    private final ByteArrayOutputStream mOutContent = new ByteArrayOutputStream();

    private final PrintStream mOriginalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(mOutContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(mOriginalOut);
    }

    private void makeLexerAndParser(String configName) throws AnalysisException, ParsingException {
        final Config config = new Config(configName);
        mParser = new Parser(config);
        mLexer = new Lexer(config, mParser);
    }

    @Test
    public void testInitFirstSets() throws AnalysisException, ParsingException {
        makeLexerAndParser("first_set.yml");

        for (final AbstractNonterminalSymbol symbol : mParser.getGrammar().getSymbolPool().getNonterminalSymbols()) {
            System.out.println(symbol);
            System.out.println(symbol.getFirstSet());
            System.out.println();
        }

        assertEquals("""
                        Nonterminal symbol: A
                        [Terminal symbol: a, Terminal symbol: b, Terminal symbol: c, Terminal symbol: d, Terminal symbol: g]

                        Nonterminal symbol: B
                        [Terminal symbol: b, Terminal symbol: null]

                        Nonterminal symbol: C
                        [Terminal symbol: a, Terminal symbol: c, Terminal symbol: d]

                        Nonterminal symbol: D
                        [Terminal symbol: d, Terminal symbol: null]

                        Nonterminal symbol: _S
                        [Terminal symbol: a, Terminal symbol: b, Terminal symbol: c, Terminal symbol: d, Terminal symbol: g]

                        Nonterminal symbol: E
                        [Terminal symbol: c, Terminal symbol: g]

                        """,
                mOutContent.toString().replace("\r", ""));
    }

    @Test
    public void testParseCpp() throws AnalysisException, ParsingException {
        makeLexerAndParser("cpp.yml");
        mLexer.lex("""
                int main() {
                    int a = a + 1;
                    cout << a << endl;
                    return 0;
                }
                """);
        assertEquals("""
                        KEYWORD: int
                        IDENTIFIER: main
                        LP: (
                        RP: )
                        LB: {
                        KEYWORD: int
                        IDENTIFIER: a
                        ASSIGN_OP: =
                        IDENTIFIER: a
                        ADD_OP: +
                        INTEGER: 1
                        SEMICOLON: ;
                        IDENTIFIER: cout
                        LSTREAM: <<
                        IDENTIFIER: a
                        LSTREAM: <<
                        IDENTIFIER: endl
                        SEMICOLON: ;
                        KEYWORD: return
                        INTEGER: 0
                        SEMICOLON: ;
                        RB: }
                        """,
                mOutContent.toString().replace("\r", ""));
    }

    @Test
    public void watchParseCalculator() throws ParsingException, AnalysisException, IOException {
        makeLexerAndParser("calculator.yml");
        final String expr = "3*(6+(4/2)-5)+8";
        mParser.parse(mLexer.lex(expr)).draw("out/3_calculator.png");
    }
}
