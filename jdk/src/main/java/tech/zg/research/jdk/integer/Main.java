package tech.zg.research.jdk.integer;

import java.io.PrintStream;

public class Main {
    /**
     * 2 的 31 次方
     */
    public static final int MAX_POW2 = 1 << 30;
    private static final PrintStream out = System.out;

    public static void main(String[] args) {

        for (int index = 1; index < 100; index++) {
            int i = Integer.numberOfLeadingZeros(index);
            int i1 = Integer.numberOfTrailingZeros(index);
            int i2 = roundToPowerOfTwo(index);
            out.println(i);
            out.println(i1);
            out.println(i2);
        }
    }

    /**
     * 大于传入值且最小的2的次方数据
     *
     * @param value
     * @return
     */
    public static int roundToPowerOfTwo(final int value) {
        if (value > MAX_POW2) {
            throw new IllegalArgumentException("There is no larger power of 2 int for value:" + value + " since it exceeds 2^31.");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Given value:" + value + ". Expecting value >= 0.");
        }
        final int nextPow2 = 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
        return nextPow2;
    }
}
