package me.bechberger.sizes.programs.sharing;

/* Two fields reference two distinct String instances with the same content (no sharing). */
class StringSharedDistinctInstances {
    Holder value = new Holder();

    static final class Holder {
        final String a = new String("abcdef");
        final String b = new String("abcdef");
    }
}