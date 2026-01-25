// Edge case: Record variations
// Expected Version: 16
// Required Features: RECORDS
public class RecordEdgeCases_Java16 {

    record Point(int x, int y) {}

    record Person(String name, int age) {
        public Person {
            if (age < 0) throw new IllegalArgumentException();
        }
    }

    record Rectangle(int width, int height) {
        public int area() {
            return width * height;
        }
    }

    record Pair<T, U>(T first, U second) {}

    public void testRecords() {
        Point p = new Point(3, 4);
        int x = p.x();

        Person person = new Person("John", 25);

        Pair<String, Integer> pair = new Pair<>("hello", 42);
    }
}