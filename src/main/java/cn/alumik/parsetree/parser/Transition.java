package cn.alumik.parsetree.parser;

public class Transition {

    public static final int SHIFT = 0x01;

    public static final int GOTO = 0x02;

    public static final int REDUCE = 0x03;

    private final int mAction;

    private int mNextState;

    private Production mReduceProduction;

    private int mIndex;

    Transition(int action, int nextState) {
        mAction = action;
        mNextState = nextState;
    }

    Transition(Production reduceProduction, int index) {
        mAction = REDUCE;
        mReduceProduction = reduceProduction;
        mIndex = index;
    }

    public int getAction() {
        return mAction;
    }

    public Integer getNextState() {
        return mAction == SHIFT || mAction == GOTO ? mNextState : null;
    }

    public Production getReduceProduction() {
        return mReduceProduction;
    }

    @Override
    public String toString() {
        if (mAction == REDUCE) {
            return "r" + (mIndex + 1);
        } else {
            return "s" + mNextState;
        }
    }
}
