package me.bechberger.sizes.programs.pools;

/* Integer.valueOf(42) returns a cached Integer instance (from the Integer cache / pool). */
class IntegerPoolCached {
    Integer value = Integer.valueOf(42);
}