package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;
import com.spaceape.protobuf.ConstructorFactory;

import java.io.IOException;
import java.lang.reflect.Method;

public class MessageEnumFieldDescriptor<T extends Enum> extends FieldDescriptor{
    private Class enumType;
    private Method constructMethod;
    private Method valueGetter;

    public MessageEnumFieldDescriptor(int fieldNumber, String fieldName, Class clazz, ConstructorFactory constructorFactory, Class fieldClass){
        super(fieldNumber, fieldName, clazz, fieldClass);
        this.enumType = fieldClass;
        try {
            this.constructMethod = enumType.getMethod("valueOf", int.class);
            this.valueGetter = enumType.getMethod("getNumber");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public T readField(CodedInputStream input, CodeGenContext context) throws IOException {
        int value = input.readInt32();
        try {
            return (T)constructMethod.invoke(null, value);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        try {
            output.writeInt32(fieldNumber, (int) valueGetter.invoke(object));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
