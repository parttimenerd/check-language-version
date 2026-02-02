// Tiny: Reflection looks like method ref (Java 1)
// Expected Version: 1
// Required Features: REFLECTION, CLASS_PROPERTY

import java.lang.reflect.*;

class Tiny_ReflectLooksModern_Java1 {
    Method m = String.class.getMethods()[0];
}