package com.spaceape.protobuf.patch;

public enum DeltaType {
    OptimizedList(1), FullValueList(2), Message(5), Value(6);

    public static DeltaType valueOf(int id){
        for (DeltaType v: DeltaType.values()){
            if (v.id == id) return v;
        }
        throw new IllegalArgumentException(id + " is not found");
    }
    public final int id;
    private DeltaType(int id){
        this.id = id;
    }
}