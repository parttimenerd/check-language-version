package me.bechberger.sizes.programs.pools;

/* Two autoboxed Integers for 1000 (typically outside cache) -> often distinct instances. */
class AutoboxNotCached1000_TwoRefs {
    Holder value = new Holder();

    static final class Holder {
        final Integer a = 1000;
        final Integer b = 1000;
    }
}