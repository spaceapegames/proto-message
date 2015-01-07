package com.spaceape.protobuf.reflect.patch;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;
import com.spaceape.protobuf.CodeGenContext;
import com.spaceape.protobuf.GeneratedMessage;
import com.spaceape.protobuf.patch.DeltaType;
import com.spaceape.protobuf.reflect.FieldDescriptor;
import com.spaceape.protobuf.reflect.ListFieldDescriptor;
import com.spaceape.protobuf.reflect.MessageEnumFieldDescriptor;
import com.spaceape.protobuf.reflect.MessageFieldDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PatchViewer {
    public static <T extends GeneratedMessage> String view(byte [] data, Class<T> clazz) throws IOException{
        T message;
        try {
            message = clazz.newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        view(output, CodedInputStream.newInstance(data), new CodeGenContext(), message, 0);
        output.flush();
        return new String(output.toByteArray());
    }

    public static void view(OutputStream output, CodedInputStream input, CodeGenContext context, GeneratedMessage message, int depth) throws IOException{
        Map<Integer, FieldDescriptor> fields = message.getFieldDescriptors();
        int tag = input.readTag();
        while (tag != 0 && WireFormat.getTagWireType(tag) != WireFormat.WIRETYPE_END_GROUP) {
            int deltaType = input.readInt32();
            tag = input.readTag();
            int fieldNumber = WireFormat.getTagFieldNumber(tag);

            if (fields.containsKey(fieldNumber)) {
                FieldDescriptor descriptor = fields.get(fieldNumber);
                if (deltaType == DeltaType.Value.id) {
                    Object fieldValue = descriptor.readField(input, context);
                    viewObject(output, fieldValue, descriptor, new HashSet<GeneratedMessage>(), depth + 1, true);
                } else if (deltaType == DeltaType.Message.id) {
                    String className = input.readString();
                    GeneratedMessage fieldMsg;
                    try{
                        fieldMsg = (GeneratedMessage)Class.forName(className).newInstance();
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                    write(output, descriptor.getFieldName() + ": {", depth + 1);
                    view(output, input, context, fieldMsg, depth + 1);
                    write(output, "}", depth + 1);
                } else if (deltaType == DeltaType.FullValueList.id) {
                    Object listField = descriptor.readField(input, context);
                    viewList(output, (List)listField, (ListFieldDescriptor)descriptor, new HashSet<GeneratedMessage>(), depth + 1);
                } else if (deltaType == DeltaType.OptimizedList.id) {
                    write(output, descriptor.getFieldName() + ": [", depth + 1);
                    int size = input.readInt32();
                    for (int i=0;i<size;i++) {
                        boolean newOrder = input.readBool();
                        if (newOrder) {
                            int newPos = input.readInt32();
                            int oldPos = input.readInt32();
                            write(output, String.format("From index %s to %s: ", oldPos, newPos), depth);
                            GeneratedMessage fieldMsg;
                            try {
                                fieldMsg = (GeneratedMessage) ((ListFieldDescriptor) descriptor).repeatingDescriptor.getField().getType().newInstance();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            view(output, input, context, fieldMsg, depth + 1);
                        } else {
                            int fieldTag = input.readTag();
                            int fieldDeltaType = input.readInt32();
                            if (WireFormat.getTagWireType(input.readTag()) != WireFormat.WIRETYPE_START_GROUP) {
                                throw new IllegalArgumentException("can't read message without start tag");
                            }
                            if (fieldDeltaType == DeltaType.Value.id) {
                                Object fieldValue = ((ListFieldDescriptor) descriptor).repeatingDescriptor.readField(input, context);
                                viewObject(output, fieldValue, ((ListFieldDescriptor) descriptor).repeatingDescriptor, new HashSet<GeneratedMessage>(), depth + 1, false);
                            } else if (fieldDeltaType == DeltaType.Message.id) {
                                write(output, String.format("At index %s: ", i), depth);
                                String className = input.readString();
                                GeneratedMessage fieldMsg;
                                try{
                                    fieldMsg = (GeneratedMessage)Class.forName(className).newInstance();
                                }catch (Exception e){
                                    throw new RuntimeException(e);
                                }
                                write(output, "{", depth + 1);
                                view(output, input, context, fieldMsg, depth + 1);
                                write(output, "}", depth + 1);
                            }
                        }
                    }
                    write(output,"]", depth + 1);
                }
            } else {
                input.skipField(tag);
                write(output, "Unknown field: " + fieldNumber, depth);
            }

            tag = input.readTag();
        }
    }

    private static void viewObject(OutputStream output, Object fieldValue, FieldDescriptor descriptor, Set<GeneratedMessage> touchedMessages, int depth, boolean withFieldName) throws IOException{
        if (descriptor instanceof ListFieldDescriptor){
            viewList(output, (List)fieldValue, (ListFieldDescriptor)descriptor, touchedMessages, depth);
        } else if (descriptor instanceof MessageFieldDescriptor){
            if (withFieldName)
                write(output, descriptor.getFieldName() + ": {", depth);
            else
                write(output, "{", depth);
            viewMessage(output, (GeneratedMessage)fieldValue, touchedMessages, depth + 1);
            write(output, "}", depth);
        } else {
            write(output, descriptor.getFieldName() + ": "+fieldValue.toString(), depth);
        }
    }

    private static void viewList(OutputStream output, List list, ListFieldDescriptor listDescriptor, Set<GeneratedMessage> touchedMessages, int depth) throws IOException{
        write(output, listDescriptor.getFieldName() + ": [", depth);
        if (listDescriptor.repeatingDescriptor instanceof MessageFieldDescriptor){
            for (int i=0;i<list.size();i++){
                GeneratedMessage elem = (GeneratedMessage)list.get(i);
                write(output, "{", depth + 1);
                viewMessage(output, elem, touchedMessages, depth + 1);
                write(output, "}", depth + 1);
            }
        } else {
            for (int i=0;i<list.size();i++){
                write(output,list.get(i).toString(), depth + 1);
            }
        }
        write(output,"]", depth);
    }
    private static void viewMessage(OutputStream output, GeneratedMessage message, Set<GeneratedMessage> touchedMessages, int depth) throws IOException{
        if (touchedMessages.contains(message)) return;
        touchedMessages.add(message);

        Map<Integer, FieldDescriptor> fields = message.getFieldDescriptors();
        for (Integer fieldNumber: fields.keySet()){
            FieldDescriptor descriptor = fields.get(fieldNumber);
            Object fieldValue;
            try {
                fieldValue = descriptor.getField().get(message);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
            if (fieldValue == null){
                write(output, descriptor.getFieldName() + ": null", depth);
            }
            else if (descriptor instanceof ListFieldDescriptor){
                viewList(output, (List)fieldValue, (ListFieldDescriptor)descriptor, touchedMessages, depth);
            } else if (descriptor instanceof MessageFieldDescriptor){
                write(output, descriptor.getFieldName() + ": {", depth);
                viewMessage(output, (GeneratedMessage)fieldValue, touchedMessages, depth + 1);
                write(output, "}", depth);
            } else {
                write(output, descriptor.getFieldName() + ": "+fieldValue.toString(), depth);
            }
        }
    }

    private static void write(OutputStream output, String msg, int depth)  throws IOException{
        output.write((padding(depth) + msg + "\n").getBytes());
    }

    private static String padding(int depth){
        StringBuilder str = new StringBuilder();
        for (int i=0;i<depth;i++){
            str.append("\t");
        }
        return str.toString();
    }
}
