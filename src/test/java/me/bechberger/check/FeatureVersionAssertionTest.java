package me.bechberger.check;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that verifies the assertion that requiredJavaVersion matches the highest feature version.
 */
public class FeatureVersionAssertionTest {

    @Test
    void testVersionMatchesHighestFeatureVersion() throws Exception {
        // Test with a Java 8 file
        URL resource = getClass().getClassLoader().getResource("feature_tests/Java8_StreamAPI.java");
        assertNotNull(resource, "Test file not found");

        File file = new File(resource.toURI());
        FeatureChecker.FeatureCheckResult result = FeatureChecker.check(file);

        assertNotNull(result, "Parse should succeed");

        // Verify the assertion: requiredJavaVersion should equal the max feature version
        int maxFeatureVersion = result.features().stream()
                .mapToInt(FeatureChecker.JavaFeature::getJavaVersion)
                .max()
                .orElse(0);

        assertEquals(maxFeatureVersion, result.requiredJavaVersion(),
                String.format("Required Java version (%d) should match highest feature version (%d). Features: %s",
                        result.requiredJavaVersion(), maxFeatureVersion,
                        result.features().stream().map(FeatureChecker.JavaFeature::name).collect(Collectors.joining(", "))));
    }

    @Test
    void testVersionMatchesForMultipleFiles() throws Exception {
        String[] testFiles = {
            "feature_tests/Java8_StreamAPI.java",
            "feature_tests/Java11_HttpClient.java",
            "feature_tests/Java14_SwitchExpressions.java",
            "feature_tests/Java17_SealedClasses.java"
        };

        for (String testFile : testFiles) {
            URL resource = getClass().getClassLoader().getResource(testFile);
            if (resource == null) {
                System.out.println("Skipping missing file: " + testFile);
                continue;
            }

            File file = new File(resource.toURI());
            FeatureChecker.FeatureCheckResult result = FeatureChecker.check(file);

            if (result != null) {
                int maxFeatureVersion = result.features().stream()
                        .mapToInt(FeatureChecker.JavaFeature::getJavaVersion)
                        .max()
                        .orElse(0);

                assertEquals(maxFeatureVersion, result.requiredJavaVersion(),
                        String.format("File %s: Required Java version (%d) should match highest feature version (%d)",
                                file.getName(), result.requiredJavaVersion(), maxFeatureVersion));
            }
        }
    }

    @Test
    void testAssertionInConstructor() {
        // This test verifies that the assertion in the compact constructor works
        // We can't easily trigger an assertion error in a test, but we can verify the logic

        File dummyFile = new File("test.java");
        Set<FeatureChecker.JavaFeature> features = Set.of(
            FeatureChecker.JavaFeature.LAMBDAS,  // Java 8
            FeatureChecker.JavaFeature.STREAM_API  // Java 8
        );

        // This should work fine - version 8 matches the highest feature version
        FeatureChecker.FeatureCheckResult result = new FeatureChecker.FeatureCheckResult(
            dummyFile, features, 8
        );

        assertEquals(8, result.requiredJavaVersion());
        assertEquals(features.size(), result.features().size());
    }
}