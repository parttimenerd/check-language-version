// Java 1.1 edge case: Local class capturing variables
// Test: Testing local classes that capture local variables and method parameters
// Expected Version: 1
// Required Features: INNER_CLASSES
class Edge_LocalClassCapture {
    void test(final int param) {
        final int local = 10;

        class LocalClass {
            void print() {
                System.out.println(param + local);
            }
        }

        new LocalClass().print();
    }
}