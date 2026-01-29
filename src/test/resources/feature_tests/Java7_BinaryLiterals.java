// Java 7 feature: Binary integer literals
// Expected Version: 7
// Required Features: BINARY_LITERALS, UNDERSCORES_IN_LITERALS
class Java7_BinaryLiterals {
    public void method() {
        int binary = 0b1010;        // 10 in decimal
        int binary2 = 0B11110000;   // 240 in decimal
        long binaryLong = 0b1111_0000_1111_0000L;

        System.out.println("Binary 0b1010 = " + binary);
    }
}