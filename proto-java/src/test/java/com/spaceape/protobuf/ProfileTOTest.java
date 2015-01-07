package com.spaceape.protobuf;
//*

import com.google.protobuf.CodedInputStream;
import com.spaceape.common.Timestamp;
import com.spaceape.model.Profile2TO;
import com.spaceape.model.Profile3TO;
import com.spaceape.model.ProfileTO;
import org.testng.Assert;
import org.testng.annotations.Test;
import static com.spaceape.protobuf.MessageTestUtil.createProfile;

public class ProfileTOTest {
    @Test
    public void testParse() throws Exception{
        ProfileTO tom = createProfile("tom", 10, 10000);
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        ProfileTO lu = createProfile("lu", 12, 1418555521000l);
        tom.addFriends(andy);
        tom.addFriends(lu);

        byte [] data = tom.toByteArray();
        ProfileTO profile = new ProfileTO();
        profile.mergeFrom(CodedInputStream.newInstance(data), new CodeGenContext());
        Assert.assertEquals(profile.getId(), tom.getId());
        Assert.assertEquals(profile.getCounter(), tom.getCounter());
        Assert.assertEquals(profile.getCreatedOn().getTime(), tom.getCreatedOn().getTime());

        Assert.assertEquals(profile.getFriends().size(), 2);
    }

    @Test
    public void testMissingFieldBackCompatible () throws Exception {
        ProfileTO tom = createProfile("tom", 10, 10000);
        byte [] data = tom.toByteArray();

        Profile2TO profile = new Profile2TO();
        profile.mergeFrom(CodedInputStream.newInstance(data), new CodeGenContext());
        Assert.assertEquals(profile.getId(), tom.getId());
        Assert.assertEquals(profile.getCounter(), tom.getCounter());
    }

    @Test
    public void testSkipNullString () throws Exception {
        ProfileTO tom = createProfile("tom", 10, 10000);
        tom.setName(null);
        byte [] data = tom.toByteArray();

        Profile3TO profile = new Profile3TO();
        profile.mergeFrom(CodedInputStream.newInstance(data), new CodeGenContext());
        Assert.assertEquals(profile.getId(), tom.getId());
        Assert.assertEquals(profile.getCounter(), tom.getCounter());
    }

//    @Test
//    public void testSimpleDiff () throws IOException{
//        ProfileTO tom = createProfile("tom", 10, 10000);
//        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
//        ProtoDiff diff = DiffHelper.compareDiff(tom.getAllFields(), andy.getAllFields());
//
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        diff.print(output);
//        output.flush();
//        Assert.assertEquals(new String(output.toByteArray()), "{id :{newValue: tom, oldValue: andy}}");
//    }

    @Test
    public void testCyclicDependency () throws Exception {
        ProfileTO tom = createProfile("tom", 10, 10000);
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        tom.addFriends(andy);
        andy.addFriends(tom);

        byte [] data = tom.toByteArray();
        System.out.println("size: "+data.length);

        ProfileTO tom2 = new ProfileTO();
        tom2.mergeFrom(CodedInputStream.newInstance(data), new CodeGenContext());
        ProfileTO andy2 = tom2.getFriends().get(0);
        Assert.assertEquals(andy2.getFriends().get(0), tom2);
    }

    @Test
    public void testCyclicDeepCopy(){
        ProfileTO tom = createProfile("tom", 10, 10000);
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        tom.addFriends(andy);
        andy.addFriends(tom);

        ProfileTO tom2 = new ProfileTO();
        tom2.deepCopy(tom);

        Assert.assertTrue(tom2.sameAs(tom));
    }

    @Test
    public void testCyclicSameAs(){
        ProfileTO tom = createProfile("tom", 10, 10000);
        ProfileTO tom2 = createProfile("tom", 10, 10000);
        ProfileTO tom3 = createProfile("tom", 10, 10000);
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);

        tom.addFriends(andy);
        tom2.addFriends(tom);
        andy.addFriends(tom);
        tom3.addFriends(tom);

        Assert.assertTrue(tom2.sameAs(tom));
        Assert.assertTrue(tom.sameAs(tom2));

        Assert.assertFalse(tom.sameAs(andy));
        Assert.assertFalse(tom3.sameAs(andy));
    }

}

//*/