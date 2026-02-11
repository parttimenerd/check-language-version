package me.bechberger.sizes.programs.primitives;

/* A single float field inside an object. */
class FloatValue {
    Holder value = new Holder(1.0f);

    record Holder(float value) {}
}