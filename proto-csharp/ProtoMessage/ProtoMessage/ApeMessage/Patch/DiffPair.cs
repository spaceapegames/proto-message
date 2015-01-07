using System;
using com.spaceape.utils;

namespace com.spaceape.protobuf.patch
{
  public class DiffPair
  {
    public String left{ get; private set; }

    public String right{ get; private set; }

    public DiffPair(String left, String right)
    {
      this.left = left;
      this.right = right;
    }

    public override Boolean Equals (Object obj){
        if (obj == null) return false;
        if (!(obj is DiffPair)) return false;
        DiffPair o = (DiffPair)obj;
        return left == o.left && right == o.right;
    }

    public override int GetHashCode(){
        return new HashCodeBuilder().Add(left).Add(right).GetHashCode();
    }
  }
}

