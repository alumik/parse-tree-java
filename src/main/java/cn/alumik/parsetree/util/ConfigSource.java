package cn.alumik.parsetree.util;

import java.util.*;

public class ConfigSource {

    private Set<String> nonterminalSymbols;

    private Map<String, String> terminalSymbols;

    private Set<String> ignoredSymbols;

    private String startSymbol;

    private List<String> productions;

    public Set<String> getNonterminalSymbols() {
        if (nonterminalSymbols != null) {
            return nonterminalSymbols;
        }
        return new LinkedHashSet<>();
    }

    public void setNonterminalSymbols(Set<String> nonterminalSymbols) {
        this.nonterminalSymbols = nonterminalSymbols;
    }

    public Map<String, String> getTerminalSymbols() {
        if (terminalSymbols != null) {
            return terminalSymbols;
        }
        return new LinkedHashMap<>();
    }

    public void setTerminalSymbols(Map<String, String> terminalSymbols) {
        this.terminalSymbols = terminalSymbols;
    }

    public Set<String> getIgnoredSymbols() {
        if (ignoredSymbols != null) {
            return ignoredSymbols;
        }
        return new LinkedHashSet<>();
    }

    public void setIgnoredSymbols(Set<String> ignoredSymbols) {
        this.ignoredSymbols = ignoredSymbols;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(String startSymbol) {
        this.startSymbol = startSymbol;
    }

    public List<String> getProductions() {
        if (productions != null) {
            return productions;
        }
        return new ArrayList<>();
    }

    public void setProductions(List<String> productions) {
        this.productions = productions;
    }
}
