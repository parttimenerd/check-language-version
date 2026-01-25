// Edge case: Inner class variations
// Expected Version: 1
// Required Features: INNER_CLASSES
public class InnerClassEdgeCases_Java1 {

    private String outerField = "outer";

    class InnerMember {
        public void method() {
            System.out.println(outerField);
        }
    }

    static class StaticNested {
        public void method() {
            System.out.println("Static nested");
        }
    }

    public void testLocalClass() {
        class LocalClass {
            public void method() {
                System.out.println("Local class");
            }
        }
        new LocalClass().method();
    }

    public void testAnonymousClass() {
        Runnable r = new Runnable() {
            public void run() {
                System.out.println("Anonymous");
            }
        };
    }
}