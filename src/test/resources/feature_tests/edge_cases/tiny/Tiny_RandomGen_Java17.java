// Tiny: Random generator (Java 17)
// Expected Version: 17
// Required Features: RANDOM_GENERATOR

import java.util.random.*;

class Tiny_RandomGen_Java17 {
    void test() {
        RandomGenerator rg = RandomGenerator.of("L64X128MixRandom");
    }
}