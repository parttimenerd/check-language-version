// Tiny: Deserialization filters (Java 9)
// Expected Version: 9
// Required Features: DESERIALIZATION_FILTERS, IO_API

import java.io.*;

class Tiny_DeserFilter_Java9 {
    void test() {
        ObjectInputFilter f = ObjectInputFilter.Config.getSerialFilter();
    }
}