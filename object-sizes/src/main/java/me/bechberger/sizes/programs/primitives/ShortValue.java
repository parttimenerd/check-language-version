package me.bechberger.sizes.programs.primitives;

/* A single short field inside an object. */
class ShortValue {
    Holder value = new Holder((short) 42);

    record Holder(short value) {}
}