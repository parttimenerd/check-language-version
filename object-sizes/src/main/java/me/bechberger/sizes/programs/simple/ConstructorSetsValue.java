package me.bechberger.sizes.programs.simple;

/*
 * Regression target: value is assigned in the constructor.
 * If the runner reads value before calling the constructor, it will see null.
 */
class ConstructorSetsValue {
    Object value;

    ConstructorSetsValue() {
        value = new Object();
    }
}