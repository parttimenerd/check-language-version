// Tiny: Repeating anno (Java 8)
// Expected Version: 8
// Required Features: REPEATING_ANNOTATIONS

import java.lang.annotation.*;
@Repeatable(As.class) @interface A { int value(); }
@interface As { A[] value(); }

@A(1) @A(2)
public class Tiny_RepeatAnno_Java8 {}