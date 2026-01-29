// Java 9 feature: Diamond operator with anonymous classes
// Expected Version: 9
// Required Features: ANNOTATIONS, CONCURRENT_API, DIAMOND_OPERATOR, DIAMOND_WITH_ANONYMOUS, GENERICS, INNER_CLASSES
import java.util.Comparator;
import java.util.concurrent.Callable;

class Java9_DiamondWithAnonymous {
    public void testDiamondWithAnonymous() {
        // Before Java 9: had to specify type arguments for anonymous classes
        // Comparator<String> comp = new Comparator<String>() { ... };

        // Java 9: Diamond operator works with anonymous classes
        Comparator<String> comparator = new Comparator<>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.length() - s2.length();
            }
        };

        // Another example with Callable
        Callable<String> callable = new Callable<>() {
            @Override
            public String call() {
                return "Hello from anonymous class with diamond!";
            }
        };

        System.out.println("Comparator: " + comparator.compare("abc", "defgh"));
    }
}