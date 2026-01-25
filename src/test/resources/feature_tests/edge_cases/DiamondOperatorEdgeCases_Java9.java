// Edge case: Diamond operator edge cases
// Expected Version: 9
// Required Features: DIAMOND_OPERATOR, DIAMOND_WITH_ANONYMOUS
import java.util.*;
import java.util.function.*;

public class DiamondOperatorEdgeCases_Java9 {

    // Java 7: Basic diamond operator
    public void testBasicDiamond() {
        List<String> list = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        Set<Double> set = new HashSet<>();
    }

    // Java 7: Nested generics with diamond
    public void testNestedDiamond() {
        Map<String, List<Integer>> nested = new HashMap<>();
        List<Map<String, Set<Double>>> complex = new ArrayList<>();
    }

    // Java 7: Diamond with explicit type (not using diamond)
    public void testExplicitType() {
        List<String> explicit = new ArrayList<String>();  // pre-Java 7 style
        Map<String, Integer> explicitMap = new HashMap<String, Integer>();
    }

    // Java 9: Diamond with anonymous class
    public void testDiamondWithAnonymous() {
        // Java 9 allows diamond with anonymous classes
        Comparator<String> cmp = new Comparator<>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.length() - s2.length();
            }
        };

        // Another example
        List<String> customList = new ArrayList<>() {
            @Override
            public boolean add(String s) {
                System.out.println("Adding: " + s);
                return super.add(s);
            }
        };

        // With interface
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Running");
            }
        };
    }

    // Java 7: Diamond in method calls
    public void testDiamondInMethodCall() {
        processMap(new HashMap<>());
        processList(new ArrayList<>());
    }

    private void processMap(Map<String, Integer> map) {}
    private void processList(List<String> list) {}

    // Java 7: Diamond with constructor type inference
    public void testConstructorTypeInference() {
        // Type inferred from variable declaration
        List<String> strings = new ArrayList<>();

        // Type inferred from method parameter
        printList(new ArrayList<>());
    }

    private <T> void printList(List<T> list) {
        list.forEach(System.out::println);
    }

    // Java 7: Diamond with bounded type parameters
    public void testBoundedDiamond() {
        List<? extends Number> numbers = new ArrayList<>();
        List<? super Integer> integers = new ArrayList<>();
    }

    // Edge case: Diamond vs raw type
    public void testDiamondVsRaw() {
        // Diamond - type-safe
        List<String> diamond = new ArrayList<>();

        // Raw type - NOT type-safe (pre-Java 5 style)
        @SuppressWarnings("rawtypes")
        List raw = new ArrayList();  // No diamond, no type parameter
    }
}