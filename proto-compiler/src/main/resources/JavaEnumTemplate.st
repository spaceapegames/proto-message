package $packageName$;

import com.google.protobuf.*;
import com.spaceape.protobuf.*;
import java.io.*;

public enum $className$ {
    $fields: {field |
    $field.fieldName$($field.fieldNumber$),
    }$;

    $fields: {field |
    public static final int $field.upperFieldName$ = $field.fieldNumber$;
    }$

    public static $className$ valueOf(int value) {
        switch (value) {
        $fields: {field |
            case $field.fieldNumber$: return $field.fieldName$;
        }$
            default: return null;
        }
    }

    private final int value;

    private $className$(int value) {
        this.value = value;
    }

    public final int getNumber() { return value; }

    public static $className$ readMessage (CodedInputStream input, CodeGenContext context, ConstructorFactory constructorFactory) throws IOException{
        return valueOf(input.readInt32());
    }

    protected static void writeMessage (CodedOutputStream output, CodeGenContext context, int fieldNumber, $className$ message) throws IOException{
        output.writeInt32(fieldNumber, message.value);
    }

    public boolean sameAs(Enum obj){
        if (obj == null) return false;
        return this.equals(obj);
    }
}