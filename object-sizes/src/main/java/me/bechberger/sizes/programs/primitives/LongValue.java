package me.bechberger.sizes.programs.primitives;

/** Demonstrates long field alignment effects compared to smaller primitives. */
class LongValue {
    Holder value = new Holder(42L);

    record Holder(long value) {}
}