package cn.alumik.parsetree.lexer.fsm;

import cn.alumik.parsetree.lexer.Lexer;
import cn.alumik.parsetree.util.Escaper;
import cn.alumik.parsetree.util.IDGen;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public class NFA {

    private Lexer mLexer;

    private FSMState mStartState;

    private Set<FSMState> mFinalStates = new HashSet<>();

    public NFA() {
    }

    public NFA(Lexer lexer) {
        mLexer = lexer;
        mStartState = new FSMState(mLexer);
        mStartState.setFinal(true);
        mFinalStates.add(mStartState);
    }

    public NFA(FSMState startState, Lexer lexer) {
        mLexer = lexer;
        mStartState = startState;
        if (startState.isFinal()) {
            mFinalStates.add(startState);
        }
    }

    public FSMState getStartState() {
        return mStartState;
    }

    public void setStartState(FSMState startState) {
        mStartState = startState;
    }

    public Set<FSMState> getFinalStates() {
        return mFinalStates;
    }

    public Lexer getLexer() {
        return mLexer;
    }

    public void setFinalStates(Set<FSMState> finalStates) {
        mFinalStates = finalStates;
    }

    public void addFinalState(FSMState state) {
        mFinalStates.add(state);
    }

    public void draw(String path) throws IOException {
        Graphviz.fromGraph(getGraph()).render(Format.SVG).toFile(new File(path));
    }

    private MutableGraph getGraph() {
        Graphviz.useEngine(new GraphvizV8Engine());
        final MutableGraph graph = mutGraph(IDGen.next())
                .setDirected(true)
                .graphAttrs()
                .add(Rank.dir(LEFT_TO_RIGHT));
        final Set<FSMState> states = new LinkedHashSet<>();
        mStartState.dfs(states);
        int id = 1;
        for (final FSMState state : states) {
            state.setId(id++);
        }
        for (final FSMState state : states) {
            final MutableNode node = mutNode(String.valueOf(state.getId()));
            if (state.isFinal()) {
                node.add(Shape.DOUBLE_CIRCLE);
                final MutableNode acceptingRuleNode = mutNode(state.getId() + " accepting rules")
                        .add(Label.lines(String.join("\n", state.getAcceptingRules())))
                        .add(Shape.RECTANGLE)
                        .add(Color.BLUE);
                node.addLink(
                        to(acceptingRuleNode)
                                .with(Style.DASHED)
                                .with(Arrow.NONE)
                                .with(Color.BLUE));
            } else {
                node.add(Shape.CIRCLE);
            }
            if (state == mStartState) {
                final MutableNode entryNode = mutNode("0")
                        .add(Shape.POINT)
                        .add(Label.of(""))
                        .addLink(to(node).with(Label.of("start")));
                graph.add(entryNode);
            }
            for (final char c : state.getTransitions().keySet()) {
                final Set<FSMState> nextStates = state.getTransitions().get(c);
                for (final FSMState nextState : nextStates) {
                    node.addLink(
                            to(mutNode(String.valueOf(nextState.getId())))
                                    .with(Label.of(" " + Escaper.unescape(c))));
                }
            }
            graph.add(node);
        }
        return graph;
    }
}
