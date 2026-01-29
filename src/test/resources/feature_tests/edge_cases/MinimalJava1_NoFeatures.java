// Edge case: Minimal Java 1.0 code with no special features
// Expected Version: -1
// Required Features: ALPHA3_ARRAY_SYNTAX
class MinimalJava1_NoFeatures {

    private int value;

    public MinimalJava1_NoFeatures(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void calculate(int x) {
        int result = 0;
        if (x > 0) {
            result = x * 2;
        }
        for (int i = 0; i < 10; i++) {
            result = result + i;
        }
    }

    public static void main(String[] args) {
        MinimalJava1_NoFeatures obj = new MinimalJava1_NoFeatures(42);
        obj.calculate(10);
    }
}