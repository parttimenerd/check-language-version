package me.bechberger.sizes.programs.arrays;

/** Empty object array still has header/class pointer and length field. */
class ObjectArrayLen0 {
    Object[] value = new Object[0];
}