using System;
using System.Reflection;
using System.Collections.Generic;
using Google.ProtocolBuffers;
using com.spaceape.protobuf;

namespace com.spaceape.protobuf.reflect
{
  public class MessageEnumFieldDescriptor : FieldDescriptor
  {
    private Type enumType;
    private MethodInfo constructMethod;
    private MethodInfo valueGetter;

    public MessageEnumFieldDescriptor(int fieldNumber, String fieldName, Type clazz, ConstructorFactory constructorFactory, Type fieldClass) : base(fieldNumber, fieldName, clazz)
    {
      this.enumType = fieldClass;
      this.constructMethod = enumType.GetMethod("valueOf", new Type[]{ typeof(int) });
      this.valueGetter = enumType.GetMethod("getNumber");
    }

    override public Object readField(CodedInputStream input, CodeGenContext context)
    {
      int value = input.ReadInt32();
      return constructMethod.Invoke(null, new object[]{ value });
    }

    override public void writeField(CodedOutputStream output, CodeGenContext context, Object obj)
    {
      output.WriteInt32(fieldNumber, (int)valueGetter.Invoke(obj, new object[0]));
    }
  }
}
