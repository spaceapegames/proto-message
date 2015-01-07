using System;
using System.Reflection;
using System.Collections.Generic;
using Google.ProtocolBuffers;
using com.spaceape.protobuf;

namespace com.spaceape.protobuf.reflect
{
  public class ListFieldDescriptor<T> : FieldDescriptor
  {
    private FieldDescriptor repeatingDescriptor;

    public ListFieldDescriptor(int fieldNumber, String fieldName, Type clazz, FieldDescriptor repeatingDescriptor) : base(fieldNumber, fieldName, clazz)
    {
      this.repeatingDescriptor = repeatingDescriptor;
    }

    override public Object readField(CodedInputStream input, CodeGenContext context)
    {
      int size = input.ReadInt32();
      List<T> newlist = new List<T>(size);
      for (int i = 0; i < size; i++)
      {
        newlist.Add((T)repeatingDescriptor.readField(input, context));
      }
      return newlist;
    }

    override public void writeField(CodedOutputStream output, CodeGenContext context, Object obj)
    {
      List<T> list = (List<T>)obj;
      output.WriteInt32(fieldNumber, list.Count);
      for (int i = 0; i < list.Count; i++)
      {
        repeatingDescriptor.writeField(output, context, list[i]);
      }
    }
  }
}

