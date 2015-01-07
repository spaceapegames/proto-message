package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import com.spaceape.protobuf.CodeGenContext;
import com.spaceape.protobuf.GeneratedMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class GenerateMessageStream {
    public static GeneratedMessage read(byte [] data) throws IOException{
        CodeGenContext context = new CodeGenContext();
        CodedInputStream input = CodedInputStream.newInstance(data);
        final boolean hasValue = input.readBool();
        if (!hasValue){
            return null;
        }
        String id = input.readStringRequireUtf8(); //ignore id
        String className = input.readStringRequireUtf8();
        GeneratedMessage message;
        try {
            message = (GeneratedMessage)Class.forName(className).newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        context.touch(id, message);
        return read(input, context, message);
    }
    public static <T extends GeneratedMessage> T merge(byte [] data, T message) throws IOException{
        CodeGenContext context = new CodeGenContext();
        CodedInputStream input = CodedInputStream.newInstance(data);
        final boolean hasValue = input.readBool();
        if (!hasValue){
            return null;
        }
        String id = input.readStringRequireUtf8(); //ignore id
        String className = input.readStringRequireUtf8();
        context.touch(id, message);
        return read(input, context, message);
    }
    public static <T extends GeneratedMessage> T read(CodedInputStream input, CodeGenContext context, T message) throws IOException{
        Map<Integer, FieldDescriptor> descriptors = message.getFieldDescriptors();
        int tag = input.readTag();
        while (tag != 0 && WireFormat.getTagWireType(tag) != WireFormat.WIRETYPE_END_GROUP) {
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            if (descriptors.containsKey(fieldNumber)){
                FieldDescriptor descriptor = descriptors.get(fieldNumber);
                descriptor.readAndSet(input, context, message);
            } else {
                input.skipField(tag);
            }
            tag = input.readTag();
        }
        return message;
    }

    public static <T extends GeneratedMessage> void write(CodedOutputStream output, T message) throws IOException {
        CodeGenContext context = new CodeGenContext();

        MessageFieldDescriptor.writeMessageNoTag(output, context, message);
        output.flush();
    }

    public static <T extends GeneratedMessage> byte [] toByteArray (T message) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(CodedOutputStream.newInstance(out), message);
        out.flush();
        return out.toByteArray();
    }
}
