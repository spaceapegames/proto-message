package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class FieldDescriptor {
    protected int fieldNumber;
    protected String fieldName;
    protected Class clazz;
    protected Field field;
    protected Class fieldClass;

    public FieldDescriptor(int fieldNumber, String fieldName, Class clazz, Class fieldClass){
        this.fieldNumber = fieldNumber;
        this.fieldName = fieldName;
        this.clazz = clazz;
        this.fieldClass = fieldClass;
        try{
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Field getField(){
        return field;
    }
    public String getFieldName() {
        return fieldName;
    }
    public Class getFieldClass() {
        return fieldClass;
    }

    public void readAndSet(CodedInputStream input, CodeGenContext context, Object entity) throws IOException{
        try {
            field.set(entity, readField(input, context));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public abstract Object readField(CodedInputStream input, CodeGenContext context) throws IOException;
    public abstract void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException;
}
