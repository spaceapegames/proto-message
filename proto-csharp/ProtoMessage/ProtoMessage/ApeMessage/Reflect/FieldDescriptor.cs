using System;
using System.Reflection;
using Google.ProtocolBuffers;
using com.spaceape.protobuf;

namespace com.spaceape.protobuf.reflect
{
  public abstract class FieldDescriptor
  {
    protected int fieldNumber;
    protected String fieldName;
    protected Type clazz;
    protected FieldInfo field;

    public FieldDescriptor(int fieldNumber, String fieldName, Type clazz){
        this.fieldNumber = fieldNumber;
        this.fieldName = fieldName;
        this.clazz = clazz;
        field = clazz.GetField(fieldName);
    }

    public FieldInfo getField(){
        return field;
    }

    public void readAndSet(CodedInputStream input, CodeGenContext context, Object entity) {
        field.SetValue(entity, readField(input, context));
    }

    public abstract Object readField(CodedInputStream input, CodeGenContext context) ;
    public abstract void writeField(CodedOutputStream output, CodeGenContext context, Object obji) ;
  }
}

