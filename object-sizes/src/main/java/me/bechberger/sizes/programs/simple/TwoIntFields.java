package me.bechberger.sizes.programs.simple;

/* Two int fields in one object (shows header + two fields + alignment). */
class TwoIntFields {
    Holder value = new Holder();

    static final class Holder {
        int a = 1;
        int b = 2;
    }
}