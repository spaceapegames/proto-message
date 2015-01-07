package com.spaceape.protobuf;

import com.spaceape.common.Timestamp;
import com.spaceape.model.ProfileTO;
import com.spaceape.model.ResourceTO;
import com.spaceape.model.ResourceTypeTO;

public class MessageTestUtil {
    public static ProfileTO createProfile(String id, int counter, long time){
        ProfileTO profile = new ProfileTO();
        profile.setId(id);
        profile.setCounter(counter);
        Timestamp timestamp = new Timestamp();
        timestamp.setTime(time);
        profile.setCreatedOn(timestamp);

        return profile;
    }

    public static ResourceTO createResourceTO(ResourceTypeTO resourceType, int amount){
        ResourceTO to = new ResourceTO();
        to.setAmount(amount);
        to.setResourceType(resourceType);
        return to;
    }
}
