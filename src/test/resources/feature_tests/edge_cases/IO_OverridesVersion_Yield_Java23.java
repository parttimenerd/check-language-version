// Edge case: Java 14 yield + explicit import of java.io.IO (IO introduced in Java 23)
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS, YIELD

class IO_OverridesVersion_Yield_Java23 {
    public int calculate(String operation, int a, int b) {
        return switch (operation) {
            case "add" -> a + b;
            case "subtract" -> a - b;
            case "multiply" -> {
                System.out.println("Multiplying " + a + " and " + b);
                yield a * b;
            }
            case "divide" -> {
                if (b == 0) {
                    yield 0;
                }
                yield a / b;
            }
            default -> {
                yield 0;
            }
        };
    }
}