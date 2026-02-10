package me.bechberger.sizes.programs.sharing;

/* Two null references: no additional objects beyond the holder. */
class TwoNullRefs {
    Holder value = new Holder();

    static final class Holder {
        Object a = null;
        Object b = null;
    }
}