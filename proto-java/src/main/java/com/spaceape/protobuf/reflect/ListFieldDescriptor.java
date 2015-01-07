package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.spaceape.protobuf.CodeGenContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListFieldDescriptor<T> extends FieldDescriptor{
    public final FieldDescriptor repeatingDescriptor;

    public ListFieldDescriptor(int fieldNumber, String fieldName, Class clazz, FieldDescriptor repeatingDescriptor){
        super(fieldNumber, fieldName, clazz, List.class);
        this.repeatingDescriptor = repeatingDescriptor;
    }
    public List<T> readField(CodedInputStream input, CodeGenContext context) throws IOException{
        int size = input.readInt32();
        List<T> newlist = new ArrayList<T>(size);
        for (int i=0;i<size;i++){
            int tag = input.readTag();
            newlist.add((T)repeatingDescriptor.readField(input, context));
        }
        return newlist;
    }

    public void writeField(CodedOutputStream output, CodeGenContext context, Object object) throws IOException{
        List<T> list = (List<T>)object;
        output.writeInt32(fieldNumber, list.size());
        for (int i=0;i<list.size();i++){
            repeatingDescriptor.writeField(output, context, list.get(i));
        }
    }
}
