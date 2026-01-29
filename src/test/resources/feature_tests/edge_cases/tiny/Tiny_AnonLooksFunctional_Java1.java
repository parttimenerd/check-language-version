// Tiny: Anonymous class looks like functional (Java 1)
// Expected Version: 1
// Required Features: INNER_CLASSES

import java.util.*;

class Tiny_AnonLooksFunctional_Java1 {
    Comparator c = new Comparator() {
        public int compare(Object a, Object b) { return 0; }
    };
}