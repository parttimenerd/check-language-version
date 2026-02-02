package me.bechberger.check;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates {@code src/main/resources/me/bechberger/check/feature-descriptions.format}
 * according to the rules stated at the top of the file.
 *
 * <p>Note: The current repository file still contains only template feature blocks (AWT, APPLET).
 * This test validates structure and link rules for the blocks that exist.
 * Once the file is expanded to include all JavaFeature entries, the coverage check can be enabled.
 */
class FeatureDescriptionsMdmapValidatorTest {

    @Test
    void mdmapHasOnlyValidFeatureBlocksAndNoBareUrls() {
        // Feature descriptions have been migrated from feature-descriptions.format
        // to per-feature resources under /me/bechberger/check/features/*.md.
        // Validation is now covered by FeatureMarkdownDescriptionsTest and FeatureMarkdownLinksTest.
        org.junit.jupiter.api.Assumptions.assumeTrue(false, "mdmap validator test deprecated (use per-feature markdown files)");
    }

    @Test
    void noNonCommentContentBeforeFirstHeader() {
        org.junit.jupiter.api.Assumptions.assumeTrue(false, "mdmap validator test deprecated (use per-feature markdown files)");
    }
}