// Tiny: Autoboxing looks like valueOf (Java 5)
// Expected Version: 5
// Required Features: AUTOBOXING

import java.util.*;

public class Tiny_AutoboxLooksModern_Java5 {
    List<Integer> nums = new ArrayList<Integer>();
    void add() { nums.add(42); }
    int get() { return nums.get(0); }
}