package me.bechberger.sizes.programs.pools;

/* Two cached Integer references usually point to the same instance. */
class IntegerPoolSameInstance {
    Holder value = new Holder();

    static final class Holder {
        final Integer a = Integer.valueOf(42);
        final Integer b = Integer.valueOf(42);
    }
}