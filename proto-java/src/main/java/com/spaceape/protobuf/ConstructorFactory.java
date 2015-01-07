package com.spaceape.protobuf;

public interface ConstructorFactory {
    public GeneratedMessage newInstance(String fullName);
}
