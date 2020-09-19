package cn.alumik.parsetree.lexer;

import cn.alumik.parsetree.lexer.fsm.NFA;
import cn.alumik.parsetree.parser.Production;
import cn.alumik.parsetree.symbol.Symbol;

import java.util.List;

abstract class RegexProduction extends Production {

    public RegexProduction(Production production) {
        super(production);
    }

    public abstract NFA getNFA(List<NFA> nodes, List<Symbol> children);
}
