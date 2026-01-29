// Tiny: Enum looks like sealed (Java 5)
// Expected Version: 5
// Required Features: ENUMS

enum Status { PENDING, RUNNING, DONE }

class Tiny_EnumLooksSealed_Java5 {
    Status state = Status.PENDING;
}