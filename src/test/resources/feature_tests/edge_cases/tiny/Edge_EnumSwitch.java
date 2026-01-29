// Java 5 edge case: Enum in switch without other Java 5 features
// Test: Switch on enum (requires Java 5)
// Expected Version: 5
// Required Features: ENUMS
enum Edge_EnumSwitch { A }
class Tiny_EnumSwitch_Java5 {
    void test(Edge_EnumSwitch e) { switch(e) { case A: } }
}