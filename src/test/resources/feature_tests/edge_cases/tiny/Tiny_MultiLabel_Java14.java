// Tiny: Multiple switch labels (Java 14)
// Expected Version: 14
// Required Features: SWITCH_MULTIPLE_LABELS

public class Tiny_MultiLabel_Java14 {
    void test(int x) {
        switch (x) {
            case 1, 2, 3 -> System.out.println("low");
            default -> System.out.println("high");
        }
    }
}