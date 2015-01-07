package $packageName$;

import java.util.*;

public class ClassRegistry implements ConstructorFactory{
    private final static ClassRegistry instance = new ClassRegistry();

    public static ClassRegistry getInstance() {
        return instance;
    }

    private Map<String, Constructor> registry = new HashMap<String, Constructor>();
    $messageTypes: {messageType |
        public static class $messageType.name$Constructor implements Constructor{
            public GeneratedMessage newInstance(){
                return new $messageType.fullName$();
            \}
        \}
    }$

    private ClassRegistry () {
    $messageTypes: {messageType |
        registry.put("$messageType.fullName$", new $messageType.name$Constructor());
    }$
    \}

    public GeneratedMessage newInstance(String fullName){
        if (!registry.containsKey(fullName)){
            throw new MessageParsingException(new IllegalArgumentException(fullName+" is not registered"));
        }
        return registry.get(fullName).newInstance();
    }
}