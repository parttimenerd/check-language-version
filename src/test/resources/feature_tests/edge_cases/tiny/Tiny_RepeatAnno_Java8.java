// Tiny: Repeating anno (Java 8)
// Expected Version: 8
// Required Features: ANNOTATIONS, REPEATING_ANNOTATIONS, TYPE_ANNOTATIONS, CLASS_PROPERTY

import java.lang.annotation.*;
@Repeatable(As.class) @interface A { int value(); }
@interface As { A[] value(); }

@A(1) @A(2)
class Tiny_RepeatAnno_Java8 {}