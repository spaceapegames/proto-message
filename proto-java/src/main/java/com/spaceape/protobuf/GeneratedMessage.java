package com.spaceape.protobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import com.spaceape.protobuf.patch.DeltaType;
import com.spaceape.protobuf.patch.PatchContext;
import com.spaceape.protobuf.reflect.FieldDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public abstract class GeneratedMessage {

    public void deepCopy(GeneratedMessage dst, CopyContext context){}
    public boolean sameAs(GeneratedMessage dst){
        if (dst == null) return false;
        return this.getClass().getName().equals(dst.getClass().getName());
    }
    public byte [] toByteArray() throws IOException, IllegalAccessException{

        CodeGenContext context = new CodeGenContext();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(out);
        writeMessageNoTag(output, context, this);
        output.flush();
        out.flush();
        return out.toByteArray();
    }

    public void mergeFrom(CodedInputStream input, CodeGenContext context) throws IOException{
        mergeRead(input, context);
    }

    protected void read(CodedInputStream input, CodeGenContext context) {
        try {
            int tag = input.readTag();
            while (tag != 0 && WireFormat.getTagWireType(tag) != WireFormat.WIRETYPE_END_GROUP) {
                int fieldNumber = WireFormat.getTagFieldNumber(tag);
                if (!readField(fieldNumber, input, context)){
                    input.skipField(tag);
                }
                tag = input.readTag();
            }
        }catch (Exception e){
            throw new MessageParsingException(e);
        }
    }

    public static GeneratedMessage readMessage (CodedInputStream input, CodeGenContext context, ConstructorFactory constructorFactory) throws IOException{
        final boolean hasValue = input.readBool();
        if (!hasValue){
            int tag = input.readTag();
            if (tag != 0 && WireFormat.getTagWireType(tag) != WireFormat.WIRETYPE_END_GROUP){
                throw new IllegalArgumentException("invalid null message end with tag "+tag);
            }
            return null;
        }
        String id = input.readStringRequireUtf8();

        if (context.contains(id)) return context.getMessage(id);
        else {
            String className = input.readStringRequireUtf8();

            GeneratedMessage message = constructorFactory.newInstance(className);
            context.touch(id, message);
            message.read(input, context);
            return message;
        }
    }

    private void mergeRead(CodedInputStream input, CodeGenContext context) throws IOException{
        final boolean hasValue = input.readBool();
        if (!hasValue){
            return;
        }
        String id = input.readStringRequireUtf8(); //ignore id
        String className = input.readStringRequireUtf8(); // ignore original class

        if (!context.contains(id)) {
            context.touch(id, this);
        }
        this.read(input, context);
    }

    public static void writeMessage (CodedOutputStream output, CodeGenContext context, int fieldNumber, GeneratedMessage message) throws IOException{
        output.writeTag(fieldNumber, WireFormat.WIRETYPE_START_GROUP);
        writeMessageNoTag(output, context, message);
        output.writeTag(fieldNumber, WireFormat.WIRETYPE_END_GROUP);
    }

    private static void writeMessageNoTag (CodedOutputStream output, CodeGenContext context, GeneratedMessage message) throws IOException{
        if (message == null) {
            output.writeBoolNoTag(false);
        } else {
            output.writeBoolNoTag(true);
            String id = context.generateMessageId(message);
            output.writeStringNoTag(id);

            if (!context.contains(id)) {
                context.touch(id, message);
                output.writeStringNoTag(message.getClass().getName());

                message.writeField(output, context);
            }
        }
    }

    protected boolean readField(int fieldNumber, CodedInputStream input, CodeGenContext context) throws IOException {
        return false;
    }
    protected void writeField(CodedOutputStream output, CodeGenContext context) throws IOException{}
    public String getMessageId () {
        return null;
    }

    /**===================== Patch base ====================*/
    public byte [] patch(GeneratedMessage rightMessage) throws IOException{
        PatchContext context = new PatchContext();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(out);
        if (!patchMessageNoTag(this, rightMessage, output, context)){
            return new byte[0];
        }
        output.flush();
        out.flush();
        return out.toByteArray();
    }
    public boolean patch(GeneratedMessage obj, CodedOutputStream output, PatchContext context) throws IOException{
        return false;
    }

    public static <T extends GeneratedMessage> boolean patchMessageList(int fieldNumber, List<T> left, List<T> right, CodedOutputStream output, PatchContext context) throws IOException{
        boolean hasPatch = false;
        //prepare id-pos map
        Map<String, List<Integer>> rightIds = new HashMap<String, List<Integer>>();
        for (int i=0;i<right.size();i++) {
            String rightId = context.generateMessageId(right.get(i));
            List<Integer> positions;
            if (rightIds.containsKey(rightId)){
                positions = rightIds.get(rightId);
            } else {
                positions = new ArrayList<Integer>();
            }
            positions.add(i);
            rightIds.put(rightId, positions);
        }

        //patch element with same id first
        Set<Integer> patchedPositions = new HashSet<Integer>();
        for (int i=0;i<left.size();i++) {
            String leftTmpId = context.generateMessageId(left.get(i));
            if (rightIds.containsKey(leftTmpId)){
                List<Integer> availablePos = rightIds.get(leftTmpId);
                if (availablePos.size() > 0) {
                    Integer pos = availablePos.get(0);

                    output.writeBoolNoTag(true); //not list order
                    output.writeInt32NoTag(i);
                    output.writeInt32NoTag(pos);

                    if (patchMessage(fieldNumber, left.get(i), right.get(pos), output, context)){
                        hasPatch = true;
                    }
                    patchedPositions.add(i);

                    availablePos.remove(0);
                    if (availablePos.size() > 0)
                        rightIds.put(leftTmpId, availablePos);
                    else
                        rightIds.remove(leftTmpId);
                }

            }
        }
        List<Integer> remainingPositions = new ArrayList<Integer>();
        for (String id: rightIds.keySet()){
            List<Integer> positions = rightIds.get(id);
            remainingPositions.addAll(positions);
        }
        Collections.sort(remainingPositions);

        //patch element without same id found
        for (int i=0;i<left.size();i++) {
            if (!patchedPositions.contains(i)){
                output.writeBoolNoTag(false); //same list order
                if (remainingPositions.size()>0){
                    if (patchMessage(fieldNumber, left.get(i), right.get(remainingPositions.get(0)), output, context)){
                        hasPatch = true;
                    }
                    remainingPositions.remove(0);
                } else {
                    hasPatch = true;
                    output.writeInt32(fieldNumber, DeltaType.Value.id);
                    writeMessage(output, context, fieldNumber, left.get(i));
                }
            }
        }
        return hasPatch;
    }

    protected static boolean patchMessage(int fieldNumber, GeneratedMessage left, GeneratedMessage right, CodedOutputStream output, PatchContext context) throws IOException{
        output.writeInt32(fieldNumber, DeltaType.Message.id);

        output.writeTag(fieldNumber, WireFormat.WIRETYPE_START_GROUP);
        output.writeStringNoTag(left.getClass().getName());
        boolean hasPatch = patchMessageNoTag(left, right, output, context);
        output.writeTag(fieldNumber, WireFormat.WIRETYPE_END_GROUP);
        return hasPatch;
    }
    private static boolean patchMessageNoTag(GeneratedMessage left, GeneratedMessage right, CodedOutputStream output, PatchContext context) throws IOException{
        context.patch(left, right);
        return left.patch(right, output, context);
    }

    public void applyPatch(byte [] data) throws IOException{
        CodedInputStream input = CodedInputStream.newInstance(data);
        PatchContext context = new PatchContext();
        applyPatchToMessage(input, context);
    }
    public static GeneratedMessage applyPatch(GeneratedMessage right, CodedInputStream input, PatchContext context, ConstructorFactory constructorFactory) throws IOException{
        String className = input.readString();
        GeneratedMessage message = constructorFactory.newInstance(className);
        message.deepCopy(right, context.getCopyContext());
        message.applyPatchToMessage(input, context);

        return message;
    }
    private void applyPatchToMessage(CodedInputStream input, PatchContext context) throws IOException{
        int tag = input.readTag();
        while (tag != 0 && WireFormat.getTagWireType(tag) != WireFormat.WIRETYPE_END_GROUP) {
            int deltaType = input.readInt32();
            tag = input.readTag();
            int fieldNumber = WireFormat.getTagFieldNumber(tag);

            if (deltaType == DeltaType.Value.id){
                if (!readField(fieldNumber, input, context)){
                    input.skipField(tag);
                }
            } else if (deltaType == DeltaType.Message.id){
                if (!applyPatchOnMessageField(fieldNumber, input, context)){
                    input.skipField(tag);
                }
            } else if (deltaType == DeltaType.FullValueList.id) {
                if (!readField(fieldNumber, input, context)){
                    input.skipField(tag);
                }
            }
            else if (deltaType == DeltaType.OptimizedList.id) {
                int size = input.readInt32();
                applyOptimizedListPatch(fieldNumber, size, input, context);
            }

            tag = input.readTag();
        }
    }

    protected boolean applyPatchOnMessageField(int fieldNumber, CodedInputStream input, PatchContext context) throws IOException{
        return false;
    }

    protected boolean applyOptimizedListPatch(int fieldNumber, int size, CodedInputStream input, PatchContext context) throws IOException{
        return false;
    }

    /**===========Reflection base =====================**/
    public Map<Integer, FieldDescriptor> getFieldDescriptors(){
        return new HashMap<>();
    }
}
