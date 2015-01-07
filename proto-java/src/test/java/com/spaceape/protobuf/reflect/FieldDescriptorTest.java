package com.spaceape.protobuf.reflect;

import com.spaceape.model.ProfileTO;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class FieldDescriptorTest {
    @Test
    public void testAllFieldsInInheritance() throws Exception {
        ProfileTO profile = new ProfileTO();
        Map<Integer, FieldDescriptor> fields = profile.getFieldDescriptors();
        Assert.assertTrue(fields.containsKey(1));
    }
}
