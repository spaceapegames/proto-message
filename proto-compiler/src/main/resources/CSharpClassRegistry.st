using System;
using System.Collections.Generic;

namespace $packageName$ {
    public class ClassRegistry : ConstructorFactory{
        private static ClassRegistry instance = new ClassRegistry();

        public static ClassRegistry getInstance() {
            return instance;
        }

        private Dictionary<String, Constructor> registry = new Dictionary<String, Constructor>();

        private ClassRegistry(){
        $messageTypes: {messageType |
            registry.Add("$messageType.fullName$", new $messageType.name$Constructor());
        }$
        \}

        public override GeneratedMessage newInstance(String fullName){
            if (!registry.ContainsKey(fullName)){
                 throw new Exception("not found "+fullName);
            }
            return registry[fullName].newInstance();
        }
    }

    $messageTypes: {messageType |
        class $messageType.name$Constructor : Constructor{
            public override GeneratedMessage newInstance(){
                return new $messageType.fullName$();
            \}
        \}
    }$
}