package cn.alumik.parsetree.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.*;

public class Config {

    final private ConfigSource mConfigSource;

    public Config(String path) {
        final Yaml yaml = new Yaml(new Constructor(ConfigSource.class));
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
        mConfigSource = yaml.load(inputStream);
    }

    public Set<String> getNonterminalSymbols() {
        return mConfigSource.getNonterminalSymbols();
    }

    public Set<String> getTerminalSymbols() {
        final Set<String> terminalSymbols = new HashSet<>(mConfigSource.getTerminalSymbols().keySet());
        terminalSymbols.removeAll(mConfigSource.getIgnoredSymbols());
        return terminalSymbols;
    }

    public Map<String, String> getAcceptingRules() {
        return mConfigSource.getTerminalSymbols();
    }

    public Set<String> getIgnoredSymbols() {
        return mConfigSource.getIgnoredSymbols();
    }

    public String getStartSymbol() {
        return mConfigSource.getStartSymbol();
    }

    public List<String> getProductions() {
        return mConfigSource.getProductions();
    }
}
