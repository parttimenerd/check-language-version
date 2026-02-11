package me.bechberger.sizes.programs.primitives;

/* A single char field inside an object. */
class CharValue {
    Holder value = new Holder('a');

    record Holder(char value) {}
}