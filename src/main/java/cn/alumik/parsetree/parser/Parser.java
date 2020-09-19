package cn.alumik.parsetree.parser;

import cn.alumik.parsetree.exception.AnalysisException;
import cn.alumik.parsetree.exception.ParsingException;
import cn.alumik.parsetree.symbol.*;
import cn.alumik.parsetree.util.Config;

import java.util.*;

public class Parser {

    private final Grammar mGrammar;

    public Parser(Config config) throws AnalysisException {
        mGrammar = new Grammar(config);
        mGrammar.initParseTable();
    }

    public Grammar getGrammar() {
        return mGrammar;
    }

    public ParseTree parse(List<TerminalSymbol> terminalSymbols) throws AnalysisException, ParsingException {
        initTerminalSymbolList(terminalSymbols);
        final List<Symbol> symbols = new ArrayList<>(terminalSymbols);
        final ParseTree tree = new ParseTree();
        final Stack<Integer> stateStack = new Stack<>();
        final Stack<ParseTreeNode> nodeStack = new Stack<>();
        final ParseTable parseTable = mGrammar.getParseTable();
        final Map<Integer, Map<AbstractSymbol, Transition>> parseTableMap = parseTable.getTable();
        stateStack.push(0);
        int index = 0;
        final AbstractSymbol startSymbol = parseTableMap
                .get(parseTable.getAcceptState())
                .get(mGrammar.getSymbolPool().getTerminalSymbol(AbstractTerminalSymbol.END))
                .getReduceProduction()
                .from();
        while (index != symbols.size() - 1 || !symbols.get(index).getAbstractSymbol().equals(startSymbol)) {
            final int currentState = stateStack.peek();
            final Symbol currentSymbol = index < symbols.size() ? symbols.get(index)
                    : new TerminalSymbol(mGrammar.getSymbolPool().getTerminalSymbol(AbstractTerminalSymbol.END));
            final Transition transition = parseTableMap.get(currentState).get(currentSymbol.getAbstractSymbol());
            if (transition == null) {
                throw new ParsingException(
                        String.format(ParsingException.PARSING_ERROR, index + 1, currentSymbol), null);
            } else {
                switch (transition.getAction()) {
                    case Transition.SHIFT:
                        nodeStack.push(new ParseTreeNode(currentSymbol));
                    case Transition.GOTO:
                        stateStack.push(transition.getNextState());
                        index++;
                        break;
                    case Transition.REDUCE:
                        final Production production = transition.getReduceProduction();
                        final ParseTreeNode node = new ParseTreeNode(
                                new NonterminalSymbol((AbstractNonterminalSymbol) production.from()));
                        final Stack<ParseTreeNode> tmpStack = new Stack<>();
                        for (final AbstractSymbol ignored : production.to()) {
                            stateStack.pop();
                            tmpStack.push(nodeStack.pop());
                        }
                        for (final AbstractSymbol ignored : production.to()) {
                            node.addChildren(tmpStack.pop());
                        }
                        nodeStack.push(node);
                        index--;
                        symbols.set(index, node.getSymbol());
                        break;
                }
            }
        }
        if (nodeStack.size() != 1) {
            throw new ParsingException(String.format(ParsingException.PARSING_NOT_COMPLETE, nodeStack), null);
        } else {
            tree.setRoot(nodeStack.pop());
        }
        return tree;
    }

    private void initTerminalSymbolList(List<TerminalSymbol> terminalSymbols) throws AnalysisException {
        for (final TerminalSymbol terminalSymbol : terminalSymbols) {
            terminalSymbol.setAbstractSymbol(
                    mGrammar.getSymbolPool().getTerminalSymbol(terminalSymbol.getAbstractSymbol().getName()));
        }
    }
}
