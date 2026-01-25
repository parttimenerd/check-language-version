// Java 7 combination: Binary literals + Underscores + String switch (Advanced)
// Test: Advanced combination of binary literals with underscores in string switch
// Expected Version: 7
// Required Features: BINARY_LITERALS, UNDERSCORES_IN_LITERALS, STRINGS_IN_SWITCH
public class Combo_BinaryUnderscoreStringSwitch_Java7_Advanced {
    void test(String s) {
        int binary = 0b1010_1010;
        int hex = 0xFF_FF_FF_FF;
        switch (s) {
            case "binary":
                System.out.println(binary);
                break;
            case "hex":
                System.out.println(hex);
                break;
        }
    }
}