package com.spaceape.protobuf.patch;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DiffPair {
    public final String left;
    public final String right;
    public DiffPair(String left, String right){
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals (Object obj){
        if (obj == null) return false;
        if (!(obj instanceof DiffPair)) return false;
        DiffPair o = (DiffPair)obj;
        return left == o.left && right == o.right;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(left).append(right).hashCode();
    }
}
