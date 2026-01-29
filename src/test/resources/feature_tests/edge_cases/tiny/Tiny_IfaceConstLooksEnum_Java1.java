// Tiny: Interface constant looks like enum (Java 1)
// Expected Version: 1
// Required Features: INNER_CLASSES

class Tiny_IfaceConstLooksEnum_Java1 {
    interface Constants {
        int SMALL = 1;
        int MEDIUM = 2;
        int LARGE = 3;
    }
    int size = Constants.MEDIUM;
}