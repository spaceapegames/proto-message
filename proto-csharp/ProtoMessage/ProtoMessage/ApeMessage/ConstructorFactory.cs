using System;

namespace com.spaceape.protobuf
{
  public abstract class ConstructorFactory
  {
    public abstract GeneratedMessage newInstance(String fullName);
  }
}

