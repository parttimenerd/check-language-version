package me.bechberger.sizes.programs.primitives;

/** Demonstrates that primitives inside an object still include header + alignment. */
class IntValue {
    Holder value = new Holder(42);

    record Holder(int value) {}
}