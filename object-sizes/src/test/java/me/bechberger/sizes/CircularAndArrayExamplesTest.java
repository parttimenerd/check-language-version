package me.bechberger.sizes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.bechberger.minicli.MiniCli;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CircularAndArrayExamplesTest {

    private static ProgramResults.ProgramResult runOne(String fqcn) throws Exception {
        Path out = Files.createTempFile("object-sizes-", ".json");
        Files.deleteIfExists(out);

        int exit = MiniCli.run(new Main(), System.out, System.err, new String[]{
                "--output", out.toString(),
                "--class", fqcn,
                "--compact-headers", "false"
        });
        assertEquals(0, exit);

        ObjectMapper om = new ObjectMapper();
        List<ProgramResults.ProgramResult> results = om.readValue(Files.readString(out), new TypeReference<>() {
        });
        assertEquals(1, results.size());
        return results.getFirst();
    }

    @Test
    void circularExamplesProduceNonZeroSizeAndRating() throws Exception {
        for (String fqcn : List.of(
                "me.bechberger.sizes.programs.circular.SelfCycle",
                "me.bechberger.sizes.programs.circular.TwoNodeCycle",
                "me.bechberger.sizes.programs.circular.ThreeNodeCycle",
                "me.bechberger.sizes.programs.circular.ArraySelfReference"
        )) {
            var r = runOne(fqcn);
            assertEquals(fqcn, r.className());

            assertNotNull(r.layout());
            assertFalse(r.layout().isEmpty());

            var l = r.layout().getFirst();
            assertNotNull(l.totalSize());
            assertTrue(l.totalSize() > 0);

            assertNotNull(r.rating());
            assertNotNull(l.layout());
            assertEquals(l.layout().size(), r.rating());
        }
    }

    @Test
    void arrayExamplesHaveExpectedRootTypeAndRating() throws Exception {
        for (String fqcn : List.of(
                "me.bechberger.sizes.programs.arrays.ByteArrayLen2",
                "me.bechberger.sizes.programs.arrays.IntArrayLen2",
                "me.bechberger.sizes.programs.arrays.TwoDimIntArray_2x2",
                "me.bechberger.sizes.programs.arrays.ObjectArrayLen2_Nulls",
                "me.bechberger.sizes.programs.arrays.ObjectArrayLen2_Objects"
        )) {
            var r = runOne(fqcn);
            assertEquals(fqcn, r.className());

            var l = r.layout().getFirst();
            assertNotNull(l.layout());
            assertFalse(l.layout().isEmpty());

            // Root row should be present.
            var root = l.layout().getFirst();
            assertTrue(root.path().isBlank() || root.value().contains("(object)"), "Expected root row");

            assertNotNull(r.rating());
            assertEquals(l.layout().size(), r.rating());
        }
    }
}