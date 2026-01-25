// Tiny: Deserialization filters (Java 17)
// Expected Version: 17
// Required Features: DESERIALIZATION_FILTERS

import java.io.*;

public class Tiny_DeserFilter_Java17 {
    void test() {
        ObjectInputFilter f = ObjectInputFilter.Config.getSerialFilter();
    }
}