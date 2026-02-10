package me.bechberger.sizes.programs.primitives;

/* int then long: compare with IntAndLong_Order2 to see padding differences. */
class IntAndLong_Order1 {
    Holder value = new Holder();

    static final class Holder {
        int i = 1;
        long l = 2;
    }
}