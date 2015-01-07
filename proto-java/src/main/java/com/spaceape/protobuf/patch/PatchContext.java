package com.spaceape.protobuf.patch;

import com.spaceape.protobuf.CodeGenContext;
import com.spaceape.protobuf.CopyContext;
import com.spaceape.protobuf.GeneratedMessage;

import java.util.HashSet;
import java.util.Set;

public class PatchContext extends CodeGenContext {
    private Set<DiffPair> patchPair = new HashSet<DiffPair>();
    private CopyContext copyContext = new CopyContext();

    public void patch(Enum left, Enum right){
    }
    public void patch(GeneratedMessage left, GeneratedMessage right){
        String leftId = generateMessageId(left);
        String rightId = generateMessageId(right);
        patch(new DiffPair(leftId, rightId));

    }
    private void patch(DiffPair pair){
        patchPair.add(pair);
    }

    public CopyContext getCopyContext(){
        return copyContext;
    }

    public static String padding(int depth, String pad){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <depth; i++) {
            builder.append("\t");
        }
        return builder.toString();
    }
}