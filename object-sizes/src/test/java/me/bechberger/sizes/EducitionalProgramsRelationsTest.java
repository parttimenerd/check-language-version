package me.bechberger.sizes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.bechberger.minicli.MiniCli;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Educational relation tests: compare programs that demonstrate the same concept
 * and assert relative properties (sharing vs duplication, cycles finish, etc.).
 */
class EducationalProgramsRelationsTest {

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

    private static long totalSize(ProgramResults.ProgramResult r) {
        assertNotNull(r.layout());
        assertFalse(r.layout().isEmpty());
        assertNotNull(r.layout().getFirst().totalSize());
        return r.layout().getFirst().totalSize();
    }

    private static void assertRatingMatchesLayout(ProgramResults.ProgramResult r) {
        assertNotNull(r.rating());
        assertNotNull(r.layout());
        assertFalse(r.layout().isEmpty());
        assertNotNull(r.layout().getFirst().layout());
        assertEquals(r.layout().getFirst().layout().size(), r.rating());
    }

    @Test
    void sharingReducesOrEqualsGraphSize() throws Exception {
        var shared = runOne("me.bechberger.sizes.programs.sharing.SharedIntArrayTwice");
        var distinct = runOne("me.bechberger.sizes.programs.sharing.DistinctIntArraysTwice");

        assertRatingMatchesLayout(shared);
        assertRatingMatchesLayout(distinct);

        assertTrue(totalSize(shared) <= totalSize(distinct),
                "Shared graph should not be larger than duplicated graph");
    }

    @Test
    void cachedIntegerSharingVsForcedDistinctAllocation() throws Exception {
        var shared = runOne("me.bechberger.sizes.programs.sharing.SharedBoxedIntegerCached");
        var distinct = runOne("me.bechberger.sizes.programs.sharing.DistinctBoxedIntegerCached");

        assertRatingMatchesLayout(shared);
        assertRatingMatchesLayout(distinct);

        assertTrue(totalSize(shared) <= totalSize(distinct),
                "Reusing the same Integer instance should not be larger than two allocated Integers");
    }

    @Test
    void cyclesDoNotExplodeLayout() throws Exception {
        for (String fqcn : List.of(
                "me.bechberger.sizes.programs.circular.SelfCycle",
                "me.bechberger.sizes.programs.circular.ThreeNodeCycle",
                "me.bechberger.sizes.programs.circular.ArraySelfReference"
        )) {
            var r = runOne(fqcn);
            assertTrue(totalSize(r) > 0);
            assertRatingMatchesLayout(r);

            // sanity: layout row count should stay small for these tiny cycles
            assertTrue(r.rating() < 100, "Unexpectedly large rating for " + fqcn + ": " + r.rating());
        }
    }
}