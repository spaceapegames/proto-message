using System;
using System.IO;
using NUnit.Framework;
using com.spaceape.protobuf;
using com.spaceape.common;
using com.spaceape.model2;
using com.spaceape.model;
using Google.ProtocolBuffers;

namespace com.spaceape.protobuf
{
  public class MessageTestUtil
  {
    public static ProfileTO createProfile(String id, int counter, long time)
    {
      ProfileTO profile = new ProfileTO();
      profile.setId(id);
      profile.setCounter(10);
      Timestamp timestamp = new Timestamp();
      timestamp.setTime(1000000);
      profile.setCreatedOn(timestamp);

      return profile;
    }

    public static ResourceTO createResourceTO(ResourceTypeTO resourceType, int amount)
    {
      ResourceTO to = new ResourceTO();
      to.setAmount(amount);
      to.setResourceType(resourceType);
      return to;
    }
  }
}

