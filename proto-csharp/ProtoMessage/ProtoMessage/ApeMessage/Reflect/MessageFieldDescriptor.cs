using System;
using System.Reflection;
using System.Collections.Generic;
using Google.ProtocolBuffers;
using com.spaceape.protobuf;

namespace com.spaceape.protobuf.reflect
{
  public class MessageFieldDescriptor : FieldDescriptor
  {
    private ConstructorFactory constructorFactory;

    public MessageFieldDescriptor(int fieldNumber, String fieldName, Type clazz, ConstructorFactory constructorFactory, Type fieldClass) : base(fieldNumber, fieldName, clazz)
    {
      this.constructorFactory = constructorFactory;
    }

    override public Object readField(CodedInputStream input, CodeGenContext context)
    {
      uint tag;
      input.ReadTag(out tag);
      return readMessage(input, context, constructorFactory);
    }

    override public void writeField(CodedOutputStream output, CodeGenContext context, Object obj)
    {
      GeneratedMessage msg = (GeneratedMessage)obj;
      writeMessage(output, context, fieldNumber, msg);
    }

    private GeneratedMessage readMessage(CodedInputStream input, CodeGenContext context, ConstructorFactory constructorFactory)
    {
      bool hasValue = input.ReadBool();
      if (!hasValue)
      {
        uint tag;
        input.ReadTag(out tag);
        if (tag != 0 && WireFormat.GetTagWireType(tag) != WireFormat.WireType.EndGroup)
        {
          throw new Exception("invalid null message end with tag " + tag);
        }
        return null;
      }
      String id = input.ReadString();

      if (context.contains(id))
        return context.getMessage(id);
      else
      {
        String className = input.ReadString();

        GeneratedMessage message = constructorFactory.newInstance(className);
        context.touch(id, message);
        foreach (KeyValuePair<int, FieldDescriptor> entry in message.getFieldDescriptors())
        {
          entry.Value.readAndSet(input, context, message);
        }
        return message;
      }
    }

    public void writeMessage(CodedOutputStream output, CodeGenContext context, int fieldNumber, GeneratedMessage message)
    {
      output.WriteTag(fieldNumber, WireFormat.WireType.StartGroup);
      writeMessageNoTag(output, context, message);
      output.WriteTag(fieldNumber, WireFormat.WireType.EndGroup);
    }

    private void writeMessageNoTag(CodedOutputStream output, CodeGenContext context, GeneratedMessage message)
    {
      if (message == null)
      {
        output.WriteBoolNoTag(false);
      }
      else
      {
        output.WriteBoolNoTag(true);
        String id = context.generateMessageId(message);
        output.WriteStringNoTag(id);

        if (!context.contains(id))
        {
          context.touch(id, message);
          output.WriteStringNoTag(message.GetType().FullName);

          foreach (KeyValuePair<int, FieldDescriptor> entry in message.getFieldDescriptors())
          {
            entry.Value.writeField(output, context, field.GetValue(message));
          }
        }
      }
    }
  }
}
