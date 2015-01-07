package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import com.spaceape.protobuf.CodeGenContext;
import com.spaceape.protobuf.ConstructorFactory;
import com.spaceape.protobuf.GeneratedMessage;

import java.io.IOException;

public class MessageFieldDescriptor extends FieldDescriptor {
    private ConstructorFactory constructorFactory;

    public MessageFieldDescriptor(int fieldNumber, String fieldName, Class clazz, ConstructorFactory constructorFactory, Class fieldClass){
        super(fieldNumber, fieldName, clazz, fieldClass);
        this.constructorFactory = constructorFactory;
    }

    public GeneratedMessage readField(CodedInputStream input, CodeGenContext context) throws IOException {
        return readMessage(input, context, constructorFactory);
    }

    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        GeneratedMessage msg = (GeneratedMessage)object;
        writeMessage(output, context, fieldNumber, msg);
    }

    private GeneratedMessage readMessage(CodedInputStream input, CodeGenContext context, ConstructorFactory constructorFactory) throws IOException{
        final boolean hasValue = input.readBool();
        if (!hasValue){
            int tag = input.readTag();
            if (tag != 0 && WireFormat.getTagWireType(tag) != WireFormat.WIRETYPE_END_GROUP){
                throw new IllegalArgumentException("invalid null message end with tag "+tag);
            }
            return null;
        }
        String id = input.readStringRequireUtf8();

        if (context.contains(id)) return context.getMessage(id);
        else {
            String className = input.readStringRequireUtf8();

            GeneratedMessage message = constructorFactory.newInstance(className);
            context.touch(id, message);
            return GenerateMessageStream.read(input, context, message);
        }
    }

    public void writeMessage (CodedOutputStream output, CodeGenContext context, int fieldNumber, GeneratedMessage message) throws IOException{
        output.writeTag(fieldNumber, WireFormat.WIRETYPE_START_GROUP);
        writeMessageNoTag(output, context, message);
        output.writeTag(fieldNumber, WireFormat.WIRETYPE_END_GROUP);
    }

    public static void writeMessageNoTag (CodedOutputStream output, CodeGenContext context, GeneratedMessage message) throws IOException{
        if (message == null) {
            output.writeBoolNoTag(false);
        } else {
            output.writeBoolNoTag(true);
            String id = context.generateMessageId(message);
            output.writeStringNoTag(id);

            if (!context.contains(id)) {
                context.touch(id, message);
                output.writeStringNoTag(message.getClass().getName());

                try {
                    for (FieldDescriptor descriptor : message.getFieldDescriptors().values()) {
                        descriptor.writeField(output, context, descriptor.field.get(message));
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
