using System;
using System.Collections.Generic;

namespace com.spaceape.protobuf
{
  public class CopyContext
  {
    private Dictionary<GeneratedMessage, GeneratedMessage> copiedMessages = new Dictionary<GeneratedMessage, GeneratedMessage>();

    public GeneratedMessage copyFrom(GeneratedMessage src, ConstructorFactory factory) {
        if (copiedMessages.ContainsKey(src)) return copiedMessages[src];
        GeneratedMessage copiedMsg = factory.newInstance(src.GetType().FullName);
        copiedMessages.Add(src, copiedMsg);
        copiedMsg.deepCopy(src, this);
        return copiedMsg;
    }
  }
}

