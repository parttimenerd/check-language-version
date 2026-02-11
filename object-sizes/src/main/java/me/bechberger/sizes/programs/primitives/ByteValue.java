package me.bechberger.sizes.programs.primitives;

/* A single byte field inside an object. */
class ByteValue {
    Holder value = new Holder((byte)1);

    record Holder(byte value) {}
}