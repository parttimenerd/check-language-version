// Tricky: Inner class with Collections (not generics!)
// Expected Version: 2
// Required Features: COLLECTIONS_FRAMEWORK, INNER_CLASSES
import java.util.HashMap;

class Tiny_MapInner_Java2 {
    class Entry { HashMap map = new HashMap(); }
}