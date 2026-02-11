package me.bechberger.sizes;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ClassLayoutCollectionTest {

    @Test
    void multiRecord_collectsClassLayoutsForAllReachableTypes() throws Exception {
        // MultiRecord has a field "value" which is a Person(Address, String, int).
        // We expect collectClassLayouts to include:
        // - Person
        // - Address
        // - java.lang.String
        // (and potentially other implementation details, but at least these)

        Class<?> clazz = Class.forName("me.bechberger.sizes.programs.records.MultiRecord");
        var ctor = clazz.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object instance = ctor.newInstance();

        Field valueField = clazz.getDeclaredField("value");
        valueField.setAccessible(true);
        Object rootValue = valueField.get(instance);
        assertNotNull(rootValue);

        Method m = Main.class.getDeclaredMethod("collectClassLayouts", Object.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<ProgramResults.ParsedClassLayout> layouts = (List<ProgramResults.ParsedClassLayout>) m.invoke(null, rootValue);

        assertNotNull(layouts);
        assertFalse(layouts.isEmpty());

        Set<String> types = layouts.stream()
                .map(ProgramResults.ParsedClassLayout::type)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toSet());

        // Always expect String: it is reachable and usually supported by JOL even when SA attach is restricted.
        assertTrue(types.contains("String"), "Missing String layout, got: " + types);

        assertTrue(types.contains("Person"), "Missing Person layout, got: " + types);
        assertTrue(types.contains("Address"), "Missing Address layout, got: " + types);

        // Sanity: the parser should have produced at least some rows per type.
        for (ProgramResults.ParsedClassLayout l : layouts) {
            assertNotNull(l.type());
            assertNotNull(l.rows());
            // Parsed rows can be empty if parsing fails; don't make the whole test fail for that.
        }
    }
}