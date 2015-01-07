package com.spaceape.protobuf.reflect;

import com.google.protobuf.CodedInputStream;
import com.spaceape.model.Profile2TO;
import com.spaceape.model.Profile3TO;
import com.spaceape.model.ProfileTO;
import com.spaceape.protobuf.CodeGenContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.spaceape.protobuf.MessageTestUtil.createProfile;

public class GenerateMessageStreamTest {
    @Test
    public void testParse() throws Exception{
        ProfileTO tom = createProfile("tom", 10, 10000);
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        ProfileTO lu = createProfile("lu", 12, 1418555521000l);
        tom.addFriends(andy);
        tom.addFriends(lu);

        byte [] data = GenerateMessageStream.toByteArray(tom);
        ProfileTO profile = (ProfileTO)GenerateMessageStream.read(data);
        Assert.assertEquals(profile.getId(), tom.getId());
        Assert.assertEquals(profile.getCounter(), tom.getCounter());
        Assert.assertEquals(profile.getCreatedOn().getTime(), tom.getCreatedOn().getTime());

        Assert.assertEquals(profile.getFriends().size(), 2);
    }

    @Test
    public void testMissingFieldBackCompatible () throws Exception {
        ProfileTO tom = createProfile("tom", 10, 10000);
        byte [] data = GenerateMessageStream.toByteArray(tom);

        Profile2TO profile = GenerateMessageStream.merge(data, new Profile2TO());
        Assert.assertEquals(profile.getId(), tom.getId());
        Assert.assertEquals(profile.getCounter(), tom.getCounter());
    }

    @Test
    public void testSkipNullString () throws Exception {
        ProfileTO tom = createProfile("tom", 10, 10000);
        tom.setName(null);
        byte [] data = GenerateMessageStream.toByteArray(tom);

        Profile3TO profile = GenerateMessageStream.merge(data, new Profile3TO());
        Assert.assertEquals(profile.getId(), tom.getId());
        Assert.assertEquals(profile.getCounter(), tom.getCounter());
    }

    @Test
    public void testCyclicDependency () throws Exception {
        ProfileTO tom = createProfile("tom", 10, 10000);
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        tom.addFriends(andy);
        andy.addFriends(tom);

        byte [] data = GenerateMessageStream.toByteArray(tom);
        System.out.println("size: "+data.length);

        ProfileTO tom2 = (ProfileTO)GenerateMessageStream.read(data);
        ProfileTO andy2 = tom2.getFriends().get(0);
        Assert.assertEquals(andy2.getFriends().get(0), tom2);
    }
}
