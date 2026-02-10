package me.bechberger.sizes.programs.simple;

/* Object with one reference field pointing to another object. */
class OneObjectReferenceField {
    Holder value = new Holder();

    static final class Holder {
        final Object ref = new Object();
    }
}