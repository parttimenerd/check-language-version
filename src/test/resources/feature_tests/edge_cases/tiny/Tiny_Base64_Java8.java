// Test: Base64 API (Java 8)
// Expected Version: 8
// Required Features: BASE64_API
import java.util.Base64;
public class Tiny_Base64_Java8 {
    String encoded = Base64.getEncoder().encodeToString("hello".getBytes());
}