package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;

import java.io.IOException;

public class IntFieldDescriptor extends FieldDescriptor{
    public IntFieldDescriptor(int fieldNumber, String fieldName, Class clazz){
        super(fieldNumber, fieldName, clazz, int.class);
    }
    public Integer readField(CodedInputStream input, CodeGenContext context) throws IOException {
        return input.readInt32();
    }
    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        output.writeInt32(fieldNumber, (int)object);
    }
}
