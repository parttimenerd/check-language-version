package me.bechberger.sizes.programs.circular;

/* Object[] that contains a reference to itself (cycle via array). */
class ArraySelfReference {
    Object[] value;

    ArraySelfReference() {
        Object[] a = new Object[1];
        a[0] = a;
        value = a;
    }
}