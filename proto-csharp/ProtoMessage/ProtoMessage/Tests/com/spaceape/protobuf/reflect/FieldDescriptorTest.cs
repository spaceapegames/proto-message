using System;
using System.IO;
using System.Collections.Generic;
using NUnit.Framework;
using com.spaceape.protobuf;
using com.spaceape.common;
using com.spaceape.model2;
using com.spaceape.model;
using Google.ProtocolBuffers;

namespace com.spaceape.protobuf.reflect
{
  [TestFixture()]
  public class FieldDescriptorTest
  {
    [Test()]
    public void testAllFieldsInInheritance()
    {
      ProfileTO profile = new ProfileTO();
      Dictionary<int, FieldDescriptor> fields = profile.getFieldDescriptors();
      Assert.IsTrue(fields.ContainsKey(1));
    }
  }
}

