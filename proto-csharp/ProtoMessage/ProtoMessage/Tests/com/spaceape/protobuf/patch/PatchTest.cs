//*
using System;
using System.IO;
using NUnit.Framework;
using com.spaceape.protobuf;
using com.spaceape.common;
using com.spaceape.model2;
using com.spaceape.model;
using Google.ProtocolBuffers;

namespace com.spaceape.protobuf.patch
{
  [TestFixture()]
  public class PatchTest
  {
    [Test()]
    public void testPatchMessage (){
        ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(MessageTestUtil.createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
        ProfileTO lu = MessageTestUtil.createProfile("lu", 12, 1418555521000L);
        tom.addFriends(andy);
        tom.addFriends(lu);

        ProfileTO tom2 = new ProfileTO();
        tom2.deepCopy(tom);
        Assert.IsTrue(tom2.sameAs(tom));
        Assert.AreEqual(tom2.patch(tom).Length, 0);

        byte [] data = tom.patch(andy);
        Console.WriteLine("patch size diff: "+data.Length);
        andy.applyPatch(data);
        Assert.IsTrue(andy.sameAs(tom));

    }

    [Test()]
    public void testListDiffNoDelta () {
        ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(MessageTestUtil.createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
        ProfileTO lu = MessageTestUtil.createProfile("lu", 12, 1418555521000L);
        tom.addFriends(andy);
        tom.addFriends(lu);

        ProfileTO tom2 = MessageTestUtil.createProfile("tom", 10, 10000);
        tom2.setPenalty(0.9f);
        tom2.setWarBonus(0.5d);
        tom2.addCollections(MessageTestUtil.createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy2 = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
        ProfileTO lu2 = MessageTestUtil.createProfile("lu", 12, 1418555521000L);
        tom2.addFriends(andy2);
        tom2.addFriends(lu2);

        Assert.AreEqual(tom.patch(tom2).Length, 0);
        Assert.IsTrue(tom.sameAs(tom2));
    }

    [Test()]
    public void testOneListDiff () {
        ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(MessageTestUtil.createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
        ProfileTO lu = MessageTestUtil.createProfile("lu", 12, 1418555521000L);
        tom.addFriends(andy);
        tom.addFriends(lu);

        ProfileTO tom2 = MessageTestUtil.createProfile("tom", 10, 10000);
        tom2.setPenalty(0.9f);
        tom2.setWarBonus(0.5d);
        tom2.addCollections(MessageTestUtil.createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy2 = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
        ProfileTO lu2 = MessageTestUtil.createProfile("lu", 12, 1418555521000L);
        tom2.addFriends(lu2);
        tom2.addFriends(lu2);

        byte [] diff = tom.patch(tom2);
        Assert.IsTrue(diff.Length > 0);
        tom2.applyPatch(diff);
        Assert.IsTrue(tom2.sameAs(tom));
    }

    [Test()]
    public void testNonIdMessageHasDelta () {
        ProfileTO tom = MessageTestUtil.createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(MessageTestUtil.createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
        ProfileTO lu = MessageTestUtil.createProfile("lu", 12, 1418555521000L);
        tom.addFriends(andy);
        tom.addFriends(lu);

        ProfileTO tom2 = MessageTestUtil.createProfile("tom", 10, 10000);
        tom2.setPenalty(0.9f);
        tom2.setWarBonus(0.5d);
        tom2.addCollections(MessageTestUtil.createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy2 = MessageTestUtil.createProfile("andy", 11, 1418555521001L);
        ProfileTO lu2 = MessageTestUtil.createProfile("lu", 12, 1418555521000L);
        tom2.addFriends(lu2);
        tom2.addFriends(andy2);

        byte [] diff = tom.patch(tom2);
        Assert.IsTrue(diff.Length > 0);

        tom2.applyPatch(diff);
        Assert.IsTrue(tom2.sameAs(tom));
    }
  }
}

//*/