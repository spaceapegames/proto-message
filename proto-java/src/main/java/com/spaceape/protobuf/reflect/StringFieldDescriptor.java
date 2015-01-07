package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;

import java.io.IOException;

public class StringFieldDescriptor extends FieldDescriptor{
    public StringFieldDescriptor(int fieldNumber, String fieldName, Class clazz){
        super(fieldNumber, fieldName, clazz, String.class);
    }
    public String readField(CodedInputStream input, CodeGenContext context) throws IOException {
        return input.readString();
    }
    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        output.writeString(fieldNumber, (String)object);
    }
}
