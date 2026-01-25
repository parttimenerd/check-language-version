// Java 7 feature: Underscores in numeric literals
// Expected Version: 7
// Required Features: UNDERSCORES_IN_LITERALS
public class Java7_UnderscoresInLiterals {
    public void method() {
        int million = 1_000_000;
        long creditCard = 1234_5678_9012_3456L;
        int hex = 0xFF_EC_DE_5E;

        System.out.println("Million: " + million);
    }
}