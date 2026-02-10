package me.bechberger.sizes.programs.sharing;

/* Two fields refer to the same interned empty string literal. */
class SharedEmptyStringLiteral {
    Holder value = new Holder();

    static final class Holder {
        final String a = "";
        final String b = "";
    }
}