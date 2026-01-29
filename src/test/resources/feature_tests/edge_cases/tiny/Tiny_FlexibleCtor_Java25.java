// Tiny: Flexible constructor bodies (Java 25)
// Expected Version: 25
// Required Features: FLEXIBLE_CONSTRUCTOR_BODIES

class Tiny_FlexibleCtor_Java25 {
    int x;
    Tiny_FlexibleCtor_Java25(int x) {
        if (x < 0) x = 0;
        this.x = x;
    }
}

class Sub extends Tiny_FlexibleCtor_Java25 {
    Sub(int x) {
        int v = Math.abs(x);
        super(v);
    }
}