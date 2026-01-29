// Tiny: Autoboxing looks like valueOf (Java 5)
// Expected Version: 5
// Required Features: AUTOBOXING, GENERICS, COLLECTIONS_FRAMEWORK

class Tiny_AutoboxLooksModern_Java5 {
    java.util.List<Integer> nums = new java.util.ArrayList<Integer>();
    void add() { nums.add(42); }
    int get() { return nums.get(0); }
}