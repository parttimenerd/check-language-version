// Required Features: ALPHA3_STRING_BUFFER_PRINT_STREAM, ALPHA3_STRING_INPUT_STREAM, ALPHA3_TEXT_INPUT_STREAM
import net.StringBufferPrintStream;
import net.StringInputStream;
import net.TextInputStream;

class Edge_Alpha3NetStreams {
    void test() {
        StringBufferPrintStream out = null;
        StringInputStream in = null;
        TextInputStream tin = null;
        System.out.println(out);
        System.out.println(in);
        System.out.println(tin);
    }
}