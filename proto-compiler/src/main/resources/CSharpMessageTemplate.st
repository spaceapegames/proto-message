using System;
using System.IO;
using System.Collections.Generic;
using com.spaceape.protobuf;
using Google.ProtocolBuffers;
$importedPacks: { pack |
using $pack$;
}$

$if (patch) $
using com.spaceape.protobuf.patch;
$endif$

$if (reflect)$
using com.spaceape.protobuf.reflect;
$endif$

namespace $packageName$ {
    public class $className$ : $superClassName$ {

$if (reflect)$
    private static Dictionary<int, FieldDescriptor> fieldDescriptors = new Dictionary<int, FieldDescriptor>();

    override public Dictionary<int, FieldDescriptor> getFieldDescriptors(){
        foreach(var entry in base.getFieldDescriptors()) {
            fieldDescriptors.Add(entry.Key,entry.Value);
        }
        return fieldDescriptors;
    }
    static $className$(){
    $fields: {field |
    $if (field.fieldType.isRepeatableType)$
        $if (field.fieldType.repeatingType.isMessageType)$
        fieldDescriptors.Add($field.fieldNumber$, new ListFieldDescriptor<$field.fieldType.repeatingType.langType$>($field.fieldNumber$, "$field.fieldName$", typeof($className$), new $field.fieldType.repeatingType.fieldDescriptor$($field.fieldNumber$, "$field.fieldName$", typeof($className$), ClassRegistry.getInstance(), typeof($field.fieldType.repeatingType.langType$))));
        $else$
        fieldDescriptors.Add($field.fieldNumber$, new ListFieldDescriptor<$field.fieldType.repeatingType.langType$>($field.fieldNumber$, "$field.fieldName$", typeof($className$), new $field.fieldType.repeatingType.fieldDescriptor$($field.fieldNumber$, "$field.fieldName$", typeof($className$))));
        $endif$
    $elseif (field.fieldType.isMessageType)$
        fieldDescriptors.Add($field.fieldNumber$, new $field.fieldType.fieldDescriptor$($field.fieldNumber$, "$field.fieldName$", typeof($className$), ClassRegistry.getInstance(), typeof($field.fieldType.langType$)));
    $else$
        fieldDescriptors.Add($field.fieldNumber$, new $field.fieldType.fieldDescriptor$($field.fieldNumber$, "$field.fieldName$", typeof($className$)));
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
            this.$field.fieldName$.Add(input);
        \}
        $endif$
    }$

        public void deepCopy($className$ dst){
            deepCopy(dst, new CopyContext());
        }
        public override void deepCopy(GeneratedMessage o, CopyContext context){
            $className$ dst = ($className$)o;
            base.deepCopy(dst, context);
            ConstructorFactory factory = ClassRegistry.getInstance();
            $fields: {field |
            $if (field.fieldType.isRepeatableType)$
                List<$field.fieldType.repeatingType.langType$> new$field.fieldName$ = new List<$field.fieldType.repeatingType.langType$>(dst.$field.fieldName$.Count);
                for (int i=0;i<dst.$field.fieldName$.Count;i++){
                    $if (field.fieldType.repeatingType.isMessageType && !field.fieldType.repeatingType.enumMsg)$ {
                        $field.fieldType.repeatingType.langType$ newRepeatingElem = ($field.fieldType.repeatingType.langType$)context.copyFrom(dst.$field.fieldName$[i], factory);
                        new$field.fieldName$.Add(newRepeatingElem);
                    \}
                    $else$
                        new$field.fieldName$.Add(dst.$field.fieldName$[i]);
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

        public override Boolean sameAs(GeneratedMessage obj){
            if (!base.sameAs(obj)) return false;
            if (!(obj is $className$)) return false;
            $className$ o = ($className$)obj;

            $fields: {field |
                $if (field.fieldType.isRepeatableType)$
                    if ($field.fieldName$.Count != o.$field.fieldName$.Count) return false;
                    for (int i=0;i<$field.fieldName$.Count;i++){
                        $if (field.fieldType.repeatingType.isMessageType)$ {
                            if ($field.fieldName$[i] == null)
                                if (o.$field.fieldName$[i] != null)
                                    return false;
                            else
                                if (!$field.fieldName$[i].sameAs(o.$field.fieldName$[i])) return false;
                        \}
                        $else$
                            if (DiffUtil.isDifferent($field.fieldName$[i], o.$field.fieldName$[i])) return false;
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

        protected override Boolean readField(int fieldNumber, CodedInputStream input, CodeGenContext context) {
            if (base.readField(fieldNumber, input, context)){
                return true;
            }
            $fields: {field |
            if (fieldNumber == $field.fieldNumber$){
                $if (field.fieldType.isRepeatableType)$
                    int size = input.ReadInt32();
                    $field.fieldType.langType$ new$field.fieldName$ = new List<$field.fieldType.repeatingType.langType$>(size);
                    for (int i=0;i<size;i++){
                        $if (field.fieldType.repeatingType.isMessageType)$
                            uint tag;
                            input.ReadTag(out tag);
                            new$field.fieldName$.Add(($field.fieldType.repeatingType.langType$)$field.fieldType.repeatingType.langType$.readMessage (input, context, ClassRegistry.getInstance()));
                        $else$
                            new$field.fieldName$.Add(input.$field.fieldType.inputMethod$());
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

        protected override void writeField(CodedOutputStream output, CodeGenContext context) {
            base.writeField(output,context);
            $fields: {field |
            $if (field.fieldType.isRepeatableType)$
                output.WriteInt32($field.fieldNumber$, this.$field.fieldName$.Count);
                for (int i=0;i<this.$field.fieldName$.Count;i++){
                    $if (field.fieldType.repeatingType.isMessageType)$
                        $field.fieldType.repeatingType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldName$[i]);
                    $else$
                        output.$field.fieldType.outputMethod$($field.fieldNumber$, $field.fieldName$[i]);
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
        public override Boolean patch(GeneratedMessage rightMessage, CodedOutputStream output, PatchContext context) {
            Boolean hasPatch = false;
            $className$ obj = ($className$)rightMessage;

            if (base.patch(obj, output, context)){
                hasPatch = true;
            }
            $fields: {field |
            $if (field.fieldType.isRepeatableType)$
                $if (field.fieldType.repeatingType.isMessageType)$
                    $if (field.fieldType.repeatingType.enumMsg) $
                        if (DiffUtil.isDifferentList($field.fieldName$, obj.$field.fieldName$)){
                            hasPatch = true;
                            output.WriteInt32($field.fieldNumber$, (int)DeltaType.FullValueList);
                            output.WriteInt32($field.fieldNumber$, this.$field.fieldName$.Count);
                            for (int i=0;i<this.$field.fieldName$.Count;i++){
                                $field.fieldType.repeatingType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldName$[i]);
                            \}
                        \}
                    $else$
                        MemoryStream out$field.fieldNumber$ = new MemoryStream();
                        CodedOutputStream output$field.fieldNumber$ = CodedOutputStream.CreateInstance(out$field.fieldNumber$);
                        output$field.fieldNumber$.WriteInt32($field.fieldNumber$, (int)DeltaType.OptimizedList);
                        output$field.fieldNumber$.WriteInt32($field.fieldNumber$, this.$field.fieldName$.Count);
                        if (patchMessageList($field.fieldNumber$, $field.fieldName$, obj.$field.fieldName$, output$field.fieldNumber$, context)){
                            hasPatch = true;
                            output$field.fieldNumber$.Flush();
                            out$field.fieldNumber$.Flush();
                            output.WriteRawBytes(out$field.fieldNumber$.ToArray());
                        \}
                    $endif$
                $else$
                    if (DiffUtil.isDifferentList($field.fieldName$, obj.$field.fieldName$)){
                        hasPatch = true;
                        output.WriteInt32($field.fieldNumber$, (int)DeltaType.FullValueList);
                        output.WriteInt32($field.fieldNumber$, this.$field.fieldName$.Count);
                        for (int i=0;i<this.$field.fieldName$.Count;i++){
                            output.$field.fieldType.outputMethod$($field.fieldNumber$, $field.fieldName$[i]);
                        \}
                    \}
                $endif$

            $elseif (field.fieldType.isMessageType)$
                if ($field.fieldName$ != null && obj.$field.fieldName$ != null) {
                    $if (field.fieldType.enumMsg) $
                        if ($field.fieldName$ != obj.$field.fieldName$){
                            hasPatch = true;
                            output.WriteInt32($field.fieldNumber$, (int)DeltaType.Value);
                            $field.fieldType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldName$);
                        \}
                    $else$ {
                        MemoryStream out$field.fieldNumber$ = new MemoryStream();
                        CodedOutputStream output$field.fieldNumber$ = CodedOutputStream.CreateInstance(out$field.fieldNumber$);
                        if (patchMessage($field.fieldNumber$, $field.fieldName$, obj.$field.fieldName$, output$field.fieldNumber$, context)){
                            hasPatch = true;
                            output$field.fieldNumber$.Flush();
                            out$field.fieldNumber$.Flush();
                            output.WriteRawBytes(out$field.fieldNumber$.ToArray());
                        \}
                    \}
                    $endif$
                \} else if ($field.fieldName$ != null && obj.$field.fieldName$ == null) {
                    hasPatch = true;
                    output.WriteInt32($field.fieldNumber$, (int)DeltaType.Value);
                    $field.fieldType.langType$.writeMessage(output, context, $field.fieldNumber$, $field.fieldName$);
                \} else if ($field.fieldName$ == null && obj.$field.fieldName$ != null) {
                    hasPatch = true;
                    output.WriteInt32($field.fieldNumber$, (int)DeltaType.Value);
                    $field.fieldType.langType$.writeMessage(output, context, $field.fieldNumber$, null);
                \}
            $else$
                if (DiffUtil.isDifferent($field.fieldName$, obj.$field.fieldName$)) {
                    hasPatch = true;
                    output.WriteInt32($field.fieldNumber$, (int)DeltaType.Value);
                    output.$field.fieldType.outputMethod$($field.fieldNumber$, $field.fieldName$);
                \}
            $endif$
            }$
            return hasPatch;
        }

        protected override Boolean applyPatchOnMessageField(int fieldNumber, CodedInputStream input, PatchContext context) {
            if (base.applyPatchOnMessageField(fieldNumber, input, context)){
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

        protected override Boolean applyOptimizedListPatch(int fieldNumber, int size, CodedInputStream input, PatchContext context) {
            if (base.applyOptimizedListPatch(fieldNumber, size, input, context)){
                return true;
            }
            $fields: {field |
                $if (field.fieldType.isRepeatableType && !field.fieldType.repeatingType.enumMsg)$
                if (fieldNumber == $field.fieldNumber$){
                    List<$field.fieldType.repeatingType.langType$> new$field.fieldName$ = new List<$field.fieldType.repeatingType.langType$>(size);
                    for (int i=0;i<size;i++){
                        Boolean newOrder = input.ReadBool();
                        if (newOrder) {
                            int newPos = input.ReadInt32();
                            int oldPos = input.ReadInt32();
                            new$field.fieldName$.Insert(newPos, ($field.fieldType.repeatingType.langType$)applyPatch($field.fieldName$[oldPos], input, context, ClassRegistry.getInstance()));
                        \} else {
                            uint tag;
                            input.ReadTag(out tag);
                            int deltaType = input.ReadInt32();
                            input.ReadTag(out tag);
                            if (WireFormat.GetTagWireType(tag) != WireFormat.WireType.StartGroup){
                                throw new Exception("can't read messagewithout start tag");
                            \}
                            if (deltaType == (int)DeltaType.Value) {
                                new$field.fieldName$.Insert(i, ($field.fieldType.repeatingType.langType$)$field.fieldType.repeatingType.langType$.readMessage (input, context, ClassRegistry.getInstance()));
                            \}
                            else if (deltaType == (int)DeltaType.Message)
                                new$field.fieldName$.Insert(i, ($field.fieldType.repeatingType.langType$)applyPatch($field.fieldName$[i], input, context, ClassRegistry.getInstance()));
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
}