// Test: Modules (Java 9)
// Expected Version: 9
// Required Features: MODULES
module test.module {
    exports test.module.api;
    requires java.base;
}