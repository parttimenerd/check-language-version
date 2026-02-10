package me.bechberger.sizes.programs.collections;

import java.util.ArrayList;

/** ArrayList with 10 elements: shows growth policy/capacity backing. */
class ArrayListSize10 {
    ArrayList<Integer> value = new ArrayList<>();

    ArrayListSize10() {
        for (int i = 0; i < 10; i++) {
            value.add(i);
        }
    }
}