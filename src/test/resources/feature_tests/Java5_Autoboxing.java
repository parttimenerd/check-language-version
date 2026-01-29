// Java 5 feature: Autoboxing and unboxing
// Expected Version: 5
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, GENERICS
import java.util.ArrayList;
import java.util.List;

class Java5_Autoboxing {
    public void testAutoboxing() {
        // Autoboxing: primitive to wrapper automatically
        Integer boxedInt = 42;  // autoboxing: int -> Integer
        Double boxedDouble = 3.14;  // autoboxing: double -> Double
        Boolean boxedBool = true;  // autoboxing: boolean -> Boolean

        // Unboxing: wrapper to primitive automatically
        int primitiveInt = boxedInt;  // unboxing: Integer -> int
        double primitiveDouble = boxedDouble;  // unboxing: Double -> double
        boolean primitiveBool = boxedBool;  // unboxing: Boolean -> boolean

        // Autoboxing in collections
        List<Integer> numbers = new ArrayList<Integer>();
        numbers.add(1);  // autoboxing: int -> Integer
        numbers.add(2);
        numbers.add(3);

        // Unboxing from collections
        int first = numbers.get(0);  // unboxing: Integer -> int

        // Autoboxing in expressions
        Integer a = 10;
        Integer b = 20;
        int sum = a + b;  // unboxing for arithmetic, then autoboxing for assignment

        // Comparison with autoboxing (be careful with identity vs equality!)
        Integer x = 127;
        Integer y = 127;
        System.out.println("x == y: " + (x == y));  // true (cached)

        Integer p = 128;
        Integer q = 128;
        System.out.println("p == q: " + (p == q));  // false (not cached)
        System.out.println("p.equals(q): " + p.equals(q));  // true
    }
}