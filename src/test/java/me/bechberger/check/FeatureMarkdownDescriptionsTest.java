package me.bechberger.check;

import me.bechberger.check.FeatureChecker.JavaFeature;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class FeatureMarkdownDescriptionsTest {

    @Test
    void hasMarkdownForEveryFeature() {
        assertEquals(EnumSet.allOf(JavaFeature.class), FeatureMarkdownDescriptions.MARKDOWN_BY_FEATURE.keySet(),
                "Every JavaFeature must have a markdown block in feature-descriptions.format");
    }

    @Test
    void markdownIsNotBlank() {
        for (JavaFeature feature : JavaFeature.values()) {
            String md = FeatureMarkdownDescriptions.markdown(feature);
            assertNotNull(md);
            assertFalse(md.isBlank(), "Markdown should not be blank for " + feature.name());
        }
    }
}