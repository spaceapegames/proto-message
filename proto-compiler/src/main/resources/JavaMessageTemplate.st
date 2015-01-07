package $packageName$;

import com.google.protobuf.*;
import com.spaceape.protobuf.*;
import java.util.*;
import java.io.*;

$if (patch) $
import com.spaceape.protobuf.patch.*;
$endif$

$if (reflect)$
import com.spaceape.protobuf.reflect.*;
$endif$

$importedPacks: { pack |
import $pack$.*;
}$

public class $className$ extends $superClassName$ {
$if (reflect)$
    private static Map<Integer, FieldDescriptor> fieldDescriptors = new HashMap<Integer, FieldDescriptor>();
    @Override
    public Map<Integer, FieldDescriptor> getFieldDescriptors(){
        fieldDescriptors.putAll(super.getFieldDescriptors());
        return fieldDescriptors;
    }
    static {
    $fields: {field |
    $if (field.fieldType.isRepeatableType)$
        $if (field.fieldType.repeatingType.isMessageType)$
        fieldDescriptors.put($field.fieldNumber$, new ListFieldDescriptor<$field.fieldType.repeatingType.langType$>($field.fieldNumber$, "$field.fieldName$", $className$.class, new $field.fieldType.repeatingType.fieldDescriptor$($field.fieldNumber$, "$field.fieldName$", $className$.class, ClassRegistry.getInstance(), $field.fieldType.repeatingType.langType$.class)));
        $else$
        fieldDescriptors.put($field.fieldNumber$, new ListFieldDescriptor<$field.fieldType.repeatingType.langType$>($field.fieldNumber$, "$field.fieldName$", $className$.class, new $field.fieldType.repeatingType.fieldDescriptor$($field.fieldNumber$, "$field.fieldName$", $className$.class)));
        $endif$
    $elseif (field.fieldType.isMessageType)$
        fieldDescriptors.put($field.fieldNumber$, new $field.fieldType.fieldDescriptor$($field.fieldNumber$, "$field.fieldName$", $className$.class, ClassRegistry.getInstance(), $field.fieldType.langType$.class));
    $else$
        fieldDescriptors.put($field.fieldNumber$, new $field.fieldType.fieldDescriptor$($field.fieldNumber$, "$field.fieldName$", $className$.class));
    $endif$
    }$
    }
$endif$

$fields: {field |
    private $field.fieldType.langType$ $field.fieldName$ = $field.fieldType.defaultValue$;

    public $field.fieldType.langType$ $field.fieldGetter$ () {
        return $field.fieldName$;
    \}

    public void $field.fieldSetter$ ($field.fieldType.langType$ input) {
        this.$field.fieldName$ = input;
    \}

    $if (field.fieldType.isRepeatableType)$
    public void add$field.firstCapName$ ($field.fieldType.repeatingType.langType$ input) {
        this.$field.fieldName$.add(input);
    \}
    $endif$
}$
    public void deepCopy($className$ dst){
        deepCopy(dst, new CopyContext());
    }
    public void deepCopy(GeneratedMessage o, CopyContext context){
        $className$ dst = ($className$)o;
        super.deepCopy(dst, context);
        ConstructorFactory factory = ClassRegistry.getInstance();
        $fields: {field |
        $if (field.fieldType.isRepeatableType)$
            $field.fieldType.langType$ new$field.fieldName$ = new ArrayList<$field.fieldType.repeatingType.langType$>(dst.$field.fieldName$.size());
            for (int i=0;i<dst.$field.fieldName$.size();i++){
                $if (field.fieldType.repeatingType.isMessageType && !field.fieldType.repeatingType.enumMsg)$ {
                    $field.fieldType.repeatingType.langType$ newRepeatingElem = ($field.fieldType.repeatingType.langType$)context.copyFrom(dst.$field.fieldName$.get(i), factory);
                    new$field.fieldName$.add(newRepeatingElem);
                \}
                $else$
                    new$field.fieldName$.add(dst.$field.fieldName$.get(i));
                $endif$
            \}
            $field.fieldSetter$(new$field.fieldName$);
        $elseif (field.fieldType.isMessageType)$
            if (dst.$field.fieldName$ == null){
                $field.fieldName$ = null;
            \} else {
                $if (field.fieldType.enumMsg)$
                $field.fieldName$ = dst.$field.fieldName$;
                $else$
                $field.fieldName$ = ($field.fieldType.langType$)context.copyFrom(dst.$field.fieldName$, factory);
                $endif$
            \}
        $else$
            $field.fieldName$ = dst.$field.fieldName$;
        $endif$
        }$
    }

    public boolean sameAs(GeneratedMessage obj){
        if (!super.sameAs(obj)) return false;
        if (!(obj instanceof $className$)) return false;
        $className$ o = ($className$)obj;

        $fields: {field |
            $if (field.fieldType.isRepeatableType)$
                if ($field.fieldName$.size() != o.$field.fieldName$.size()) return false;
                for (int i=0;i<$field.fieldName$.size();i++){
                    $if (field.fieldType.repeatingType.isMessageType)$ {
                        if ($field.fieldName$.get(i) == null)
                            if (o.$field.fieldName$.get(i) != null)
                                return false;
                        else
                            if (!$field.fieldName$.get(i).sameAs(o.$field.fieldName$.get(i))) return false;
                    \}
                    $else$
                        if (DiffUtil.isDifferent($field.fieldName$.get(i), o.$field.fieldName$.get(i))) return false;
                    $endif$
                \}
            $elseif (field.fieldType.isMessageType)$
                if ($field.fieldName$ == null) {
                    if (o.$field.fieldName$ != null)
                        return false;
                \}
                else {
                    if (!$field.fieldName$.sameAs(o.$field.fieldName$)) return false;
                \}
            $else$
                 if (DiffUtil.isDifferent($field.fieldName$, o.$field.fieldName$)) return false;
            $endif$
        }$
        return true;
    }

    protected boolean readField(int fieldNumber, CodedInputStream input, CodeGenContext context) throws IOException{
        if (super.readField(fieldNumber, input, context)){
            return true;
        }
        $fields: {field |
        if (fieldNumber == $field.fieldNumber$){
            $if (field.fieldType.isRepeatableType)$
                int size = input.readInt32();
                $field.fieldType.langType$ new$field.fieldName$ = new ArrayList<$field.fieldType.repeatingType.langType$>(size);
                for (int i=0;i<size;i++){
                    $if (field.fieldType.repeatingType.isMessageType)$
                        int tag = input.readTag();
                        new$field.fieldName$.add(($field.fieldType.repeatingType.langType$)$field.fieldType.repeatingType.langType$.readMessage (input, context, ClassRegistry.getInstance()));
                    $else$
                        new$field.fieldName$.add(input.$field.fieldType.repeatingType.inputMethod$());
                    $endif$
                \}
                $field.fieldSetter$(new$field.fieldName$);
            $elseif (field.fieldType.isMessageType)$
                $field.fieldSetter$(($field.fieldType.langType$)$field.fieldType.langType$.readMessage (input, context, ClassRegistry.getInstance()));
            $else$
                $field.fieldSetter$(input.$field.fieldType.inputMethod$());
            $endif$
            return true;
        \}
        }$
        return false;
    }

    protected void writeField(CodedOutputStream output, CodeGenContext context) throws IOException {
        super.writeField(output,context);
        $fields: {field |
        $if (field.fieldType.isRepeatableType)$
            output.writeInt32($field.fieldNumber$, this.$field.fieldName$.size());
            for (int i=0;i<this.$field.fieldName$.size();i++){
                $if (field.fieldType.repeatingType.isMessageType)$
                    $field.fieldType.repeatingType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldName$.get(i));
                $else$
                    output.$field.fieldType.outputMethod$($field.fieldNumber$, $field.fieldName$.get(i));
                $endif$
            \}
        $elseif (field.fieldType.isMessageType)$
            $field.fieldType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldGetter$());
        $else$
            output.$field.fieldType.outputMethod$($field.fieldNumber$, $field.fieldGetter$());
        $endif$
        }$
    }

$if (patch) $
    public boolean patch(GeneratedMessage rightMessage, CodedOutputStream output, PatchContext context) throws IOException{
        boolean hasPatch = false;
        $className$ obj = ($className$)rightMessage;

        if (super.patch(obj, output, context)){
            hasPatch = true;
        }
        $fields: {field |
        $if (field.fieldType.isRepeatableType)$
            $if (field.fieldType.repeatingType.isMessageType)$
                $if (field.fieldType.repeatingType.enumMsg) $
                    if (DiffUtil.isDifferentList($field.fieldName$, obj.$field.fieldName$)){
                        hasPatch = true;
                        output.writeInt32($field.fieldNumber$, DeltaType.FullValueList.id);
                        output.writeInt32($field.fieldNumber$, this.$field.fieldName$.size());
                        for (int i=0;i<this.$field.fieldName$.size();i++){
                            $field.fieldType.repeatingType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldName$.get(i));
                        \}
                    \}
                $else$
                    ByteArrayOutputStream out$field.fieldNumber$ = new ByteArrayOutputStream();
                    CodedOutputStream output$field.fieldNumber$ = CodedOutputStream.newInstance(out$field.fieldNumber$);
                    output$field.fieldNumber$.writeInt32($field.fieldNumber$, DeltaType.OptimizedList.id);
                    output$field.fieldNumber$.writeInt32($field.fieldNumber$, this.$field.fieldName$.size());
                    if (patchMessageList($field.fieldNumber$, $field.fieldName$, obj.$field.fieldName$, output$field.fieldNumber$, context)){
                        hasPatch = true;
                        output$field.fieldNumber$.flush();
                        out$field.fieldNumber$.flush();
                        output.writeRawBytes(out$field.fieldNumber$.toByteArray());
                    \}
                $endif$
            $else$
                if (DiffUtil.isDifferentList($field.fieldName$, obj.$field.fieldName$)){
                    hasPatch = true;
                    output.writeInt32($field.fieldNumber$, DeltaType.FullValueList.id);
                    output.writeInt32($field.fieldNumber$, this.$field.fieldName$.size());
                    for (int i=0;i<this.$field.fieldName$.size();i++){
                        output.$field.fieldType.outputMethod$($field.fieldNumber$, $field.fieldName$.get(i));
                    \}
                \}
            $endif$

        $elseif (field.fieldType.isMessageType)$
            if ($field.fieldName$ != null && obj.$field.fieldName$ != null) {
                $if (field.fieldType.enumMsg) $
                    if ($field.fieldName$ != obj.$field.fieldName$){
                        hasPatch = true;
                        output.writeInt32($field.fieldNumber$, DeltaType.Value.id);
                        $field.fieldType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldName$);
                    \}
                $else$ {
                    ByteArrayOutputStream out$field.fieldNumber$ = new ByteArrayOutputStream();
                    CodedOutputStream output$field.fieldNumber$ = CodedOutputStream.newInstance(out$field.fieldNumber$);
                    if (patchMessage($field.fieldNumber$, $field.fieldName$, obj.$field.fieldName$, output$field.fieldNumber$, context)){
                        hasPatch = true;
                        output$field.fieldNumber$.flush();
                        out$field.fieldNumber$.flush();
                        output.writeRawBytes(out$field.fieldNumber$.toByteArray());
                    \}
                \}
                $endif$
            \} else if ($field.fieldName$ != null && obj.$field.fieldName$ == null) {
                hasPatch = true;
                output.writeInt32($field.fieldNumber$, DeltaType.Value.id);
                $field.fieldType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldName$);
            \} else if ($field.fieldName$ == null && obj.$field.fieldName$ != null) {
                hasPatch = true;
                output.writeInt32($field.fieldNumber$, DeltaType.Value.id);
                $field.fieldType.langType$.writeMessage(output, context, $field.fieldNumber$, null);
            \}
        $else$
            if (DiffUtil.isDifferent($field.fieldName$, obj.$field.fieldName$)) {
                hasPatch = true;
                output.writeInt32($field.fieldNumber$, DeltaType.Value.id);
                output.$field.fieldType.outputMethod$($field.fieldNumber$, $field.fieldName$);
            \}
        $endif$
        }$
        return hasPatch;
    }

    protected boolean applyPatchOnMessageField(int fieldNumber, CodedInputStream input, PatchContext context)  throws IOException{
        if (super.applyPatchOnMessageField(fieldNumber, input, context)){
            return true;
        }
        $fields: {field |
            $if (field.fieldType.isMessageType && !field.fieldType.enumMsg)$
            if (fieldNumber == $field.fieldNumber$){
                $field.fieldName$ = ($field.fieldType.langType$)applyPatch($field.fieldName$, input, context, ClassRegistry.getInstance());
                return true;
            \}
            $endif$
        }$
        return false;
    }

    protected boolean applyOptimizedListPatch(int fieldNumber, int size, CodedInputStream input, PatchContext context) throws IOException {
        if (super.applyOptimizedListPatch(fieldNumber, size, input, context)){
            return true;
        }
        $fields: {field |
            $if (field.fieldType.isRepeatableType && !field.fieldType.repeatingType.enumMsg)$
            if (fieldNumber == $field.fieldNumber$){
                $field.fieldType.langType$ new$field.fieldName$ = new ArrayList<$field.fieldType.repeatingType.langType$>(size);
                for (int i=0;i<size;i++){
                    boolean newOrder = input.readBool();
                    if (newOrder) {
                        int newPos = input.readInt32();
                        int oldPos = input.readInt32();
                        new$field.fieldName$.add(newPos, ($field.fieldType.repeatingType.langType$)applyPatch($field.fieldName$.get(oldPos), input, context, ClassRegistry.getInstance()));
                    \} else {
                        int tag = input.readTag();
                        int deltaType = input.readInt32();
                        if (WireFormat.getTagWireType(input.readTag()) != WireFormat.WIRETYPE_START_GROUP){
                            throw new IllegalArgumentException("can't read messagewithout start tag");
                        \}
                        if (deltaType == DeltaType.Value.id) {
                            new$field.fieldName$.add(i, ($field.fieldType.repeatingType.langType$)$field.fieldType.repeatingType.langType$.readMessage (input, context, ClassRegistry.getInstance()));
                        \}
                        else if (deltaType == DeltaType.Message.id)
                            new$field.fieldName$.add(i, ($field.fieldType.repeatingType.langType$)applyPatch($field.fieldName$.get(i), input, context, ClassRegistry.getInstance()));
                    \}
                \}
                $field.fieldName$ = new$field.fieldName$;
                return true;
            \}
            $endif$
        }$
        return false;
    }
$endif$
}