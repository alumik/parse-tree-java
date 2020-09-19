package cn.alumik.parsetree.parser;

import cn.alumik.parsetree.symbol.Symbol;
import cn.alumik.parsetree.util.IDGen;

import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {

    private final String mId = IDGen.next();

    private final Symbol mSymbol;

    private final List<ParseTreeNode> mChildren = new ArrayList<>();

    public ParseTreeNode(Symbol symbol) {
        mSymbol = symbol;
    }

    public String getId() {
        return mId;
    }

    public Symbol getSymbol() {
        return mSymbol;
    }

    public List<ParseTreeNode> getChildren() {
        return mChildren;
    }

    public void addChildren(ParseTreeNode node) {
        mChildren.add(node);
    }
}
