package me.bechberger.sizes.programs.records;

/**
 * A small object graph consisting of multiple records.
 * Records are still regular objects with headers + fields.
 */
class MultiRecord {
    Person value = new Person(new Address("Main St", 42), "Alice", 30);

    record Address(String street, int number) {
    }

    record Person(Address address, String name, int age) {
    }
}