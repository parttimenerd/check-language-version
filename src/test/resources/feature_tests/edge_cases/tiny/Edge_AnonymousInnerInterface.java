// Java 1.1 edge case: Anonymous inner class implementing interface
// Test: Testing anonymous inner classes that implement interfaces
// Expected Version: 1
// Required Features: INNER_CLASSES
public class Edge_AnonymousInnerInterface_Java1 {
    Runnable r = new Runnable() {
        public void run() {
            System.out.println("running");
        }
    };
}