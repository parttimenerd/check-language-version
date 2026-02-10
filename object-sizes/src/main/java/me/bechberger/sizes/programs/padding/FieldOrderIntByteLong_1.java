package me.bechberger.sizes.programs.padding;

/* Same fields as FieldOrderIntByteLong_2 but different order -> padding differences. */
class FieldOrderIntByteLong_1 {
    A value = new A();

    static final class A {
        int i;
        byte b;
        long l;
    }
}