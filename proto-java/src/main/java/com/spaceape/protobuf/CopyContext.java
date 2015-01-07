package com.spaceape.protobuf;

import java.util.HashMap;
import java.util.Map;

public class CopyContext {
    private Map<GeneratedMessage, GeneratedMessage> copiedMessages = new HashMap<>();

    public GeneratedMessage copyFrom(GeneratedMessage src, ConstructorFactory factory) {
        if (copiedMessages.containsKey(src)) return copiedMessages.get(src);
        GeneratedMessage copiedMsg = factory.newInstance(src.getClass().getName());
        copiedMessages.put(src, copiedMsg);
        copiedMsg.deepCopy(src, this);
        return copiedMsg;
    }
}
