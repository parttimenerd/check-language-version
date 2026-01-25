// Tiny: Method ref in annotation (Java 8)
// Expected Version: 8
// Required Features: METHOD_REFERENCES

import java.util.function.*;

public class Tiny_MethodRefField_Java8 {
    Function<String,Integer> f = Integer::parseInt;
}