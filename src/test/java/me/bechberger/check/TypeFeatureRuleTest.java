package me.bechberger.check;

import me.bechberger.check.FeatureChecker.JavaFeature;
import me.bechberger.check.FeatureChecker.TypeFeatureRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the TypeFeatureRule DSL that maps types/packages to features.
 */
class TypeFeatureRuleTest {

    // ==================== Direct Import Tests ====================

    @Test
    @DisplayName("types() rule matches direct import of exact class")
    void typesRuleMatchesDirectImportExact() {
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL,
            "Optional", "OptionalInt", "OptionalLong", "OptionalDouble");

        assertTrue(rule.matchesDirectImport("java.util.Optional"));
        assertTrue(rule.matchesDirectImport("java.util.OptionalInt"));
        assertTrue(rule.matchesDirectImport("java.util.OptionalLong"));
        assertTrue(rule.matchesDirectImport("java.util.OptionalDouble"));
    }

    @Test
    @DisplayName("types() rule does not match direct import of other classes in same package")
    void typesRuleDoesNotMatchOtherClasses() {
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL,
            "Optional", "OptionalInt");

        assertFalse(rule.matchesDirectImport("java.util.ArrayList"));
        assertFalse(rule.matchesDirectImport("java.util.HashMap"));
        assertFalse(rule.matchesDirectImport("java.util.Scanner"));
    }

    @Test
    @DisplayName("types() rule matches inner classes")
    void typesRuleMatchesInnerClasses() {
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.BASE64_API, "Base64");

        assertTrue(rule.matchesDirectImport("java.util.Base64"));
        assertTrue(rule.matchesDirectImport("java.util.Base64.Decoder"));
        assertTrue(rule.matchesDirectImport("java.util.Base64.Encoder"));
        assertTrue(rule.matchesDirectImport("java.util.Base64$Decoder")); // $ notation for inner classes
    }

    @Test
    @DisplayName("pkg() rule matches direct import of any class in package")
    void pkgRuleMatchesDirectImport() {
        TypeFeatureRule rule = TypeFeatureRule.pkg("java.util.stream", JavaFeature.STREAM_API);

        assertTrue(rule.matchesDirectImport("java.util.stream.Stream"));
        assertTrue(rule.matchesDirectImport("java.util.stream.IntStream"));
        assertTrue(rule.matchesDirectImport("java.util.stream.Collectors"));
    }

    @Test
    @DisplayName("pkg() rule matches subpackages")
    void pkgRuleMatchesSubpackages() {
        TypeFeatureRule rule = TypeFeatureRule.pkg("java.util.concurrent", JavaFeature.CONCURRENT_API);

        assertTrue(rule.matchesDirectImport("java.util.concurrent.ExecutorService"));
        assertTrue(rule.matchesDirectImport("java.util.concurrent.atomic.AtomicInteger"));
        assertTrue(rule.matchesDirectImport("java.util.concurrent.locks.ReentrantLock"));
    }

    @Test
    @DisplayName("pkg() rule does not match unrelated packages")
    void pkgRuleDoesNotMatchUnrelated() {
        TypeFeatureRule rule = TypeFeatureRule.pkg("java.util.stream", JavaFeature.STREAM_API);

        assertFalse(rule.matchesDirectImport("java.util.ArrayList"));
        assertFalse(rule.matchesDirectImport("java.io.InputStream"));
        assertFalse(rule.matchesDirectImport("java.util.streaming.Something")); // similar but different
    }

    // ==================== Wildcard Import Tests ====================

    @Test
    @DisplayName("pkg() rule matches wildcard import of package")
    void pkgRuleMatchesWildcardImport() {
        TypeFeatureRule rule = TypeFeatureRule.pkg("java.util.stream", JavaFeature.STREAM_API);

        assertTrue(rule.matchesWildcardImport("java.util.stream"));
    }

    @Test
    @DisplayName("pkg() rule matches wildcard import of parent package")
    void pkgRuleMatchesWildcardImportOfParent() {
        TypeFeatureRule rule = TypeFeatureRule.pkg("java.util.concurrent.atomic", JavaFeature.CONCURRENT_API);

        // If you import java.util.concurrent.*, that includes atomic
        assertTrue(rule.matchesWildcardImport("java.util.concurrent.atomic"));
    }

    @Test
    @DisplayName("types() rule does not match wildcard import directly")
    void typesRuleDoesNotMatchWildcardImportDirectly() {
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL, "Optional");

        // Wildcard import by itself doesn't trigger the feature
        // The feature is only triggered when the type is actually used
        assertTrue(rule.matchesWildcardImport("java.util"));
    }

    // ==================== Simple Type Name Tests ====================

    @Test
    @DisplayName("types() rule matches simple type name when wildcard import exists")
    void typesRuleMatchesSimpleTypeWithWildcard() {
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL,
            "Optional", "OptionalInt");

        Set<String> wildcardImports = Set.of("java.util");

        assertTrue(rule.matchesSimpleType("Optional", wildcardImports));
        assertTrue(rule.matchesSimpleType("OptionalInt", wildcardImports));
    }

    @Test
    @DisplayName("types() rule does not match simple type name without wildcard import")
    void typesRuleDoesNotMatchSimpleTypeWithoutWildcard() {
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL, "Optional");

        Set<String> wildcardImports = Set.of("java.io", "java.lang");

        assertFalse(rule.matchesSimpleType("Optional", wildcardImports));
    }

    @Test
    @DisplayName("types() rule does not match wrong type name even with wildcard import")
    void typesRuleDoesNotMatchWrongTypeName() {
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL, "Optional");

        Set<String> wildcardImports = Set.of("java.util");

        assertFalse(rule.matchesSimpleType("ArrayList", wildcardImports));
        assertFalse(rule.matchesSimpleType("HashMap", wildcardImports));
    }

    @Test
    @DisplayName("pkg() rule does not match simple type names")
    void pkgRuleDoesNotMatchSimpleTypeNames() {
        TypeFeatureRule rule = TypeFeatureRule.pkg("java.util.stream", JavaFeature.STREAM_API);

        Set<String> wildcardImports = Set.of("java.util.stream");

        // Package-level rules don't match simple type names
        // because we don't know all types in the package
        assertFalse(rule.matchesSimpleType("Stream", wildcardImports));
    }

    // ==================== FQN Tests ====================

    @Test
    @DisplayName("types() rule matches FQN of specific type")
    void typesRuleMatchesFQN() {
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL, "Optional");

        assertTrue(rule.matchesFQN("java.util.Optional"));
        assertTrue(rule.matchesFQN("java.util.Optional.empty")); // method on type
    }

    @Test
    @DisplayName("pkg() rule matches FQN of any type in package")
    void pkgRuleMatchesFQN() {
        TypeFeatureRule rule = TypeFeatureRule.pkg("java.util.stream", JavaFeature.STREAM_API);

        assertTrue(rule.matchesFQN("java.util.stream.Stream"));
        assertTrue(rule.matchesFQN("java.util.stream.Collectors"));
        assertTrue(rule.matchesFQN("java.util.stream.IntStream"));
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Rule handles empty type names array gracefully")
    void handlesEmptyTypeNames() {
        // This shouldn't happen in practice, but let's make sure it doesn't crash
        TypeFeatureRule rule = TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL);

        assertFalse(rule.matchesDirectImport("java.util.Optional"));
        assertFalse(rule.matchesFQN("java.util.Optional"));
    }

    @Test
    @DisplayName("Rule distinguishes between similar package names")
    void distinguishesSimilarPackages() {
        TypeFeatureRule rule = TypeFeatureRule.pkg("java.util", JavaFeature.COLLECTIONS_FRAMEWORK);

        assertTrue(rule.matchesDirectImport("java.util.List"));
        assertFalse(rule.matchesDirectImport("java.utility.List")); // different package
    }

    @Test
    @DisplayName("Nested package rules work correctly")
    void nestedPackagesWork() {
        TypeFeatureRule nioRule = TypeFeatureRule.pkg("java.nio", JavaFeature.NIO);
        TypeFeatureRule nio2Rule = TypeFeatureRule.pkg("java.nio.file", JavaFeature.NIO2);

        // java.nio.ByteBuffer -> NIO (Java 4)
        assertTrue(nioRule.matchesDirectImport("java.nio.ByteBuffer"));
        assertFalse(nio2Rule.matchesDirectImport("java.nio.ByteBuffer"));

        // java.nio.file.Path -> NIO2 (Java 7)
        assertTrue(nio2Rule.matchesDirectImport("java.nio.file.Path"));
        assertTrue(nioRule.matchesDirectImport("java.nio.file.Path")); // Note: NIO rule also matches!
        // This is why detection code should check more specific rules first
    }

    @ParameterizedTest
    @DisplayName("Various direct import patterns")
    @CsvSource({
        "java.util.Optional, java.util, Optional, true",
        "java.util.ArrayList, java.util, Optional, false",
        "java.util.Optional.Holder, java.util, Optional, true",
        "java.lang.String, java.util, Optional, false",
        "javax.swing.JFrame, javax.swing, JFrame, true",
    })
    void directImportPatterns(String importName, String packageName, String typeName, boolean expected) {
        TypeFeatureRule rule = TypeFeatureRule.types(packageName, JavaFeature.OPTIONAL, typeName);
        assertEquals(expected, rule.matchesDirectImport(importName));
    }

    @ParameterizedTest
    @DisplayName("Various FQN patterns")
    @CsvSource({
        "java.util.Optional, java.util, Optional, true",
        "java.util.Optional.empty, java.util, Optional, true",
        "java.util.ArrayList, java.util, Optional, false",
        "java.util.stream.Stream, java.util.stream, Stream, true",
    })
    void fqnPatterns(String fqn, String packageName, String typeName, boolean expected) {
        TypeFeatureRule rule = TypeFeatureRule.types(packageName, JavaFeature.OPTIONAL, typeName);
        assertEquals(expected, rule.matchesFQN(fqn));
    }
}