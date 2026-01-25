// Tiny: Enum in switch (Java 5)
// Expected Version: 5
// Required Features: ENUMS

enum E { A }

public class Tiny_EnumSwitch_Java5 {
    void test(E e) { switch(e) { case A: } }
}