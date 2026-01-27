// Required Features: ALPHA3_HEX_LITERALS
public class Edge_Alpha3HexLiterals {
    void test() {
        int a = 0X1;
        long b = 0x1L;
        long c = 0XFFL;
        int d = 0xFF; // should not trigger alpha3 feature by itself
        System.out.println(a + b + c + d);
    }
}