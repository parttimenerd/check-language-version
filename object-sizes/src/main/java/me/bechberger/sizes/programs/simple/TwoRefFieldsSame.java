package me.bechberger.sizes.programs.simple;

/* Two reference fields pointing to the same object. */
class TwoRefFieldsSame {
    Holder value = new Holder();

    static final class Holder {
        final Object shared = new Object();
        final Object a = shared;
        final Object b = shared;
    }
}