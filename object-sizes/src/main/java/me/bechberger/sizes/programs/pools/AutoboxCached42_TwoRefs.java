package me.bechberger.sizes.programs.pools;

/* Two autoboxed Integers for 42 (typically cached) -> often same instance. */
class AutoboxCached42_TwoRefs {
    Holder value = new Holder();

    static final class Holder {
        final Integer a = 42;
        final Integer b = 42;
    }
}