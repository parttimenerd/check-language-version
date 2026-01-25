// Java 16 edge case: Record with compact constructor
// Test: Testing records with compact constructor syntax for validation
// Expected Version: 16
// Required Features: RECORDS
public class Edge_RecordCompactConstructor {
    record Person(String name, int age) {
        public Person {
            if (age < 0) throw new IllegalArgumentException();
            name = name.trim();
        }
    }
}