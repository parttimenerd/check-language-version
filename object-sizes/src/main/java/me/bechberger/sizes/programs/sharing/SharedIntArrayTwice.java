package me.bechberger.sizes.programs.sharing;

/* Same int[] referenced twice vs duplicated arrays (compare with DistinctIntArraysTwice). */
class SharedIntArrayTwice {
    Holder value = new Holder();

    static final class Holder {
        final int[] shared = new int[16];
        final int[] a = shared;
        final int[] b = shared;
    }
}