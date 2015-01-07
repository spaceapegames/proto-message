using System;
using System.Collections.Generic;

namespace com.spaceape.protobuf
{
  public class CodeGenContext
  {
    private int idSequencer = 0;
    private Dictionary<GeneratedMessage, String> messageIds = new Dictionary<GeneratedMessage, String>();
    private Dictionary<String, GeneratedMessage> touchedMessages = new Dictionary<String, GeneratedMessage>();

    public void touch(String id, GeneratedMessage message)
    {
      touchedMessages.Add(id, message);
    }

    public String generateMessageId(GeneratedMessage message)
    {
      String id = message.getMessageId();
      if (id != null)
      {
        return id;
      }
      
      if (!messageIds.ContainsKey(message))
      { // not generate before
        idSequencer += 1;
        id = idSequencer.ToString();
        messageIds.Add(message, id);
      } else {
        id = messageIds[message];
      }
      return id;
    }

    public GeneratedMessage getMessage(String id)
    {
      return touchedMessages[id];
    }

    public Boolean contains(String id)
    {
      return touchedMessages.ContainsKey(id);
    }
  }
}

