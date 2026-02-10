package me.bechberger.sizes.programs.sharing;

/* Two fields reference the same String instance (sharing reduces graph size). */
class StringSharedSameInstance {
    Holder value = new Holder();

    static final class Holder {
        final String s = new String("abcdef");
        final String a = s;
        final String b = s;
    }
}