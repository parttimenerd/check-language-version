// Tiny: Stack walking (Java 9)
// Expected Version: 9
// Required Features: LAMBDAS, STACK_WALKING

class Tiny_StackWalk_Java9 {
    void test() {
        StackWalker sw = StackWalker.getInstance();
        sw.forEach(f -> System.out.println(f));
    }
}