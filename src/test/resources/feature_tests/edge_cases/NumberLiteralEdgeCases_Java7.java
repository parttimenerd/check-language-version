// Edge case: Binary literals and underscores in numeric literals
// Expected Version: 7
// Required Features: BINARY_LITERALS, UNDERSCORES_IN_LITERALS
public class NumberLiteralEdgeCases_Java7 {

    // Java 7: Binary literals
    public void testBinaryLiterals() {
        int binary1 = 0b1010;          // 10 in decimal
        int binary2 = 0B1111_0000;     // uppercase B also valid
        long binaryLong = 0b1010_1010_1010_1010L;

        // Byte range binary
        byte flags = 0b0011_1100;

        // Binary for bit masks
        int readPermission = 0b100;
        int writePermission = 0b010;
        int executePermission = 0b001;
        int allPermissions = 0b111;
    }

    // Java 7: Underscores in numeric literals
    public void testUnderscoresInLiterals() {
        // Decimal with underscores
        int million = 1_000_000;
        long billion = 1_000_000_000L;

        // Credit card number format
        long creditCard = 1234_5678_9012_3456L;

        // Phone number format
        long phone = 555_123_4567L;

        // Bytes in groups
        int bytes = 0xFF_EC_DE_5E;

        // Floating point
        float pi = 3.14_15_92f;
        double avogadro = 6.022_140_76e23;

        // Hexadecimal with underscores
        int hex = 0xFF_FF_FF_FF;
        long hexLong = 0x7FFF_FFFF_FFFF_FFFFL;

        // Binary with underscores (combining both Java 7 features)
        int binaryWithUnderscores = 0b1111_0000_1111_0000;

        // Octal with underscores
        int octal = 0177_777;
    }

    // Edge case: Where underscores are NOT allowed
    public void testUnderscoreRestrictions() {
        // NOT allowed at beginning or end of number
        // int invalid1 = _1000;  // Error
        // int invalid2 = 1000_;  // Error

        // NOT allowed adjacent to decimal point
        // float invalid3 = 3_.14f;  // Error
        // float invalid4 = 3._14f;  // Error

        // NOT allowed adjacent to suffix
        // long invalid5 = 1000_L;  // Error

        // NOT allowed adjacent to prefix
        // int invalid6 = 0_x1234;  // Error
        // int invalid7 = 0x_1234;  // Error

        // Valid: multiple underscores
        int multiUnderscore = 1___000___000;
    }

    // Combining with other features
    public void testCombined() {
        // Binary with underscores in switch
        int value = 0b1010_1010;
        switch (value) {
            case 0b0000_0000:
                System.out.println("Zero");
                break;
            case 0b1111_1111:
                System.out.println("All ones");
                break;
            default:
                System.out.println("Mixed: " + value);
        }
    }
}