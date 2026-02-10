package me.bechberger.sizes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgramResultsParsingTest {

    @Test
    void parseGraphLayoutPrintable_parsesRows() {
        String printable = """
                whatever object graph:
                     ADDRESS       SIZE TYPE                             PATH                           VALUE
                    75f7d7a28         24 me.bechberger.sizes.programs.StringSize                             (object)
                    75f7d7a40         16 java.lang.String                    .value                         abcdefg
                    75f7d7a50         16 [B                                 .value.value                  (byte[7])
                """;

        List<ProgramResults.GraphRow> rows = ProgramResults.parseGraphLayoutPrintable(printable);
        assertEquals(3, rows.size());

        assertEquals(24, rows.get(0).size());
        assertTrue(rows.get(0).type().contains("StringSize"));

        assertEquals(16, rows.get(1).size());
        assertEquals("java.lang.String", rows.get(1).type());
        assertEquals(".value", rows.get(1).path());
        assertEquals("abcdefg", rows.get(1).value());

        assertEquals("[B", rows.get(2).type());
        assertTrue(rows.get(2).path().startsWith(".value"));
    }

    @Test
    void parseFootprint_parsesRows_andIgnoresTotal() {
        String footprint = """
                whatever footprint:
                     COUNT       AVG       SUM   DESCRIPTION
                         1        16        16   java.lang.String
                         1        24        24   me.bechberger.sizes.programs.StringSize
                         1        16        16   [B
                         3                 56   (total)
                """;

        List<ProgramResults.FootprintRow> rows = ProgramResults.parseFootprint(footprint);
        // total line should be ignored
        assertEquals(3, rows.size());

        assertEquals(1, rows.get(0).count());
        assertEquals(16L, rows.get(0).avg());
        assertEquals(16L, rows.get(0).sum());
        assertEquals("java.lang.String", rows.get(0).description());
    }

    @Test
    void extractTotalSizeFromFootprint_parsesTotalSum() {
        String footprint = """
                whatever footprint:
                     COUNT       AVG       SUM   DESCRIPTION
                         1        16        16   java.lang.String
                         1        24        24   me.bechberger.sizes.programs.StringSize
                         1        16        16   [B
                         3                 56   (total)
                """;

        assertEquals(56L, ProgramResults.extractTotalSizeFromFootprint(footprint));
    }

    @Test
    void extractOwnSizeFromPrintableGraph_parsesRootObjectRow() {
        String printable = """
                whatever object graph:
                     ADDRESS       SIZE TYPE                             PATH                           VALUE
                    75f7d7a28         24 me.bechberger.sizes.programs.StringSize                             (object)
                    75f7d7a40         16 java.lang.String                    .value                         abcdefg
                """;

        assertEquals(24L, ProgramResults.extractOwnSizeFromPrintableGraph(printable));
    }

    @Test
    void parseReturnsEmptyListWhenNoHeaderPresent() {
        assertTrue(ProgramResults.parseGraphLayoutPrintable("no header here").isEmpty());
        assertTrue(ProgramResults.parseFootprint("no header here").isEmpty());
    }

    @Test
    void parseGraphLayoutPrintable_stripsOuterNameFromInnerClassType() {
        String printable = """
                whatever object graph:
                     ADDRESS       SIZE TYPE                             PATH                           VALUE
                    75f7d7a28         24 me.bechberger.sizes.programs.sharing.DistinctBoxedIntegerCached$Holder   (object)
                    75f7d7a40         16 java.lang.String                    .s                             abc
                    75f7d7a50         16 [B                                 .bytes                         (byte[3])
                """;

        List<ProgramResults.GraphRow> rows = ProgramResults.parseGraphLayoutPrintable(printable);
        assertEquals(3, rows.size());
        assertEquals("Holder", rows.get(0).type());
        assertEquals("java.lang.String", rows.get(1).type());
        assertEquals("[B", rows.get(2).type());
    }

    @Test
    void parseFootprint_stripsOuterNameFromInnerClassDescription() {
        String footprint = """
                whatever footprint:
                     COUNT       AVG       SUM   DESCRIPTION
                         1        24        24   me.bechberger.sizes.programs.sharing.DistinctBoxedIntegerCached$Holder
                         1        16        16   java.lang.String
                         2        16        32   [B
                         4                72   (total)
                """;

        List<ProgramResults.FootprintRow> rows = ProgramResults.parseFootprint(footprint);
        assertEquals(3, rows.size());
        assertEquals("Holder", rows.get(0).description());
        assertEquals("java.lang.String", rows.get(1).description());
        assertEquals("[B", rows.get(2).description());
    }

    @Test
    void parseGraphLayoutPrintable_handlesParenthesizedTypeWithSpaces_likeMultiRecord() {
        String printable = """
                whatever object graph:
                     ADDRESS       SIZE TYPE                       PATH                           VALUE
                    30346260         24 me.bechberger.sizes.programs.records.MultiRecord$Person                                (object)
                    30346270       6464 (something else)           (somewhere else)               (something else)
                    3036ab18         24 java.lang.String           .name                          (object)
                """;

        List<ProgramResults.GraphRow> rows = ProgramResults.parseGraphLayoutPrintable(printable);
        assertEquals(3, rows.size());

        // Root row: inner class type should be normalized
        assertEquals(24, rows.get(0).size());
        assertEquals("Person", rows.get(0).type());
        assertEquals("", rows.get(0).path());
        assertEquals("(object)", rows.get(0).value());

        // Critical row: TYPE contains spaces and must not be split across type/path/value
        assertEquals(6464, rows.get(1).size());
        assertEquals("(something else)", rows.get(1).type());
        assertEquals("(somewhere else)", rows.get(1).path());
        assertEquals("(something else)", rows.get(1).value());

        assertEquals("java.lang.String", rows.get(2).type());
        assertEquals(".name", rows.get(2).path());
    }

    @Test
    void parseGraphLayoutPrintable_parenthesizedTypeWithSpaces_andNoPath() {
        // Some rows have a parenthesized type with spaces, but no PATH column at all.
        String printable = """
                whatever object graph:
                     ADDRESS       SIZE TYPE                       PATH                           VALUE
                    30346270       1234 (something else)                                        (something else)
                """;

        var rows = ProgramResults.parseGraphLayoutPrintable(printable);
        assertEquals(1, rows.size());
        assertEquals(1234, rows.get(0).size());
        assertEquals("(something else)", rows.get(0).type());
        assertEquals("", rows.get(0).path());
        assertEquals("(something else)", rows.get(0).value());
    }

    @Test
    void parseGraphLayoutPrintable_parenthesizedTypeWithoutClosingParen_fallsBack() {
        // Defensive: if JOL (or filtering) ever produces an unbalanced '(', we should not crash.
        String printable = """
                whatever object graph:
                     ADDRESS       SIZE TYPE                             PATH                           VALUE
                    30346270       10   (weird                          .p                             v
                """;

        var rows = ProgramResults.parseGraphLayoutPrintable(printable);
        assertEquals(1, rows.size());
        assertEquals(10, rows.get(0).size());
        assertEquals("(weird", rows.get(0).type());
        // Remaining tokens are best-effort; we mainly assert it doesn't throw and produces a row.
    }

    @Test
    void parseGraphLayoutPrintable_ignoresLeadingNoiseUntilHeader() {
        String printable = """
                some prelude that should be ignored
                another line
                     ADDRESS       SIZE TYPE                             PATH                           VALUE
                    75f7d7a40         16 java.lang.String                    .value                         abcdefg
                """;

        var rows = ProgramResults.parseGraphLayoutPrintable(printable);
        assertEquals(1, rows.size());
        assertEquals("java.lang.String", rows.get(0).type());
        assertEquals(".value", rows.get(0).path());
    }
}