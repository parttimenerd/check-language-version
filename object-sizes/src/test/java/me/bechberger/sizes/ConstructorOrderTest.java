package me.bechberger.sizes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.bechberger.minicli.MiniCli;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorOrderTest {

    @Test
    void constructorRunsBeforeValueIsRead() throws Exception {
        Path out = Files.createTempFile("object-sizes-", ".json");
        Files.deleteIfExists(out);

        // This program assigns 'value' in the constructor.
        String fqcn = "me.bechberger.sizes.programs.simple.ConstructorSetsValue";

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

        var r = results.getFirst();
        assertEquals(fqcn, r.className());

        // If value was read before constructor ran, it would be null and totalSize would be 0.
        assertNotNull(r.layout());
        assertFalse(r.layout().isEmpty());
        assertNotNull(r.layout().getFirst().totalSize());
        assertTrue(r.layout().getFirst().totalSize() > 0, "Expected a non-null root value set by constructor");

        assertNotNull(r.rating());
        assertEquals(r.layout().getFirst().layout().size(), r.rating());
    }
}