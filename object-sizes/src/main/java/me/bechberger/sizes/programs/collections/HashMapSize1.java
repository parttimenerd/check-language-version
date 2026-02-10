package me.bechberger.sizes.programs.collections;

import java.util.HashMap;

/** HashMap with 1 entry: shows table + node overhead. */
class HashMapSize1 {
    HashMap<Integer, Integer> value = new HashMap<>();

    HashMapSize1() {
        value.put(0, 0);
    }
}