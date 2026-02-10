package me.bechberger.sizes.programs.pools;

/* Integer.valueOf(1000) typically returns a new Integer (outside the default cache range). */
class IntegerPoolNotCached {
    Integer value = Integer.valueOf(1000);
}