using System;
using System.IO;
using System.Collections;
using System.Collections.Generic;
using Google.ProtocolBuffers;
using com.spaceape.protobuf.patch;
using com.spaceape.protobuf.reflect;

namespace com.spaceape.protobuf
{
  public abstract class GeneratedMessage
  {
    public virtual void deepCopy(GeneratedMessage dst, CopyContext context)
    {
    }

    public virtual Boolean sameAs(GeneratedMessage dst)
    {
      if (dst == null)
        return false;
      return GetType().Equals(dst.GetType());
    }

    public byte [] toByteArray()
    {
      CodeGenContext context = new CodeGenContext();
      MemoryStream outstream = new MemoryStream();
      CodedOutputStream output = CodedOutputStream.CreateInstance(outstream);
      writeMessageNoTag(output, context, this);
      output.Flush();
      outstream.Flush();
      return outstream.ToArray();
    }

    public void mergeFrom(CodedInputStream input, CodeGenContext context)
    {
      mergeRead(input, context);
    }

    protected void read(CodedInputStream input, CodeGenContext context)
    {
      try
      {
        uint tag;
        input.ReadTag(out tag);
        while (tag != 0 && WireFormat.GetTagWireType(tag) != WireFormat.WireType.EndGroup)
        {
          int fieldNumber = WireFormat.GetTagFieldNumber(tag);
          if (!readField(fieldNumber, input, context))
          {
            input.SkipField(tag);
          }
          input.ReadTag(out tag);
        }
      }
      catch (Exception e)
      {
        throw e;
      }
    }

    protected static GeneratedMessage readMessage(CodedInputStream input, CodeGenContext context, ConstructorFactory constructorFactory)
    {
      Boolean hasValue = input.ReadBool();
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
        message.read(input, context);
        return message;
      }
    }

    private void mergeRead(CodedInputStream input, CodeGenContext context)
    {
      Boolean hasValue = input.ReadBool();
      if (!hasValue)
      {
        return;
      }
      String id = input.ReadString(); //ignore id
      String className = input.ReadString(); // ignore original class

      if (!context.contains(id))
      {
        context.touch(id, this);
      }
      this.read(input, context);
    }

    protected static void writeMessage(CodedOutputStream output, CodeGenContext context, int fieldNumber, GeneratedMessage message)
    {
      output.WriteTag(fieldNumber, WireFormat.WireType.StartGroup);
      writeMessageNoTag(output, context, message);
      output.WriteTag(fieldNumber, WireFormat.WireType.EndGroup);
    }

    private static void writeMessageNoTag(CodedOutputStream output, CodeGenContext context, GeneratedMessage message)
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

          message.writeField(output, context);
        }
      }
    }

    protected virtual Boolean readField(int fieldNumber, CodedInputStream input, CodeGenContext context)
    {
      return false;
    }

    protected virtual void writeField(CodedOutputStream output, CodeGenContext context)
    {
    }


    public String getMessageId()
    {
      return null;
    }

    /**===================== Patch ====================*/

    public byte [] patch(GeneratedMessage rightMessage)
    {
      PatchContext context = new PatchContext();
      MemoryStream stream = new MemoryStream();
      CodedOutputStream output = CodedOutputStream.CreateInstance(stream);
      if (!patchMessageNoTag(this, rightMessage, output, context))
      {
        return new byte[0];
      }
      output.Flush();
      stream.Flush();
      return stream.ToArray();
    }

    public virtual Boolean patch(GeneratedMessage obj, CodedOutputStream output, PatchContext context)
    {
      return false;
    }

    public static Boolean patchMessageList<T>(int fieldNumber, List<T> left, List<T> right, CodedOutputStream output, PatchContext context) where T: GeneratedMessage
    {
      Boolean hasPatch = false;
      //prepare id-pos map
      Dictionary<String, List<int>> rightIds = new Dictionary<String, List<int>>();
      for (int i = 0; i < right.Count; i++)
      {
        String rightId = context.generateMessageId(right[i]);
        List<int> positions;
        if (rightIds.ContainsKey(rightId))
        {
          positions = rightIds[rightId];
          positions.Add(i);
          rightIds[rightId] = positions;
        }
        else
        {
          positions = new List<int>();
          positions.Add(i);
          rightIds.Add(rightId, positions);
        }
      }

      //patch element with same id first
      HashSet<int> patchedPositions = new HashSet<int>();
      for (int i = 0; i < left.Count; i++)
      {
        String leftTmpId = context.generateMessageId(left[i]);
        if (rightIds.ContainsKey(leftTmpId))
        {
          List<int> availablePos = rightIds[leftTmpId];
          if (availablePos.Count > 0)
          {
            int pos = availablePos[0];

            output.WriteBoolNoTag(true); //not list order
            output.WriteInt32NoTag(i);
            output.WriteInt32NoTag(pos);

            if (patchMessage(fieldNumber, left[i], right[pos], output, context))
            {
              hasPatch = true;
            }
            patchedPositions.Add(i);

            availablePos.RemoveAt(0);
            if (availablePos.Count > 0)
              rightIds.Add(leftTmpId, availablePos);
            else
              rightIds.Remove(leftTmpId);
          }

        }
      }
      List<int> remainingPositions = new List<int>();
      foreach (KeyValuePair<String, List<int>> entry in rightIds)
      {
        remainingPositions.AddRange(entry.Value);
      }
      remainingPositions.Sort();

      //patch element without same id found
      for (int i = 0; i < left.Count; i++)
      {
        if (!patchedPositions.Contains(i))
        {
          output.WriteBoolNoTag(false); //same list order
          if (remainingPositions.Count > 0)
          {
            if (patchMessage(fieldNumber, left[i], right[remainingPositions[0]], output, context))
            {
              hasPatch = true;
            }
            remainingPositions.RemoveAt(0);
          }
          else
          {
            hasPatch = true;
            output.WriteInt32(fieldNumber, (int)DeltaType.Value);
            writeMessage(output, context, fieldNumber, left[i]);
          }
        }
      }
      return hasPatch;
    }

    protected static Boolean patchMessage(int fieldNumber, GeneratedMessage left, GeneratedMessage right, CodedOutputStream output, PatchContext context)
    {
      output.WriteInt32(fieldNumber, (int)DeltaType.Message);

      output.WriteTag(fieldNumber, WireFormat.WireType.StartGroup);
      output.WriteStringNoTag(left.GetType().FullName);
      Boolean hasPatch = patchMessageNoTag(left, right, output, context);
      output.WriteTag(fieldNumber, WireFormat.WireType.EndGroup);
      return hasPatch;
    }

    private static Boolean patchMessageNoTag(GeneratedMessage left, GeneratedMessage right, CodedOutputStream output, PatchContext context)
    {
      context.patch(left, right);
      return left.patch(right, output, context);
    }

    public virtual void applyPatch(byte[] data)
    {
      CodedInputStream input = CodedInputStream.CreateInstance(data);
      PatchContext context = new PatchContext();
      applyPatchToMessage(input, context);
    }

    public static GeneratedMessage applyPatch(GeneratedMessage right, CodedInputStream input, PatchContext context, ConstructorFactory constructorFactory)
    {
      String className = input.ReadString();
      GeneratedMessage message = constructorFactory.newInstance(className);
      message.deepCopy(right, context.getCopyContext());
      message.applyPatchToMessage(input, context);

      return message;
    }

    private void applyPatchToMessage(CodedInputStream input, PatchContext context)
    {
      uint tag;
      input.ReadTag(out tag);
      while (tag != 0 && WireFormat.GetTagWireType(tag) != WireFormat.WireType.EndGroup)
      {
        int deltaType = input.ReadInt32();
        input.ReadTag(out tag);
        int fieldNumber = WireFormat.GetTagFieldNumber(tag);

        if (deltaType == (int)DeltaType.Value)
        {
          if (!readField(fieldNumber, input, context))
          {
            input.SkipField(tag);
          }
        }
        else if (deltaType == (int)DeltaType.Message)
        {
          if (!applyPatchOnMessageField(fieldNumber, input, context))
          {
            input.SkipField(tag);
          }
        }
        else if (deltaType == (int)DeltaType.FullValueList)
        {
          if (!readField(fieldNumber, input, context))
          {
            input.SkipField(tag);
          }
        }
        else if (deltaType == (int)DeltaType.OptimizedList)
        {
          int size = input.ReadInt32();
          applyOptimizedListPatch(fieldNumber, size, input, context);
        }

        input.ReadTag(out tag);
      }
    }

    protected virtual Boolean applyPatchOnMessageField(int fieldNumber, CodedInputStream input, PatchContext context)
    {
      return false;
    }

    protected virtual Boolean applyOptimizedListPatch(int fieldNumber, int size, CodedInputStream input, PatchContext context)
    {
      return false;
    }

    /**===================== Reflect ====================*/
    public virtual Dictionary<int, FieldDescriptor> getFieldDescriptors(){
      return new Dictionary<int, FieldDescriptor>();
    }
  }
}

