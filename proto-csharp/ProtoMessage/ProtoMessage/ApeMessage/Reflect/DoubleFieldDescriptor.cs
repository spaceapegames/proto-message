﻿using System;
using System.Reflection;
using System.Collections.Generic;
using Google.ProtocolBuffers;
using com.spaceape.protobuf;

namespace com.spaceape.protobuf.reflect
{
  public class DoubleFieldDescriptor: FieldDescriptor
  {
    public DoubleFieldDescriptor(int fieldNumber, String fieldName, Type clazz) : base(fieldNumber, fieldName, clazz)
    {
    }

    override public Object readField(CodedInputStream input, CodeGenContext context)
    {
      return input.ReadDouble();
    }

    override public void writeField(CodedOutputStream output, CodeGenContext context, Object obj)
    {
      output.WriteDouble(fieldNumber, (double)obj);
    }
  }
}

