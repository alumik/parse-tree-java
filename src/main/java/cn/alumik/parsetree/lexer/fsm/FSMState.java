package cn.alumik.parsetree.lexer.fsm;

import java.util.*;

import cn.alumik.parsetree.lexer.Lexer;

public class FSMState {

    private int mId;

    private final Lexer mLexer;

    private final Map<Character, Set<FSMState>> mTransitions = new HashMap<>();

    private boolean mFinal;

    private final List<String> mAcceptingRules = new ArrayList<>();

    public FSMState(Lexer lexer) {
        mLexer = lexer;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Map<Character, Set<FSMState>> getTransitions() {
        return mTransitions;
    }

    public boolean isFinal() {
        return mFinal;
    }

    public void setFinal(boolean isFinal) {
        mFinal = isFinal;
    }

    public List<String> getAcceptingRules() {
        return mAcceptingRules;
    }

    public FSMState getNextStateOn(char by) {
        return getTransitions().get(by).iterator().next();
    }

    public void addTransition(char c, FSMState nextState) {
        if (!mTransitions.containsKey(c)) {
            mTransitions.put(c, new HashSet<>());
        }
        mTransitions.get(c).add(nextState);
    }

    public void addAcceptingRule(String acceptingRule) {
        mAcceptingRules.add(acceptingRule);
    }

    public void addAcceptingRules(List<String> acceptingRules) {
        mAcceptingRules.addAll(acceptingRules);
    }

    public void sortAcceptingRules() {
        final List<String> acceptingRuleList = new ArrayList<>(mLexer.getAcceptingRules().keySet());
        mAcceptingRules.sort(Comparator.comparingInt(acceptingRuleList::indexOf));
    }

    public void dfs(Set<FSMState> states) {
        states.add(this);
        for (final char c : mTransitions.keySet()) {
            for (final FSMState nextState : mTransitions.get(c)) {
                if (!states.contains(nextState)) {
                    states.add(nextState);
                    nextState.dfs(states);
                }
            }
        }
    }
}
