package me.bechberger.sizes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.bechberger.minicli.MiniCli;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppendMergesLayoutsTest {

    @Test
    void appendWorksAndKeepsBothLayouts() throws Exception {
        Path out = Files.createTempFile("object-sizes-", ".json");
        // Start with an empty file or no file at all.
        Files.deleteIfExists(out);

        String fqcn = "me.bechberger.sizes.programs.StringSize";

        // First run: compact headers = false
        int exit0 = MiniCli.run(new Main(), System.out, System.err, new String[]{
                "--output", out.toString(),
                "--class", fqcn,
                "--compact-headers", "false"
        });
        assertEquals(0, exit0);

        // Second run: append compact headers = true
        int exit1 = MiniCli.run(new Main(), System.out, System.err, new String[]{
                "--output", out.toString(),
                "--append",
                "--class", fqcn,
                "--compact-headers", "true"
        });
        assertEquals(0, exit1);

        assertTrue(Files.exists(out));

        ObjectMapper om = new ObjectMapper();
        List<ProgramResults.ProgramResult> results = om.readValue(Files.readString(out), new TypeReference<>() {
        });

        assertEquals(1, results.size(), "append should keep one program entry and merge layouts");
        assertEquals(fqcn, results.get(0).className());

        var r0 = results.get(0);
        var layouts = r0.layout();
        assertNotNull(layouts);
        assertEquals(2, layouts.size());

        var l0 = layouts.get(0);
        var l1 = layouts.get(1);

        assertNotNull(l0.layout());
        assertFalse(l0.layout().isEmpty());
        assertNotNull(l1.layout());
        assertFalse(l1.layout().isEmpty());
    }
}