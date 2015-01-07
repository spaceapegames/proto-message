//*
using System;
using System.IO;
using com.spaceape.protobuf;
using com.spaceape.common;
using com.spaceape.model2;
using Google.ProtocolBuffers;
using NUnit.Framework;

namespace com.spaceape.model
{
  [TestFixture()]
  public class CrossLangTest
  {
    [Test()]
    public void writeModel()
    {
      ProfileTO tom = createProfile("tom", 10, 10000);
      tom.setPenalty(0.9f);
      tom.setWarBonus(0.5d);
      tom.addCollections(createResourceTO(ResourceTypeTO.Liquid, 22));

      ProfileTO andy = createProfile("andy", 11, 1418555521001L);
      ProfileTO lu = createProfile("lu", 12, 1418555521000L);
      tom.addFriends(andy);
      tom.addFriends(lu);

      byte[] data = tom.toByteArray();
      File.WriteAllBytes("model_csharp.data", data);
    }

    [Test()]
    public void readModel()
    {
      var data = ReadFile("model_java.data");
      ProfileTO profile = new ProfileTO();
      profile.mergeFrom(CodedInputStream.CreateInstance(data), new CodeGenContext());
      Assert.AreEqual(profile.getId(), "tom");
      Assert.AreEqual(profile.getCounter(), 10);
      Assert.AreEqual(profile.getCreatedOn().getTime(), 10000);
      Assert.AreEqual(profile.getPenalty(), 0.9f);
      Assert.AreEqual(profile.getWarBonus(), 0.5d);
      Assert.AreEqual(profile.getCollections().Count, 1);
      Assert.AreEqual(profile.getCollections()[0].getResourceType(), ResourceTypeTO.Liquid);
      Assert.AreEqual(profile.getCollections()[0].getAmount(), 22);

      Assert.AreEqual(profile.getFriends().Count, 2);
    }

    [Test()]
    public void applyPatch()
    {
      var data = ReadFile("model_java.data");
      ProfileTO tom = new ProfileTO();
      tom.mergeFrom(CodedInputStream.CreateInstance(data), new CodeGenContext());

      var diff = ReadFile("model_java_diff.data");
      tom.applyPatch(diff);

      var data2 = ReadFile("model_java2.data");
      ProfileTO tom2 = new ProfileTO();
      tom2.mergeFrom(CodedInputStream.CreateInstance(data2), new CodeGenContext());
      Assert.IsTrue(tom2.sameAs(tom));
    }

    [Test()]
    public void patchModel()
    {
      ProfileTO tom = createProfile("tom", 10, 10000);
      tom.setPenalty(0.9f);
      tom.setWarBonus(0.5d);
      tom.addCollections(createResourceTO(ResourceTypeTO.Liquid, 22));

      ProfileTO andy = createProfile("andy", 11, 1418555521001L);
      ProfileTO lu = createProfile("lu", 12, 1418555521000L);
      tom.addFriends(andy);
      tom.addFriends(lu);

      byte[] data = tom.toByteArray();
      File.WriteAllBytes("model_csharp.data", data);

      ProfileTO lu2 = new ProfileTO();
      ProfileTO tom2 = new ProfileTO();
      tom2.deepCopy(tom);
      lu2.deepCopy(lu);
      tom2.getFriends().Insert(0, lu2);

      byte[] diff = tom2.patch(tom);
      File.WriteAllBytes("model_csharp_diff.data", diff);

      byte[] data2 = tom2.toByteArray();
      File.WriteAllBytes("model_csharp2.data", data2);
    }

    private ProfileTO createProfile(String id, int counter, long time)
    {
      ProfileTO profile = new ProfileTO();
      profile.setId(id);
      profile.setCounter(counter);
      Timestamp timestamp = new Timestamp();
      timestamp.setTime(time);
      profile.setCreatedOn(timestamp);

      return profile;
    }

    private ResourceTO createResourceTO(ResourceTypeTO resourceType, int amount)
    {
      ResourceTO to = new ResourceTO();
      to.setAmount(amount);
      to.setResourceType(resourceType);
      return to;
    }

    public static byte[] ReadFile(string filePath)
    {
      byte[] buffer;
      FileStream fileStream = new FileStream(filePath, FileMode.Open, FileAccess.Read);
      try
      {
        int length = (int)fileStream.Length;  // get file length
        buffer = new byte[length];            // create buffer
        int count;                            // actual number of bytes read
        int sum = 0;                          // total number of bytes read

        // read until Read method returns 0 (end of the stream has been reached)
        while ((count = fileStream.Read(buffer, sum, length - sum)) > 0)
          sum += count;  // sum is a buffer offset for next reading
      }
      finally
      {
        fileStream.Close();
      }
      return buffer;
    }
  }
}

//*/