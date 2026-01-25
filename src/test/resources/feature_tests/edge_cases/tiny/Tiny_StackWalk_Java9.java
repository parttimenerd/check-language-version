// Tiny: Stack walking (Java 9)
// Expected Version: 9
// Required Features: STACK_WALKING

public class Tiny_StackWalk_Java9 {
    void test() {
        StackWalker sw = StackWalker.getInstance();
        sw.forEach(f -> System.out.println(f));
    }
}