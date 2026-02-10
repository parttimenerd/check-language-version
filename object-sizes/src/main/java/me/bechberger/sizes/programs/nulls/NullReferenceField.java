package me.bechberger.sizes.programs.nulls;

/** Object with a null reference field. */
class NullReferenceField {
    Holder value = new Holder();

    static final class Holder {
        Object ref = null;
    }
}