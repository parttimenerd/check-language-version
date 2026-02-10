package me.bechberger.sizes.programs.simple;

/* Two reference fields pointing to two different objects. */
class TwoRefFieldsDistinct {
    Holder value = new Holder();

    static final class Holder {
        final Object a = new Object();
        final Object b = new Object();
    }
}