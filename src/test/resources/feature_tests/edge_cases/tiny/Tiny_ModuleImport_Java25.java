// Tiny: Module imports (Java 25)
// Expected Version: 25
// Required Features: COLLECTION_FACTORY_METHODS, GENERICS, MODULE_IMPORTS

import module java.base;

class Tiny_ModuleImport_Java25 {
    void test() {
        List<String> list = List.of("a");
    }
}