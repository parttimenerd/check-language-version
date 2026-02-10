package me.bechberger.sizes.programs.simple;

/* A non-null object with a single null reference field. */
class OneNullReference {
    Holder value = new Holder();

    static final class Holder {
        Object ref = null;
    }
}