package me.bechberger.sizes.programs.padding;

/* Same fields as FieldOrderByteBooleanIntLong_2 but different order -> padding differences. */
class FieldOrderByteBooleanIntLong_1 {
    A value = new A();

    static final class A {
        byte b;
        boolean flag;
        int i;
        long l;
    }
}