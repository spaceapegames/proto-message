package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;

import java.io.IOException;

public class DoubleFieldDescriptor extends FieldDescriptor{
    public DoubleFieldDescriptor(int fieldNumber, String fieldName, Class clazz){
        super(fieldNumber, fieldName, clazz, double.class);
    }
    public Double readField(CodedInputStream input, CodeGenContext context) throws IOException {
        return input.readDouble();
    }
    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        output.writeDouble(fieldNumber, (double)object);
    }
}
