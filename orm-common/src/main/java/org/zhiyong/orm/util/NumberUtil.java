package org.zhiyong.orm.util;

import java.util.Random;

public final class NumberUtil {
    private final static Random random;

    static {
        random = new Random();
    }

    public static int generate(int max){
        return random.nextInt(max);
    }

}
