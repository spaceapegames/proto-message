//*
using System;
using System.IO;
using NUnit.Framework;
using com.spaceape.protobuf;
using com.spaceape.common;
using com.spaceape.model2;
using Google.ProtocolBuffers;

namespace com.spaceape.model
{
  [TestFixture()]
  public class ProfileTOTest
  {
    [Test()]
    public void TestParse()
    {
      ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
      ProfileTO andy = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
      ProfileTO lu = MessageTestUtil.createProfile("lu", 12, 1418555521000L);
      tom.addFriends(andy);
      tom.addFriends(lu);

      byte[] data = tom.toByteArray();
      ProfileTO profile = new ProfileTO();
      profile.mergeFrom(CodedInputStream.CreateInstance(data), new CodeGenContext());
      Assert.AreEqual(profile.getId(), tom.getId());
      Assert.AreEqual(profile.getCounter(), tom.getCounter());
      Assert.AreEqual(profile.getCreatedOn().getTime(), tom.getCreatedOn().getTime());

      Assert.AreEqual(profile.getFriends().Count, 2);
    }

    [Test()]
    public void TestMissingFieldBackCompatible()
    {
      ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
      byte[] data = tom.toByteArray();

      Profile2TO profile = new Profile2TO();
      profile.mergeFrom(CodedInputStream.CreateInstance(data), new CodeGenContext());
      Assert.AreEqual(profile.getId(), tom.getId());
      Assert.AreEqual(profile.getCounter(), tom.getCounter());
    }

    //[Test()]
    //    public void TestSimpleDiff () {
    //        ProfileTO tom = createProfile("tom", 10, 10000);
    //        ProfileTO andy = createProfile("andy", 11, 1418555521001L);
    //        ProtoDiff diff = DiffHelper.compareDiff(tom.getAllFields(), andy.getAllFields());
    //
    //        MemoryStream output = new MemoryStream();
    //        diff.print(output);
    //        output.flush();
    //        Assert.AreEqual(new String(output.toByteArray()), "{id :{newValue: tom, oldValue: andy}}");
    //    }

    [Test()]
    public void TestCyclicDependency()
    {
      ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
      ProfileTO andy = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
      tom.addFriends(andy);
      andy.addFriends(tom);

      byte[] data = tom.toByteArray();
      Console.WriteLine("size: " + data.Length);

      ProfileTO tom2 = new ProfileTO();
      tom2.mergeFrom(CodedInputStream.CreateInstance(data), new CodeGenContext());
      ProfileTO andy2 = tom2.getFriends()[0];
      Assert.AreEqual(andy2.getFriends()[0], tom2);
    }

    [Test()]
    public void testCyclicDeepCopy(){
        ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
        ProfileTO andy = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
        tom.addFriends(andy);
        andy.addFriends(tom);

        ProfileTO tom2 = new ProfileTO();
        tom2.deepCopy(tom);

        Assert.IsTrue(tom2.sameAs(tom));
    }

    [Test()]
    public void testCyclicSameAs(){
        ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
        ProfileTO tom2 = MessageTestUtil.createProfile("tom", 10, 10000);
        ProfileTO tom3 = MessageTestUtil.createProfile("tom", 10, 10000);
        ProfileTO andy = MessageTestUtil.createProfile("andy", 11, 1418555521001L);

        tom.addFriends(andy);
        tom2.addFriends(tom);
        andy.addFriends(tom);
        tom3.addFriends(tom);

        Assert.IsTrue(tom2.sameAs(tom));
        Assert.IsTrue(tom.sameAs(tom2));

        Assert.IsFalse(tom.sameAs(andy));
        Assert.IsFalse(tom3.sameAs(andy));
    }

    [Test()]
    public void TestSkipNullString () {
        ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
        tom.setName(null);
        byte [] data = tom.toByteArray();

        Profile3TO profile = new Profile3TO();
        profile.mergeFrom(CodedInputStream.CreateInstance(data), new CodeGenContext());
        Assert.AreEqual(profile.getId(), tom.getId());
        Assert.AreEqual(profile.getCounter(), tom.getCounter());
    }
  }
}

//*/