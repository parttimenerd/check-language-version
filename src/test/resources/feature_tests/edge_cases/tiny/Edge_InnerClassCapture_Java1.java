// Java 1.1 edge case: Inner class capturing outer variables
// Test: Testing inner classes that capture outer class instance variables
// Expected Version: 1
// Required Features: INNER_CLASSES
class Edge_InnerClassCapture_Java1 {
    private int x = 10;
    class Inner {
        void print() {
            System.out.println(x);
        }
    }
}