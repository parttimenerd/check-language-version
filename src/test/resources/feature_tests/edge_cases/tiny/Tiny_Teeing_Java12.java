// Tiny: Collectors.teeing (Java 12)
// Expected Version: 12
// Required Features: COLLECTORS_TEEING

import java.util.stream.*;
import java.util.*;

public class Tiny_Teeing_Java12 {
    void test() {
        var r = List.of(1, 2, 3).stream().collect(
            Collectors.teeing(
                Collectors.summingInt(i -> i),
                Collectors.counting(),
                (s, c) -> s + "/" + c));
    }
}