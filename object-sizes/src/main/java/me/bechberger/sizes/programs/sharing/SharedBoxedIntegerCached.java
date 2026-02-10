package me.bechberger.sizes.programs.sharing;

/* Two references to the same cached Integer (via Integer.valueOf within cache range). */
class SharedBoxedIntegerCached {
    Holder value = new Holder();

    static final class Holder {
        final Integer shared = Integer.valueOf(42);
        final Integer a = shared;
        final Integer b = shared;
    }
}