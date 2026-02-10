package me.bechberger.sizes.programs.collections;

import java.util.LinkedList;

/** LinkedList with 10 elements: shows per-node overhead clearly. */
class LinkedListSize10 {
    LinkedList<Integer> value = new LinkedList<>();

    LinkedListSize10() {
        for (int i = 0; i < 10; i++) {
            value.add(i);
        }
    }
}