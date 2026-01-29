// Java 17 feature: HexFormat API
// Expected Version: 17
// Required Features: ALPHA3_ARRAY_SYNTAX, HEX_FORMAT
import java.util.HexFormat;

class Java17_HexFormat {
    public void testHexFormat() {
        // Default HexFormat
        HexFormat hex = HexFormat.of();

        // Convert bytes to hex string
        byte[] bytes = {0x01, 0x02, 0x0A, 0x0F};
        String hexString = hex.formatHex(bytes);
        System.out.println("Hex: " + hexString);  // "01020a0f"

        // Parse hex string to bytes
        byte[] parsed = hex.parseHex("0102030405");
        System.out.println("Parsed length: " + parsed.length);  // 5

        // Customized HexFormat with delimiter
        HexFormat hexWithDelimiter = HexFormat.ofDelimiter(":");
        String formatted = hexWithDelimiter.formatHex(bytes);
        System.out.println("With delimiter: " + formatted);  // "01:02:0a:0f"

        // Uppercase hex format
        HexFormat upperHex = HexFormat.of().withUpperCase();
        System.out.println("Uppercase: " + upperHex.formatHex(bytes));  // "01020A0F"

        // Convert single int to hex
        String singleHex = hex.toHexDigits(255);
        System.out.println("255 in hex: " + singleHex);  // "ff"
    }
}