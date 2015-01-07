using System;
using com.spaceape.protobuf;
using Google.ProtocolBuffers;

namespace $packageName$ {

    public enum $className$Enum {
        $fields: {field |
        $field.fieldName$ = $field.fieldNumber$,
        }$
    }

    public class $className$ {
        $fields: {field |
        public const int $field.upperFieldName$ = $field.fieldNumber$;
        public static readonly $className$ $field.fieldName$ = new $className$($className$Enum.$field.fieldName$);
        }$

        private $className$Enum value;

        private $className$($className$Enum value){
            this.value = value;
        }

        public static $className$ valueOf(int value) {
            switch (value) {
            $fields: {field |
                case $field.fieldNumber$:
                    return $field.fieldName$;
            }$
                default: return null;
            }
        }

        public int getNumber() { return (int)value; }
        public $className$Enum getValue() { return value; }

        public Boolean sameAs($className$ obj){
            if (obj == null) return false;
            return this.value == obj.value;
        }

        public static $className$ readMessage (CodedInputStream input, CodeGenContext context, ConstructorFactory constructorFactory) {
            return valueOf(input.ReadInt32());
        }

        public static void writeMessage (CodedOutputStream output, CodeGenContext context, int fieldNumber, $className$ message) {
            output.WriteInt32(fieldNumber, (int)message.value);
        }
    }
}