package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;

import java.io.IOException;

public class BoolFieldDescriptor extends FieldDescriptor{
    public BoolFieldDescriptor(int fieldNumber, String fieldName, Class clazz){
        super(fieldNumber, fieldName, clazz, boolean.class);
    }
    public Boolean readField(CodedInputStream input, CodeGenContext context) throws IOException {
        return input.readBool();
    }
    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        output.writeBool(fieldNumber, (boolean)object);
    }
}
