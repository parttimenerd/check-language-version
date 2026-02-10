package me.bechberger.sizes.programs.pools;

/* new Integer(42) forces allocation (no pooling). */
class IntegerNewAlwaysAllocates {
    @SuppressWarnings("removal")
    Integer value = new Integer(42);
}