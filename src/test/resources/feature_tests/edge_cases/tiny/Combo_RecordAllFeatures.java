// Java 16 combination: Record all features
// Test: Records with compact constructor, accessors, and custom methods
// Expected Version: 16
// Required Features: RECORDS
public class Combo_RecordAllFeatures {
    record Person(String name, int age) {
        // Compact constructor
        public Person {
            if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
        }

        // Custom method
        public boolean isAdult() {
            return age >= 18;
        }
    }

    void test() {
        Person p = new Person("Alice", 25);
        System.out.println(p.name() + " is " + (p.isAdult() ? "adult" : "minor"));
    }
}