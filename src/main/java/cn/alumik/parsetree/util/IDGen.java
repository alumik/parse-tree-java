package cn.alumik.parsetree.util;

public class IDGen {

    private static final char START_CODE = 'A';

    private static final char END_CODE = 'Z';

    private static final StringBuilder mId = new StringBuilder();

    static {
        mId.append(START_CODE);
    }

    public static String next() {
        final StringBuilder result = new StringBuilder(mId);
        boolean finish = false;
        for (int i = 0; i < mId.length(); i++) {
            if (mId.charAt(i) != END_CODE) {
                mId.setCharAt(i, (char) (mId.charAt(i) + 1));
                finish = true;
                break;
            } else {
                mId.setCharAt(i, START_CODE);
            }
        }
        if (!finish) {
            mId.append(START_CODE);
        }
        return result.reverse().toString();
    }
}
