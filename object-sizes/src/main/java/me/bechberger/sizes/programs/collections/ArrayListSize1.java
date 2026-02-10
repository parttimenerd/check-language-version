package me.bechberger.sizes.programs.collections;

import java.util.ArrayList;

/** ArrayList with 1 element: shows backing array allocation. */
class ArrayListSize1 {
    ArrayList<Integer> value = new ArrayList<>();

    ArrayListSize1() {
        value.add(0);
    }
}