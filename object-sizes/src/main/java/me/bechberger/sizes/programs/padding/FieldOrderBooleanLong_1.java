package me.bechberger.sizes.programs.padding;

/** Same fields as FieldOrderBooleanLong_2 but different order -> padding differences. */
class FieldOrderBooleanLong_1 {
    A value = new A();

    static final class A {
        boolean b;
        long l;
    }
}