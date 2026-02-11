package me.bechberger.sizes.programs.primitives;

/* A single boolean field inside an object (shows padding/alignment effects). */
class BooleanValue {
    Holder value = new Holder(true);

    record Holder(boolean value) {}
}