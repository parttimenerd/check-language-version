// Java 5 edge case: Enum that looks like it could be sealed
// Test: Enum with fixed set of values (predates sealed)
// Expected Version: 5
// Required Features: ENUMS
enum Edge_EnumLooksSealed { PENDING, RUNNING, DONE }
public class Tiny_EnumLooksSealed_Java5 {
    Edge_EnumLooksSealed state = Edge_EnumLooksSealed.PENDING;
}