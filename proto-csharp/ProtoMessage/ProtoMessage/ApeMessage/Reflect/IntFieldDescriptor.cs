using System;
using System.Reflection;
using System.Collections.Generic;
using Google.ProtocolBuffers;
using com.spaceape.protobuf;

namespace com.spaceape.protobuf.reflect
{
  public class IntFieldDescriptor: FieldDescriptor
  {
    public IntFieldDescriptor(int fieldNumber, String fieldName, Type clazz) : base(fieldNumber, fieldName, clazz)
    {
    }

    override public Object readField(CodedInputStream input, CodeGenContext context)
    {
      return input.ReadInt32();
    }

    override public void writeField(CodedOutputStream output, CodeGenContext context, Object obj)
    {
      output.WriteInt32(fieldNumber, (int)obj);
    }
  }
}

