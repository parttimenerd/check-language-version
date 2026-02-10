package me.bechberger.sizes.programs.padding;

/** Same fields as FieldOrderByteLong_2 but different order -> padding differences. */
class FieldOrderByteLong_1 {
    A value = new A();

    static final class A {
        byte b;
        long l;
    }
}