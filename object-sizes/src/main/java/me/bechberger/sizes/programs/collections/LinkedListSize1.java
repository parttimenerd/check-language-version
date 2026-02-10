package me.bechberger.sizes.programs.collections;

import java.util.LinkedList;

/** LinkedList with 1 element: allocates one Node (+ boxed Integer). */
class LinkedListSize1 {
    LinkedList<Integer> value = new LinkedList<>();

    LinkedListSize1() {
        value.add(0);
    }
}