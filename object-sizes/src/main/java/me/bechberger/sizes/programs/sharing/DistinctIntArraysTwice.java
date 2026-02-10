package me.bechberger.sizes.programs.sharing;

/* Two distinct int[] instances (compare with SharedIntArrayTwice). */
class DistinctIntArraysTwice {
    Holder value = new Holder();

    static final class Holder {
        final int[] a = new int[16];
        final int[] b = new int[16];
    }
}