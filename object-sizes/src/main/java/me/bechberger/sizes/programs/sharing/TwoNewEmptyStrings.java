package me.bechberger.sizes.programs.sharing;

/* Two distinct empty String instances created with new String(""). */
class TwoNewEmptyStrings {
    Holder value = new Holder();

    static final class Holder {
        final String a = new String("");
        final String b = new String("");
    }
}