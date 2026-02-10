package me.bechberger.sizes.programs.pools;

/* For values outside the cache range, valueOf typically allocates distinct instances. */
class IntegerPoolOutsideCacheDistinct {
    Holder value = new Holder();

    static final class Holder {
        final Integer a = Integer.valueOf(1000);
        final Integer b = Integer.valueOf(1000);
    }
}