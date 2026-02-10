package me.bechberger.sizes.programs.primitives;

/* long then int: compare with IntAndLong_Order1 to see padding differences. */
class IntAndLong_Order2 {
    Holder value = new Holder();

    static final class Holder {
        long l = 2;
        int i = 1;
    }
}