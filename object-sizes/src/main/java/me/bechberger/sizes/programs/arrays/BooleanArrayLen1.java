package me.bechberger.sizes.programs.arrays;

/** Boolean array is byte-backed but still has array header + alignment (len=1). */
class BooleanArrayLen1 {
    boolean[] value = new boolean[1];
}