package me.bechberger.sizes.programs.simple;

/** An object holding one reference to another object: shows reference field cost + graph expansion. */
class OneReference {
    Holder value = new Holder();

    static final class Holder {
        final Object ref = new Object();
    }
}