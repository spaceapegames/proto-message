package com.spaceape.protobuf;

//*
import com.google.protobuf.CodedInputStream;
import com.spaceape.common.Timestamp;
import com.spaceape.model.ProfileTO;
import com.spaceape.model.ResourceTO;
import com.spaceape.model.ResourceTypeTO;
import org.testng.Assert;
import org.testng.annotations.Test;
import static com.spaceape.protobuf.MessageTestUtil.*;

import java.io.File;
import java.nio.file.Files;

public class CrossLangTest {
    @Test
    public void writeModel() throws Exception{
        ProfileTO tom = createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        ProfileTO lu = createProfile("lu", 12, 1418555521000l);
        tom.addFriends(andy);
        tom.addFriends(lu);

        byte [] data = tom.toByteArray();
        Files.write(new File("proto-csharp/ProtoMessage/ProtoMessage/bin/Debug/model_java.data").toPath(), data);
    }

    @Test
    public void readModel() throws Exception {
        byte [] data = Files.readAllBytes(new File("proto-csharp/ProtoMessage/ProtoMessage/bin/Debug/model_csharp.data").toPath());
        ProfileTO profile = new ProfileTO();
        profile.mergeFrom(CodedInputStream.newInstance(data), new CodeGenContext());
        Assert.assertEquals(profile.getId(), "tom");
        Assert.assertEquals(profile.getCounter(), 10);
        Assert.assertEquals(profile.getCreatedOn().getTime(), 10000);
        Assert.assertEquals(profile.getPenalty(), 0.9f);
        Assert.assertEquals(profile.getWarBonus(), 0.5d);
        Assert.assertEquals(profile.getCollections().size(), 1);
        Assert.assertEquals(profile.getCollections().get(0).getResourceType(), ResourceTypeTO.Liquid);
        Assert.assertEquals(profile.getCollections().get(0).getAmount(), 22);

        Assert.assertEquals(profile.getFriends().size(), 2);
    }

    @Test
    public void patchModel() throws Exception{
        ProfileTO tom = createProfile("tom", 10, 10000);
        tom.setPenalty(0.9f);
        tom.setWarBonus(0.5d);
        tom.addCollections(createResourceTO(ResourceTypeTO.Liquid, 22));
        ProfileTO andy = createProfile("andy", 11, 1418555521001l);
        ProfileTO lu = createProfile("lu", 12, 1418555521000l);
        tom.addFriends(andy);
        tom.addFriends(lu);

        byte [] data = tom.toByteArray();
        Files.write(new File("proto-csharp/ProtoMessage/ProtoMessage/bin/Debug/model_java.data").toPath(), data);

        ProfileTO lu2 = new ProfileTO();
        ProfileTO tom2 = new ProfileTO();
        tom2.deepCopy(tom);
        lu2.deepCopy(lu);
        tom2.getFriends().add(0, lu2);

        byte [] diff = tom2.patch(tom);
        Files.write(new File("proto-csharp/ProtoMessage/ProtoMessage/bin/Debug/model_java_diff.data").toPath(), diff);

        byte [] data2 = tom2.toByteArray();
        Files.write(new File("proto-csharp/ProtoMessage/ProtoMessage/bin/Debug/model_java2.data").toPath(), data2);
    }

    @Test
    public void applyPatch() throws Exception
    {
        byte [] data = Files.readAllBytes(new File("proto-csharp/ProtoMessage/ProtoMessage/bin/Debug/model_csharp.data").toPath());
        ProfileTO tom = new ProfileTO();
        tom.mergeFrom(CodedInputStream.newInstance(data), new CodeGenContext());

        byte [] diff = Files.readAllBytes(new File("proto-csharp/ProtoMessage/ProtoMessage/bin/Debug/model_csharp_diff.data").toPath());
        tom.applyPatch(diff);

        byte [] data2 = Files.readAllBytes(new File("proto-csharp/ProtoMessage/ProtoMessage/bin/Debug/model_csharp2.data").toPath());
        ProfileTO tom2 = new ProfileTO();
        tom2.mergeFrom(CodedInputStream.newInstance(data2), new CodeGenContext());
        Assert.assertTrue(tom2.sameAs(tom));
    }
}
//*/
