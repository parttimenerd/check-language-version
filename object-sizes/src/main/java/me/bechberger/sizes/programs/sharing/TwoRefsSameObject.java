package me.bechberger.sizes.programs.sharing;

/** Two references pointing to the same payload object: graph sharing avoids duplication. */
class TwoRefsSameObject {
    Holder value = new Holder();

    static final class Holder {
        final byte[] shared = new byte[16];
        final byte[] a = shared;
        final byte[] b = shared;
    }
}