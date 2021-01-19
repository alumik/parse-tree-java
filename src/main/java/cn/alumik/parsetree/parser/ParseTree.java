package cn.alumik.parsetree.parser;

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

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class ParseTree {

    private ParseTreeNode mRoot;

    public void setRoot(ParseTreeNode root) {
        this.mRoot = root;
    }

    public void draw(String path) throws IOException {
        Graphviz.fromGraph(getGraph()).render(Format.SVG).toFile(new File(path));
    }

    private MutableGraph getGraph() {
        Graphviz.useEngine(new GraphvizV8Engine());
        final MutableGraph graph = mutGraph(IDGen.next()).setDirected(true);

        final List<MutableNode> graphNodes = new ArrayList<>();
        final Queue<ParseTreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(mRoot);

        while (!nodeQueue.isEmpty()) {
            final ParseTreeNode currentNode = nodeQueue.poll();
            final MutableNode currentGraphNode = mutNode(currentNode.getId())
                    .add(Label.of(currentNode.getSymbol().getAbstractSymbol().getName()));
            graphNodes.add(currentGraphNode);
            if (currentNode.getChildren().isEmpty()) {
                final MutableNode valueNode = mutNode(currentNode.getId() + currentNode.getSymbol().getValue())
                        .add(Label.of(currentNode.getSymbol().getValue())).add(Shape.BOX).add(Color.BLUE);
                graphNodes.add(valueNode);
                currentGraphNode.addLink(currentGraphNode.linkTo(valueNode)
                        .add(Style.DASHED).add(Color.BLUE).add(Arrow.NONE));
            } else {
                for (final ParseTreeNode nextNode : currentNode.getChildren()) {
                    nodeQueue.add(nextNode);
                    final MutableNode nextGraphNode = mutNode(nextNode.getId());
                    currentGraphNode.addLink(nextGraphNode);
                }
            }
        }

        for (final MutableNode graphNode : graphNodes) {
            graph.add(graphNode);
        }
        return graph;
    }
}
