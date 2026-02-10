package me.bechberger.sizes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.bechberger.minicli.MiniCli;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SanitizedCodeTest {

    @Test
    void sanitizedCodeIsEmittedAndScrubbed() throws Exception {
        Path out = Files.createTempFile("object-sizes-", ".json");
        Files.deleteIfExists(out);

        String fqcn = "me.bechberger.sizes.programs.collections.HashMapSize1";

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

        assertNotNull(r.code());
        assertFalse(r.code().isBlank());

        assertNotNull(r.sanitizedCode());
        assertFalse(r.sanitizedCode().isBlank());

        // Must not start with whitespace.
        assertEquals(r.sanitizedCode().trim(), r.sanitizedCode(), r.sanitizedCode());

        // Must not include comments.
        assertFalse(r.sanitizedCode().contains("/*"));
        assertFalse(r.sanitizedCode().contains("//"));

        // Must remove @SuppressWarnings("removal").
        assertFalse(r.sanitizedCode().contains("@SuppressWarnings(\"removal\")"), r.sanitizedCode());

        // Must replace the top-level class name with Test.
        assertTrue(r.sanitizedCode().contains("class Test"), r.sanitizedCode());

        // For snippets with constructors, constructor name should be Test after renaming.
        // (HashMapSize1 has an explicit constructor.)
        assertTrue(r.sanitizedCode().matches("(?s).*\\bTest\\s*\\(\\s*\\).*"), r.sanitizedCode());

        // Sanitized code should keep line breaks for readability.
        assertTrue(r.sanitizedCode().contains("\n"), r.sanitizedCode());

        assertNotNull(r.layout());
        assertFalse(r.layout().isEmpty());

        assertNotNull(r.rating());
        assertEquals(r.layout().getFirst().layout().size(), r.rating());
    }
}