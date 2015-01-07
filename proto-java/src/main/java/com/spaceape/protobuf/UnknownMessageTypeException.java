package com.spaceape.protobuf;

public class UnknownMessageTypeException extends RuntimeException{
    public UnknownMessageTypeException(int type) {
        super("Unknown message type "+type);
    }
}
