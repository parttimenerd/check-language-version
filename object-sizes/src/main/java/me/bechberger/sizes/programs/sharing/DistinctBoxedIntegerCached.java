package me.bechberger.sizes.programs.sharing;

/* Two distinct Integer instances with the same cached value forced via 'new'. */
class DistinctBoxedIntegerCached {
    Holder value = new Holder();

    static final class Holder {
        @SuppressWarnings("removal")
        final Integer a = new Integer(42);
        @SuppressWarnings("removal")
        final Integer b = new Integer(42);
    }
}