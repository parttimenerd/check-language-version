// Java 7 combination: Binary literals + Underscores + String switch + Multi-catch
// Test: Combination of binary literals, underscores in literals, strings in switch, and multi-catch
// Expected Version: 7
// Required Features: BINARY_LITERALS, UNDERSCORES_IN_LITERALS, STRINGS_IN_SWITCH, MULTI_CATCH
class Combo_BinaryUnderscoreStringSwitchCatch {
    void test(String s) {
        int binary = 0b1010_1010;
        int hex = 0xFF_FF_FF_FF;
        try {
            switch (s) {
                case "binary": System.out.println(binary); break;
                case "hex": System.out.println(hex); break;
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}