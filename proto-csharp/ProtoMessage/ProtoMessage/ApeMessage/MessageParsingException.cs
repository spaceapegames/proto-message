using System;

namespace com.spaceape.protobuf
{
  public class MessageParsingException : Exception
  {
    public MessageParsingException(String msg) : base(msg)
    {
    }

    public MessageParsingException(String msg, Exception e) : base(msg, e)
    {
    }
  }

  public class MessageSerializingException : Exception
  {
    public MessageSerializingException(String msg) : base(msg)
    {
    }

    public MessageSerializingException(String msg, Exception e) : base(msg, e)
    {
    }
  }
}

