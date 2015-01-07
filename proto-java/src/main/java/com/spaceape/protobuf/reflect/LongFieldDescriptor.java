package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;

import java.io.IOException;

public class LongFieldDescriptor extends FieldDescriptor {
    public LongFieldDescriptor(int fieldNumber, String fieldName, Class clazz){
        super(fieldNumber, fieldName, clazz, long.class);
    }
    public Long readField(CodedInputStream input, CodeGenContext context) throws IOException {
        return input.readInt64();
    }
    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        output.writeInt64(fieldNumber, (long)object);
    }
}
