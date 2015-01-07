using System;
using System.Collections.Generic;

namespace com.spaceape.protobuf.patch
{
  public class PatchContext: CodeGenContext {
    private HashSet<DiffPair> patchPair = new HashSet<DiffPair>();
    private CopyContext copyContext = new CopyContext();

    public void patch(Enum left, Enum right){

    }
    public void patch(GeneratedMessage left, GeneratedMessage right){
        String leftId = generateMessageId(left);
        String rightId = generateMessageId(right);
        patch(new DiffPair(leftId, rightId));

    }
    private void patch(DiffPair pair){
        patchPair.Add(pair);
    }
    public CopyContext getCopyContext(){
        return copyContext;
    }
  }
}

