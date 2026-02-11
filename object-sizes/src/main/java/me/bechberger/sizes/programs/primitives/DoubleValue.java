package me.bechberger.sizes.programs.primitives;

/* A single double field inside an object (alignment similar to long). */
class DoubleValue {
    Holder value = new Holder(1.0);

    record Holder(double value) {}
}