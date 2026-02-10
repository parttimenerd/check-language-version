package me.bechberger.sizes.programs.strings;

/* Object that contains a null String field (root object is non-null). */
class NullStringField {
    Holder value = new Holder();

    static final class Holder {
        String s = null;
    }
}