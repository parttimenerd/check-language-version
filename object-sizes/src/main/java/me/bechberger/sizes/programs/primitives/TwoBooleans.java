package me.bechberger.sizes.programs.primitives;

/* Two boolean fields: demonstrates that booleans don't pack into bits in objects. */
class TwoBooleans {
    Holder value = new Holder();

    static final class Holder {
        boolean a = true;
        boolean b = false;
    }
}