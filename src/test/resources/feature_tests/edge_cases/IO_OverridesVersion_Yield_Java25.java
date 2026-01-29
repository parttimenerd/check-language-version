// Edge case: Copy of Java14_Yield + unqualified IO usage in a compact source file.
// Expected Version: 25
// Required Features: SWITCH_EXPRESSIONS, YIELD, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS

class IO_OverridesVersion_Yield_Java25 {
    public int calculate(String operation, int a, int b) {
        return switch (operation) {
            case "add" -> a + b;
            case "subtract" -> a - b;
            case "multiply" -> {
                System.out.println("Multiplying " + a + " and " + b);
                yield a * b;
            }
            default -> {
                IO.println("Unknown operation");
                yield 0;
            }
        };
    }
}