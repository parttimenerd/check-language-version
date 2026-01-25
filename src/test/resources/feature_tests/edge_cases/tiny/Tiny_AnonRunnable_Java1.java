// Tricky: Looks like lambda but it's anonymous inner class (Java 1.1)
// Expected Version: 1
// Required Features: INNER_CLASSES
public class Tiny_AnonRunnable_Java1 {
    Runnable r = new Runnable() {
        public void run() { System.out.println("Hi"); }
    };
}