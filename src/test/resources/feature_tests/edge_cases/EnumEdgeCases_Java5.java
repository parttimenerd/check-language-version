// Edge case: Enum features across Java versions (uses diamond operator = Java 7)
// Expected Version: 7
// Required Features: ALPHA3_ARRAY_SYNTAX, ANNOTATIONS, DIAMOND_OPERATOR, ENUMS, FOR_EACH, GENERICS, INNER_CLASSES, REFLECTION
import java.util.*;

class EnumEdgeCases_Java5 {

    // Java 5: Basic enum
    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    // Java 5: Enum with fields and constructor
    enum Planet {
        MERCURY(3.303e+23, 2.4397e6),
        VENUS(4.869e+24, 6.0518e6),
        EARTH(5.976e+24, 6.37814e6);

        private final double mass;
        private final double radius;

        Planet(double mass, double radius) {
            this.mass = mass;
            this.radius = radius;
        }

        public double surfaceGravity() {
            return 6.67300E-11 * mass / (radius * radius);
        }
    }

    // Java 5: Enum with abstract method
    enum Operation {
        PLUS {
            public double apply(double x, double y) { return x + y; }
        },
        MINUS {
            public double apply(double x, double y) { return x - y; }
        },
        TIMES {
            public double apply(double x, double y) { return x * y; }
        },
        DIVIDE {
            public double apply(double x, double y) { return x / y; }
        };

        public abstract double apply(double x, double y);
    }

    // Java 5: Enum with interface
    interface Describable {
        String describe();
    }

    enum Color implements Describable {
        RED("The color of fire"),
        GREEN("The color of nature"),
        BLUE("The color of sky");

        private final String description;

        Color(String description) {
            this.description = description;
        }

        @Override
        public String describe() {
            return description;
        }
    }

    // Java 5: EnumSet and EnumMap
    public void testEnumCollections() {
        EnumSet<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
        EnumSet<Day> weekdays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
        EnumSet<Day> allDays = EnumSet.allOf(Day.class);

        EnumMap<Day, String> dayNames = new EnumMap<>(Day.class);
        dayNames.put(Day.MONDAY, "Mon");
        dayNames.put(Day.TUESDAY, "Tue");
    }

    // Java 5: Enum in switch
    public void testEnumSwitch(Day day) {
        switch (day) {
            case MONDAY:
                System.out.println("Start of week");
                break;
            case FRIDAY:
                System.out.println("End of work week");
                break;
            case SATURDAY:
            case SUNDAY:
                System.out.println("Weekend!");
                break;
            default:
                System.out.println("Midweek");
        }
    }

    // Java 5: Enum methods
    public void testEnumMethods() {
        // name() and ordinal()
        String name = Day.MONDAY.name();
        int ordinal = Day.MONDAY.ordinal();

        // valueOf()
        Day day = Day.valueOf("MONDAY");

        // values()
        Day[] allDays = Day.values();
        for (Day d : allDays) {
            System.out.println(d);
        }

        // compareTo()
        int cmp = Day.MONDAY.compareTo(Day.FRIDAY);
    }
}