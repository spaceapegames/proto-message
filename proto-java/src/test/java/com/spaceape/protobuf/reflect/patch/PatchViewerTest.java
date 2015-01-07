package com.spaceape.protobuf.reflect.patch;

import com.spaceape.model.ProfileTO;
import com.spaceape.model.ResourceTypeTO;
import com.spaceape.model2.Clan;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.spaceape.protobuf.MessageTestUtil.createProfile;
import static com.spaceape.protobuf.MessageTestUtil.createResourceTO;

public class PatchViewerTest {
    @Test
    public void testView() throws Exception{
        ProfileTO tom = createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        ProfileTO lu = createProfile("lu", 12, 1418555521000l);
        tom.addFriends(andy);
        tom.addFriends(lu);

        ProfileTO tom2 = new ProfileTO();
        tom2.deepCopy(tom);
        Assert.assertTrue(tom2.sameAs(tom));
        Assert.assertEquals(tom2.patch(tom).length, 0);

        byte [] data = tom.patch(andy);
        System.out.println(PatchViewer.view(data, ProfileTO.class));
    }

    @Test
    public void testPatchNullMessage () throws Exception{
        ProfileTO tom = createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        ProfileTO lu = createProfile("lu", 12, 1418555521000l);
        tom.addFriends(andy);
        tom.addFriends(lu);

        ProfileTO tom2 = new ProfileTO();
        tom2.deepCopy(tom);
        Clan clan = new Clan();
        clan.setShogun(andy);
        tom2.setClan(clan);

        byte [] data = tom2.patch(tom);
        System.out.println(PatchViewer.view(data, ProfileTO.class));

    }

    @Test
    public void testOneListDiff () throws Exception{
        ProfileTO tom = createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        ProfileTO lu = createProfile("lu", 12, 1418555521000l);
        tom.addFriends(andy);
        tom.addFriends(lu);

        ProfileTO tom2 = createProfile("tom", 10, 10000);
        tom2.setPenalty(0.9f);
        tom2.setWarBonus(0.5d);
        tom2.addCollections(createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy2 = createProfile("andy", 11, 1418555521001l);
        ProfileTO lu2 = createProfile("lu", 12, 1418555521000l);
        tom2.addFriends(lu2);
        tom2.addFriends(lu2);

        byte [] data = tom.patch(tom2);
        System.out.println(PatchViewer.view(data, ProfileTO.class));
    }
}
