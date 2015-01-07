package com.spaceape.protobuf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeGenContext {
    private int idSequencer = 0;
    private Map<String, GeneratedMessage> touchedMessages = new HashMap<String, GeneratedMessage>();
    private Map<GeneratedMessage, String> messageIds = new HashMap<GeneratedMessage, String>();

    public void touch(String id, GeneratedMessage message) {
        touchedMessages.put(id, message);
    }

    public String generateMessageId(GeneratedMessage message) {
        String id = message.getMessageId();
        if (id != null){
            return id;
        }
        id = messageIds.get(message);
        if (id == null) { // not generate before
            id = String.valueOf(++idSequencer);
            messageIds.put(message, id);
        }
        return id;
    }

    public GeneratedMessage getMessage(String id) {
        return touchedMessages.get(id);
    }

    public boolean contains(String id) {
        return touchedMessages.containsKey(id);
    }
}
