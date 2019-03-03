package com.company.gametools;

import java.util.Random;

public class MathTools {
    public static int randomSign() {
        Random random = new Random();
        int result = random.nextInt(2) - 1;

        return (result == 0) ? 1 : result;
    }

    public static int sqrVal(int value) {
        return value * value;
    }

    public static boolean withinRadius(Integer [] A, Integer [] B, int radius) {
        return sqrVal(radius) >= sqrVal(A[0] - B[0]) + sqrVal(A[1] - B[1]);
    }

    public static boolean in_range(int left, int val, int right, boolean strict) {
        if (strict) {
            return (left < val && val < right);
        }

        return (left <= val && val <= right);
    }
}
