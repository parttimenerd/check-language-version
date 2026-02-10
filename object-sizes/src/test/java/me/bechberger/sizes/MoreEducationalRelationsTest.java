package me.bechberger.sizes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.bechberger.minicli.MiniCli;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoreEducationalRelationsTest {

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

    @Test
    void autoboxCacheShouldNotBeBiggerThanOutsideCache() throws Exception {
        var cached = runOne("me.bechberger.sizes.programs.pools.AutoboxCached42_TwoRefs");
        var notCached = runOne("me.bechberger.sizes.programs.pools.AutoboxNotCached1000_TwoRefs");

        assertTrue(totalSize(cached) <= totalSize(notCached));
    }

    @Test
    void oneNullElementArrayShouldNotBeBiggerThanTwoObjectArray() throws Exception {
        var halfNull = runOne("me.bechberger.sizes.programs.arrays.ObjectArrayLen2_HalfNull");
        var twoObjects = runOne("me.bechberger.sizes.programs.arrays.ObjectArrayLen2_Objects");

        assertTrue(totalSize(halfNull) <= totalSize(twoObjects));
    }
}