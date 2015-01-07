package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;

import java.io.IOException;

public class FloatFieldDescriptor extends FieldDescriptor {
    public FloatFieldDescriptor(int fieldNumber, String fieldName, Class clazz){
        super(fieldNumber, fieldName, clazz, float.class);
    }
    public Float readField(CodedInputStream input, CodeGenContext context) throws IOException {
        return input.readFloat();
    }
    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        output.writeFloat(fieldNumber, (float)object);
    }
}
