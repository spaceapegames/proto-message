package com.spaceape.protobuf;

import java.util.List;

public class DiffUtil {
    public static boolean isDifferent(int left, int right){
        return left != right;
    }

    public static boolean isDifferent(long left, long right){
        return left != right;
    }

    public static boolean isDifferent(double left, double right){
        return left != right;
    }

    public static boolean isDifferent(float left, float right){
        return left != right;
    }

    public static boolean isDifferent(Object left, Object right){
        if (left == null && right == null) return false;
        if (left != null && right != null && left.equals(right)) return false;
        return true;
    }

    public static <T> boolean isDifferentList(List<T> left, List<T> right){
        if (left.size() != right.size()) return true;
        for (int i=0;i<left.size();i++){
            if (isDifferent(left.get(i), right.get(i))) return true;
        }
        return false;
    }
}
