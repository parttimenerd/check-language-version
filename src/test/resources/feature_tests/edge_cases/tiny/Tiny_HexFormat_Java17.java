// Test: HexFormat (Java 17)
// Expected Version: 17
// Required Features: HEX_FORMAT
import java.util.HexFormat;
class Tiny_HexFormat_Java17 {
    String hex = HexFormat.of().formatHex(new byte[]{1,2,3});
}