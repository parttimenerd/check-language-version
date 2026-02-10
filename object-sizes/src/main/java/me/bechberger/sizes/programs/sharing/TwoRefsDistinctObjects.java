package me.bechberger.sizes.programs.sharing;

/** Two references pointing to two different but same-shaped payload objects: graph duplicates. */
class TwoRefsDistinctObjects {
    Holder value = new Holder();

    static final class Holder {
        final byte[] a = new byte[16];
        final byte[] b = new byte[16];
    }
}