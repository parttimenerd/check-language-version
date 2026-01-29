// Java 16 feature: Records (JEP 395)
// Test: Records (Java 16)
// Expected Version: 16
// Required Features: RECORDS
class Java16_Records {
    record Point(int x, int y) {}
    record Person(String name, int age) {
        public Person {
            if (age < 0) throw new IllegalArgumentException();
        }
    }
}