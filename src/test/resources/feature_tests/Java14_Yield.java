// Java 14 feature: Yield statement
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS, YIELD
public class Java14_Yield {
    public int calculate(String operation, int a, int b) {
        return switch (operation) {
            case "add" -> a + b;
            case "subtract" -> a - b;
            case "multiply" -> {
                System.out.println("Multiplying " + a + " and " + b);
                yield a * b;  // yield for block expressions
            }
            case "divide" -> {
                if (b == 0) {
                    yield 0;
                }
                yield a / b;
            }
            default -> {
                System.out.println("Unknown operation");
                yield 0;
            }
        };
    }
}