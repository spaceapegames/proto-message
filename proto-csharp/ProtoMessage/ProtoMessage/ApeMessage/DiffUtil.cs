using System;
using System.Collections.Generic;

namespace com.spaceape.protobuf
{
  public class DiffUtil
  {
    public static Boolean isDifferent(int left, int right){
        return left != right;
    }

    public static Boolean isDifferent(long left, long right){
        return left != right;
    }

    public static Boolean isDifferent(double left, double right){
        return left != right;
    }

    public static Boolean isDifferent(float left, float right){
        return left != right;
    }

    public static Boolean isDifferent(Object left, Object right){
        if (left == null && right == null) return false;
        if (left != null && right != null && left.Equals(right)) return false;
        return true;
    }

    public static Boolean isDifferentList<T>(List<T> left, List<T> right){
        if (left.Count != right.Count) return true;
        for (int i=0;i<left.Count;i++){
            if (isDifferent(left[i], right[i])) return true;
        }
        return false;
    }
  }
}

