package me.bechberger.sizes.programs.simple;

/* One long field in an object (shows 8-byte field alignment). */
class OneLongField {

    Holder value = new Holder();

    static final class Holder {
        long l = 0L;
    }
}