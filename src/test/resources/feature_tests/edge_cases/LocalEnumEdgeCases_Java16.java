// Java 16 feature: Complex local enums with various edge cases
// Expected Version: 16
// Required Features: LOCAL_ENUMS

public class LocalEnumEdgeCases_Java16 {

    // Test 1: Local enum with methods and fields
    public void testLocalEnumWithMethods() {
        enum Color {
            RED("#FF0000"),
            GREEN("#00FF00"),
            BLUE("#0000FF");

            private final String hexCode;

            Color(String hexCode) {
                this.hexCode = hexCode;
            }

            public String getHexCode() {
                return hexCode;
            }

            @Override
            public String toString() {
                return name() + ": " + hexCode;
            }
        }

        Color c = Color.RED;
        System.out.println(c.getHexCode());
    }

    // Test 2: Local enum implementing interface
    public void testLocalEnumWithInterface() {
        interface Describable {
            String describe();
        }

        enum Priority implements Describable {
            LOW {
                @Override
                public String describe() {
                    return "Low priority task";
                }
            },
            MEDIUM {
                @Override
                public String describe() {
                    return "Medium priority task";
                }
            },
            HIGH {
                @Override
                public String describe() {
                    return "High priority task";
                }
            };

            // Default implementation (can be overridden)
            @Override
            public String describe() {
                return "Priority: " + name();
            }
        }

        Priority p = Priority.HIGH;
        System.out.println(p.describe());
    }

    // Test 3: Local enum with abstract method
    public void testLocalEnumWithAbstractMethod() {
        enum Operation {
            ADD {
                @Override
                int apply(int a, int b) { return a + b; }
            },
            SUBTRACT {
                @Override
                int apply(int a, int b) { return a - b; }
            },
            MULTIPLY {
                @Override
                int apply(int a, int b) { return a * b; }
            },
            DIVIDE {
                @Override
                int apply(int a, int b) { return a / b; }
            };

            abstract int apply(int a, int b);
        }

        int result = Operation.ADD.apply(5, 3);
        System.out.println(result);
    }

    // Test 4: Nested local enums (enum inside lambda or inner class - though this is a stretch)
    public void testEnumInLambdaContext() {
        // Local enum before lambda
        enum State { START, RUNNING, STOPPED }

        Runnable r = () -> {
            State s = State.RUNNING;
            System.out.println(s);
        };
        r.run();
    }

    // Test 5: Local enum with static initializer
    public void testLocalEnumWithStaticBlock() {
        enum Config {
            DEBUG, RELEASE, TEST;

            static final String VERSION = "1.0";
            static {
                System.out.println("Config enum loaded");
            }
        }

        System.out.println(Config.DEBUG);
        System.out.println(Config.VERSION);
    }

    // Test 6: Local enum used in switch expression
    public String testLocalEnumInSwitchExpression() {
        enum Status { PENDING, APPROVED, REJECTED }

        Status status = Status.APPROVED;
        return switch (status) {
            case PENDING -> "Waiting for review";
            case APPROVED -> "Request approved";
            case REJECTED -> "Request denied";
        };
    }

    // Test 7: Multiple local enums in same method
    public void testMultipleLocalEnums() {
        enum First { A, B, C }
        enum Second { X, Y, Z }
        enum Third { ONE, TWO, THREE }

        First f = First.A;
        Second s = Second.X;
        Third t = Third.ONE;

        System.out.println(f + " " + s + " " + t);
    }

    // Test 8: Local enum with complex constructor
    public void testComplexConstructor() {
        enum Planet {
            MERCURY(3.303e+23, 2.4397e6),
            VENUS(4.869e+24, 6.0518e6),
            EARTH(5.976e+24, 6.37814e6);

            private final double mass;   // in kilograms
            private final double radius; // in meters

            Planet(double mass, double radius) {
                this.mass = mass;
                this.radius = radius;
            }

            // Universal gravitational constant
            private static final double G = 6.67300E-11;

            double surfaceGravity() {
                return G * mass / (radius * radius);
            }

            double surfaceWeight(double otherMass) {
                return otherMass * surfaceGravity();
            }
        }

        double earthWeight = 175;
        double mass = earthWeight / Planet.EARTH.surfaceGravity();

        for (Planet p : Planet.values()) {
            System.out.printf("Your weight on %s is %f%n", p, p.surfaceWeight(mass));
        }
    }

    // Test 9: Local enum with generics usage
    public void testEnumWithGenerics() {
        enum Container {
            SMALL, MEDIUM, LARGE;

            <T> java.util.List<T> createList() {
                return new java.util.ArrayList<>();
            }
        }

        java.util.List<String> list = Container.SMALL.createList();
        list.add("test");
    }

    // Test 10: Deeply nested method with local enum
    public void testDeeplyNestedLocalEnum() {
        if (true) {
            for (int i = 0; i < 1; i++) {
                try {
                    enum DeepEnum { DEEP_A, DEEP_B }
                    DeepEnum de = DeepEnum.DEEP_A;
                    System.out.println(de);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}