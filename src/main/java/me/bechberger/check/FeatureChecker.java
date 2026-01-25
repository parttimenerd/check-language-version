package me.bechberger.check;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeParameters;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.javaparser.FixValidators.fixJavaValidator;

/**
 * Checks which Java language features are used in a given Java source file.
 * Features are mapped to the Java version that introduced them (as stable, ignoring preview features).
 *
 * <p>Based on validators from JavaParser:
 * <a href="https://github.com/javaparser/javaparser/tree/master/javaparser-core/src/main/java/com/github/javaparser/ast/validator/language_level_validations">
 * JavaParser Validators</a>
 */
public class FeatureChecker {

    /**
     * Enum representing Java language features and the version they were introduced.
     *
     * Note: Some features are API-based (e.g., Stream API, NIO) rather than syntax-based.
     * These are detected via import statements and marked with isLibraryFeature=true.
     */
    public enum JavaFeature {
        // Java 1.0 features - core language and libraries
        AWT(1, true, "java.awt"),
        APPLET(1, true, "java.applet"),
        IO_API(1, true, "java.io"),

        // Java 1.1 features (Java1_1Validator: remove noInnerClasses, noReflection)
        INNER_CLASSES(1, false, "Inner classes"),
        REFLECTION(1, false, "Reflection"),
        JDBC(1, true, "java.sql"),
        RMI(1, true, "java.rmi"),
        JAVABEANS(1, true, "java.beans"),
        SERIALIZATION(1, true, "java.io.Serializable"),

        // Java 1.2 features (Java1_2Validator: strictfp modifier added via ModifierValidator)
        STRICTFP(2, false, "strictfp modifier"),
        SWING(2, true, "javax.swing"),
        COLLECTIONS_FRAMEWORK(2, true, "java.util.Collection"),
        CORBA(2, true, "org.omg.CORBA"),

        // Java 1.3 features
        JNDI(3, true, "javax.naming"),
        JAVA_SOUND(3, true, "javax.sound"),
        JPDA(3, true, "com.sun.jdi"),

        // Java 1.4 features (Java1_4Validator: remove noAssertKeyword)
        ASSERT(4, false, "Assert statement"),
        REGEX(4, true, "java.util.regex"),
        NIO(4, true, "java.nio"),
        LOGGING(4, true, "java.util.logging"),
        XML_API(4, true, "javax.xml"),
        PREFERENCES(4, true, "java.util.prefs"),
        IMAGE_IO(4, true, "javax.imageio"),

        // Java 5 features (Java5Validator: remove noGenerics, noAnnotations, noEnums, noVarargs, noForEach, noStaticImports)
        GENERICS(5, false, "Generics"),
        ENUMS(5, false, "Enumerations"),
        ANNOTATIONS(5, false, "Annotations"),
        VARARGS(5, false, "Varargs"),
        FOR_EACH(5, false, "for-each"),
        STATIC_IMPORT(5, false, "Static imports"),
        AUTOBOXING(5, false, "Autoboxing/unboxing"),
        CONCURRENT_API(5, true, "java.util.concurrent"),
        SCANNER(5, true, "java.util.Scanner"),

        // Java 6 features
        SCRIPTING_API(6, true, "javax.script"),
        COMPILER_API(6, true, "javax.tools"),
        JAXB(6, true, "javax.xml.bind"),
        JAX_WS(6, true, "javax.xml.ws"),
        STAX(6, true, "javax.xml.stream"),
        SWING_WORKER(6, true, "javax.swing.SwingWorker"),

        // Java 7 features (Java7Validator: remove genericsWithoutDiamondOperator, noBinaryIntegerLiterals,
        //                  noUnderscoresInIntegerLiterals, replace tryWithoutResources, replace noMultiCatch)
        DIAMOND_OPERATOR(7, false, "Diamond operator"),
        TRY_WITH_RESOURCES(7, false, "Try-with-resources"),
        MULTI_CATCH(7, false, "Multi-catch"),
        BINARY_LITERALS(7, false, "Binary literals"),
        UNDERSCORES_IN_LITERALS(7, false, "Underscores in literals"),
        STRINGS_IN_SWITCH(7, false, "Strings in switch"),
        FORK_JOIN(7, true, "java.util.concurrent.ForkJoinPool"),
        NIO2(7, true, "java.nio.file"),
        WATCH_SERVICE(7, true, "java.nio.file.WatchService"),

        // Java 8 features (Java8Validator: remove noLambdas, add defaultMethodsInInterface)
        LAMBDAS(8, false, "Lambda expressions"),
        METHOD_REFERENCES(8, false, "Method references"),
        DEFAULT_INTERFACE_METHODS(8, false, "Default interface methods"),
        STATIC_INTERFACE_METHODS(8, false, "Static interface methods"),
        REPEATING_ANNOTATIONS(8, false, "Repeating annotations"),
        TYPE_ANNOTATIONS(8, false, "Type annotations"),
        STREAM_API(8, true, "java.util.stream"),
        DATE_TIME_API(8, true, "java.time"),
        OPTIONAL(8, true, "java.util.Optional"),
        BASE64_API(8, true, "java.util.Base64"),

        // Java 9 features (Java9Validator: remove noModules, add privateInterfaceMethods via ModifierValidator)
        MODULES(9, false, "Modules"),
        PRIVATE_INTERFACE_METHODS(9, false, "Private interface methods"),
        TRY_WITH_EFFECTIVELY_FINAL(9, false, "Try-with effectively final"),
        DIAMOND_WITH_ANONYMOUS(9, false, "Diamond with anonymous classes"),
        PROCESS_API(9, true, "ProcessHandle"),
        REACTIVE_STREAMS(9, true, "java.util.concurrent.Flow"),
        STACK_WALKING(9, true, "java.lang.StackWalker"),
        COLLECTION_FACTORY_METHODS(9, true, "Collection factory methods"),

        // Java 10 features (Java10Validator: add varOnlyOnLocalVariableDefinitionAndForAndTry)
        VAR(10, false, "var"),
        COLLECTION_COPY_OF(10, true, "Collection.copyOf"),

        // Java 11 features (Java11Validator: replace with varAlsoInLambdaParameters)
        VAR_IN_LAMBDA(11, false, "var in lambda"),
        HTTP_CLIENT(11, true, "java.net.http"),
        FLIGHT_RECORDER(11, true, "jdk.jfr"),
        // Java 12 features
        COMPACT_NUMBER_FORMAT(12, true, "Compact number formatting"),
        COLLECTORS_TEEING(12, true, "Collectors.teeing()"),

        // Java 14 features - Switch Expressions (Standard)
        // (Java14Validator: remove noSwitchExpressions, onlyOneLabelInSwitchCase, noYield)
        SWITCH_EXPRESSIONS(14, false, "Switch expressions"),
        SWITCH_MULTIPLE_LABELS(14, false, "Multiple switch labels"),
        YIELD(14, false, "yield statement"),

        // Java 15 features (Java15Validator: remove noTextBlockLiteral)
        TEXT_BLOCKS(15, false, "Text blocks"),
        HIDDEN_CLASSES(15, true, "Hidden classes"),
        EDDSA(15, true, "EdDSA"),

        // Java 16 features - Pattern Matching for instanceof, Records
        // (Java16Validator: remove noPatternMatchingInstanceOf, noRecordDeclaration, innerClasses for local interfaces)
        RECORDS(16, false, "Records"),
        PATTERN_MATCHING_INSTANCEOF(16, false, "Pattern matching instanceof"),
        LOCAL_INTERFACES(16, false, "Local interfaces"),
        LOCAL_ENUMS(16, false, "Local enums"),
        UNIX_DOMAIN_SOCKETS(16, true, "Unix-domain sockets"),

        // Java 17 features - Sealed Classes
        // (Java17Validator: remove noSealedClasses, noPermitsListInClasses)
        SEALED_CLASSES(17, false, "Sealed classes"),
        RANDOM_GENERATOR(17, true, "java.util.random"),
        HEX_FORMAT(17, true, "java.util.HexFormat"),
        DESERIALIZATION_FILTERS(17, true, "Deserialization filters"),

        // Java 18 features
        SIMPLE_WEB_SERVER(18, true, "com.sun.net.httpserver"),
        INET_ADDRESS_RESOLVER(18, true, "java.net.spi.InetAddressResolverProvider"),

        // Java 21 features - Record Patterns, Pattern Matching for switch
        // Virtual Threads, Sequenced Collections, Key Encapsulation Mechanism
        // (Java21Validator: remove noSwitchNullDefault, noSwitchPatterns, noRecordPatterns)
        RECORD_PATTERNS(21, false, "Record patterns"),
        SWITCH_PATTERN_MATCHING(21, false, "Pattern matching in switch"),
        SWITCH_NULL_DEFAULT(21, false, "switch null/default"),
        SEQUENCED_COLLECTIONS(21, true, "Sequenced collections"),
        VIRTUAL_THREADS(21, true, "Virtual threads"),
        KEY_ENCAPSULATION(21, true, "Key encapsulation"),

        // Java 22 features - Unnamed Variables & Patterns, Foreign Function & Memory API
        // (Java22Validator: unnamed variables)
        UNNAMED_VARIABLES(22, false, "Unnamed variables"),
        FOREIGN_FUNCTION_API(22, true, "Foreign function API"),

        // Java 23 features - Markdown Documentation Comments
        MARKDOWN_DOC_COMMENTS(23, false, "Markdown doc comments"),

        // Java 24 features - Class-File API, Stream Gatherers
        STREAM_GATHERERS(24, true, "Stream gatherers"),
        CLASS_FILE_API(24, true, "Class-file API"),
        QUANTUM_RESISTANT_KEM(24, true, "Quantum-resistant KEM"),

        // Java 25 features - Many features became final
        // Scoped Values, Module Import Declarations
        // Flexible Constructor Bodies, Key Derivation Function API
        // Compact Source Files and Instance Main Methods
        SCOPED_VALUES(25, true, "Scoped values"),
        KEY_DERIVATION_API(25, true, "Key derivation"),
        MODULE_IMPORTS(25, false, "Module imports"),
        FLEXIBLE_CONSTRUCTOR_BODIES(25, false, "Flexible constructor bodies"),
        COMPACT_SOURCE_FILES(25, false, "Compact source files");

        private final int javaVersion;
        private final boolean libraryFeature;
        private final String description;

        JavaFeature(int javaVersion, boolean libraryFeature, String description) {
            this.javaVersion = javaVersion;
            this.libraryFeature = libraryFeature;
            this.description = description;
        }

        public int getJavaVersion() {
            return javaVersion;
        }

        /**
         * Returns true if this feature is library/API-based rather than a language syntax feature.
         * Library features are detected via import statements and don't affect compilation compatibility.
         */
        public boolean isLibraryFeature() {
            return libraryFeature;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name() + " (Java " + javaVersion + (libraryFeature ? ", API" : "") + "): " + description;
        }
    }

    // ==================== Type-to-Feature Mapping DSL ====================

    /**
     * DSL for mapping types/packages to features.
     * Supports three detection modes:
     * 1. Direct import: import java.util.Optional; -> OPTIONAL
     * 2. Wildcard import + type usage: import java.util.*; + Optional x; -> OPTIONAL
     * 3. Fully qualified name: java.util.Optional.empty() -> OPTIONAL
     */
    public static class TypeFeatureRule {
        final String packageName;      // e.g., "java.util"
        final String[] typeNames;      // e.g., ["Optional", "OptionalInt"] or null for package-level match
        final JavaFeature feature;
        final boolean packageLevelMatch; // true if any type in package triggers the feature

        private TypeFeatureRule(String packageName, String[] typeNames, JavaFeature feature, boolean packageLevelMatch) {
            this.packageName = packageName;
            this.typeNames = typeNames;
            this.feature = feature;
            this.packageLevelMatch = packageLevelMatch;
        }

        /**
         * Match specific types within a package.
         * E.g., types("java.util", OPTIONAL, "Optional", "OptionalInt", "OptionalLong", "OptionalDouble")
         */
        public static TypeFeatureRule types(String packageName, JavaFeature feature, String... typeNames) {
            return new TypeFeatureRule(packageName, typeNames, feature, false);
        }

        /**
         * Match any type in a package (package-level feature).
         * E.g., pkg("java.util.stream", STREAM_API) - any import/usage of java.util.stream triggers STREAM_API
         */
        public static TypeFeatureRule pkg(String packageName, JavaFeature feature) {
            return new TypeFeatureRule(packageName, null, feature, true);
        }

        /**
         * Check if a direct/explicit import matches this rule.
         * @param importName The fully qualified import name (e.g., "java.util.Optional")
         * @return true if this import triggers the feature
         */
        public boolean matchesDirectImport(String importName) {
            if (packageLevelMatch) {
                // Package-level: matches if import starts with package
                return importName.startsWith(packageName + ".") || importName.equals(packageName);
            } else {
                // Type-level: matches if import is exactly package.TypeName
                if (typeNames == null) return false;
                for (String typeName : typeNames) {
                    String fullName = packageName + "." + typeName;
                    if (importName.equals(fullName) || importName.startsWith(fullName + ".") || importName.startsWith(fullName + "$")) {
                        return true;
                    }
                }
                return false;
            }
        }

        /**
         * Check if a wildcard import matches this rule's package.
         * @param wildcardPackage The package from wildcard import (e.g., "java.util" from "import java.util.*;")
         * @return true if this wildcard import could provide types for this rule
         */
        public boolean matchesWildcardImport(String wildcardPackage) {
            return packageName.equals(wildcardPackage) || packageName.startsWith(wildcardPackage + ".");
        }

        /**
         * Check if a simple type name (after wildcard import) matches this rule.
         * @param simpleTypeName The simple type name (e.g., "Optional")
         * @param wildcardImports Set of wildcard import packages
         * @return true if this type triggers the feature
         */
        public boolean matchesSimpleType(String simpleTypeName, Set<String> wildcardImports) {
            // Must have a matching wildcard import
            if (!wildcardImports.contains(packageName)) {
                return false;
            }

            if (packageLevelMatch) {
                // Package-level rules don't match on simple type names
                // (we'd need to know all types in the package)
                return false;
            }

            // Check if type name matches
            if (typeNames == null) return false;
            for (String typeName : typeNames) {
                if (simpleTypeName.equals(typeName)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Check if a fully qualified type name matches this rule.
         * @param fqn The fully qualified name (e.g., "java.util.Optional")
         * @return true if this FQN triggers the feature
         */
        public boolean matchesFQN(String fqn) {
            if (packageLevelMatch) {
                return fqn.startsWith(packageName + ".") || fqn.equals(packageName);
            } else {
                if (typeNames == null) return false;
                for (String typeName : typeNames) {
                    String fullName = packageName + "." + typeName;
                    if (fqn.equals(fullName) || fqn.startsWith(fullName + ".") || fqn.startsWith(fullName + "$")) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * All type-to-feature mapping rules.
     * This is the central configuration for library feature detection.
     */
    private static final List<TypeFeatureRule> TYPE_FEATURE_RULES = List.of(
        // === Java 1.0 ===
        TypeFeatureRule.pkg("java.awt", JavaFeature.AWT),
        TypeFeatureRule.pkg("java.applet", JavaFeature.APPLET),
        TypeFeatureRule.pkg("java.io", JavaFeature.IO_API),

        // === Java 1.1 ===
        TypeFeatureRule.pkg("java.sql", JavaFeature.JDBC),
        TypeFeatureRule.pkg("javax.sql", JavaFeature.JDBC),
        TypeFeatureRule.pkg("java.rmi", JavaFeature.RMI),
        TypeFeatureRule.pkg("java.beans", JavaFeature.JAVABEANS),
        TypeFeatureRule.types("java.io", JavaFeature.SERIALIZATION,
            "Serializable", "ObjectInputStream", "ObjectOutputStream", "Externalizable"),

        // === Java 1.2 ===
        TypeFeatureRule.pkg("javax.swing", JavaFeature.SWING),
        TypeFeatureRule.types("java.util", JavaFeature.COLLECTIONS_FRAMEWORK,
            "Collection", "List", "Set", "Map", "ArrayList", "LinkedList",
            "HashSet", "TreeSet", "HashMap", "TreeMap", "LinkedHashMap", "LinkedHashSet",
            "Iterator", "Collections", "Vector", "Hashtable", "Stack", "Queue", "Deque",
            "SortedSet", "SortedMap", "NavigableSet", "NavigableMap", "AbstractList",
            "AbstractSet", "AbstractMap", "AbstractCollection"),
        TypeFeatureRule.pkg("org.omg.CORBA", JavaFeature.CORBA),
        TypeFeatureRule.pkg("org.omg.CosNaming", JavaFeature.CORBA),

        // === Java 1.3 ===
        TypeFeatureRule.pkg("javax.naming", JavaFeature.JNDI),
        TypeFeatureRule.pkg("javax.sound", JavaFeature.JAVA_SOUND),
        TypeFeatureRule.pkg("com.sun.jdi", JavaFeature.JPDA),

        // === Java 1.4 ===
        TypeFeatureRule.pkg("java.util.regex", JavaFeature.REGEX),
        TypeFeatureRule.pkg("java.nio", JavaFeature.NIO),  // Note: java.nio.file is NIO2 (Java 7)
        TypeFeatureRule.pkg("java.util.logging", JavaFeature.LOGGING),
        TypeFeatureRule.pkg("javax.xml", JavaFeature.XML_API),
        TypeFeatureRule.pkg("java.util.prefs", JavaFeature.PREFERENCES),
        TypeFeatureRule.pkg("javax.imageio", JavaFeature.IMAGE_IO),

        // === Java 5 ===
        TypeFeatureRule.pkg("java.util.concurrent", JavaFeature.CONCURRENT_API),
        TypeFeatureRule.types("java.util", JavaFeature.SCANNER, "Scanner"),

        // === Java 6 ===
        TypeFeatureRule.pkg("javax.script", JavaFeature.SCRIPTING_API),
        TypeFeatureRule.pkg("javax.tools", JavaFeature.COMPILER_API),
        TypeFeatureRule.pkg("javax.xml.bind", JavaFeature.JAXB),
        TypeFeatureRule.pkg("javax.xml.ws", JavaFeature.JAX_WS),
        TypeFeatureRule.pkg("javax.xml.stream", JavaFeature.STAX),
        TypeFeatureRule.types("javax.swing", JavaFeature.SWING_WORKER, "SwingWorker"),

        // === Java 7 ===
        TypeFeatureRule.types("java.util.concurrent", JavaFeature.FORK_JOIN,
            "ForkJoinPool", "ForkJoinTask", "ForkJoinWorkerThread",
            "RecursiveAction", "RecursiveTask", "CountedCompleter"),
        TypeFeatureRule.pkg("java.nio.file", JavaFeature.NIO2),
        TypeFeatureRule.types("java.nio.file", JavaFeature.WATCH_SERVICE,
            "WatchService", "WatchKey", "WatchEvent", "Watchable", "StandardWatchEventKinds"),

        // === Java 8 ===
        TypeFeatureRule.pkg("java.util.stream", JavaFeature.STREAM_API),
        TypeFeatureRule.types("java.util.stream", JavaFeature.STREAM_API,
            "Stream", "IntStream", "LongStream", "DoubleStream", "Collectors", "StreamSupport"),
        TypeFeatureRule.pkg("java.time", JavaFeature.DATE_TIME_API),
        TypeFeatureRule.types("java.util", JavaFeature.OPTIONAL,
            "Optional", "OptionalInt", "OptionalLong", "OptionalDouble"),
        TypeFeatureRule.types("java.util", JavaFeature.BASE64_API, "Base64"),

        // === Java 9 ===
        TypeFeatureRule.types("java.lang", JavaFeature.PROCESS_API, "ProcessHandle"),
        TypeFeatureRule.types("java.util.concurrent", JavaFeature.REACTIVE_STREAMS, "Flow"),
        TypeFeatureRule.pkg("java.util.concurrent.Flow", JavaFeature.REACTIVE_STREAMS),
        TypeFeatureRule.types("java.lang", JavaFeature.STACK_WALKING, "StackWalker"),

        // === Java 11 ===
        TypeFeatureRule.pkg("java.net.http", JavaFeature.HTTP_CLIENT),
        TypeFeatureRule.types("java.net.http", JavaFeature.HTTP_CLIENT,
            "HttpClient", "HttpRequest", "HttpResponse", "WebSocket"),
        TypeFeatureRule.pkg("jdk.jfr", JavaFeature.FLIGHT_RECORDER),

        // === Java 12 ===
        TypeFeatureRule.types("java.text", JavaFeature.COMPACT_NUMBER_FORMAT, "CompactNumberFormat"),

        // === Java 15 ===
        TypeFeatureRule.types("java.lang.invoke", JavaFeature.HIDDEN_CLASSES, "MethodHandles"),
        TypeFeatureRule.types("java.security.spec", JavaFeature.EDDSA,
            "EdECPublicKeySpec", "EdECPrivateKeySpec", "EdECPoint", "NamedParameterSpec"),
        TypeFeatureRule.types("java.security.interfaces", JavaFeature.EDDSA,
            "EdECKey", "EdECPublicKey", "EdECPrivateKey"),

        // === Java 16 ===
        TypeFeatureRule.types("java.net", JavaFeature.UNIX_DOMAIN_SOCKETS, "UnixDomainSocketAddress"),
        TypeFeatureRule.types("java.nio.channels", JavaFeature.UNIX_DOMAIN_SOCKETS, "SocketChannel", "ServerSocketChannel"),

        // === Java 17 ===
        TypeFeatureRule.pkg("java.util.random", JavaFeature.RANDOM_GENERATOR),
        TypeFeatureRule.types("java.util", JavaFeature.HEX_FORMAT, "HexFormat"),
        TypeFeatureRule.types("java.io", JavaFeature.DESERIALIZATION_FILTERS, "ObjectInputFilter"),

        // === Java 18 ===
        TypeFeatureRule.pkg("com.sun.net.httpserver", JavaFeature.SIMPLE_WEB_SERVER),
        TypeFeatureRule.pkg("java.net.spi", JavaFeature.INET_ADDRESS_RESOLVER),

        // === Java 21 ===
        TypeFeatureRule.types("java.util", JavaFeature.SEQUENCED_COLLECTIONS,
            "SequencedCollection", "SequencedSet", "SequencedMap"),
        TypeFeatureRule.types("javax.crypto", JavaFeature.KEY_ENCAPSULATION, "KEM"),
        TypeFeatureRule.pkg("javax.crypto.KEM", JavaFeature.KEY_ENCAPSULATION),

        // === Java 22 ===
        TypeFeatureRule.pkg("java.lang.foreign", JavaFeature.FOREIGN_FUNCTION_API),

        // === Java 24 ===
        TypeFeatureRule.types("java.util.stream", JavaFeature.STREAM_GATHERERS, "Gatherer", "Gatherers"),
        TypeFeatureRule.pkg("java.lang.classfile", JavaFeature.CLASS_FILE_API),
        TypeFeatureRule.pkg("javax.crypto.kem", JavaFeature.QUANTUM_RESISTANT_KEM),

        // === Java 25 ===
        TypeFeatureRule.types("java.lang", JavaFeature.SCOPED_VALUES, "ScopedValue"),
        TypeFeatureRule.pkg("javax.crypto.kdf", JavaFeature.KEY_DERIVATION_API),
        TypeFeatureRule.types("javax.crypto", JavaFeature.KEY_DERIVATION_API, "KDF")
    );

    /**
     * Index of rules by package for efficient lookup.
     */
    private static final Map<String, List<TypeFeatureRule>> RULES_BY_PACKAGE;

    static {
        Map<String, List<TypeFeatureRule>> index = new HashMap<>();
        for (TypeFeatureRule rule : TYPE_FEATURE_RULES) {
            index.computeIfAbsent(rule.packageName, k -> new ArrayList<>()).add(rule);
        }
        RULES_BY_PACKAGE = Collections.unmodifiableMap(index);
    }

    /**
     * Result of feature checking containing all detected features.
     *
     * @param file The source file that was checked
     * @param features Set of detected Java features (filtered by minVersion)
     * @param requiredJavaVersion The minimum Java version required based on detected features
     * @param minVersionConsidered The minimum version that was considered (features below this are ignored)
     */
    public record FeatureCheckResult(
            File file,
            Set<JavaFeature> features,
            int requiredJavaVersion,
            int minVersionConsidered
    ) {
        /**
         * Convenience constructor that uses default min version.
         */
        public FeatureCheckResult(File file, Set<JavaFeature> features, int requiredJavaVersion) {
            this(file, features, requiredJavaVersion, DEFAULT_MIN_VERSION);
        }

        public FeatureCheckResult {
            EnumSet<JavaFeature> copy = EnumSet.noneOf(JavaFeature.class);
            copy.addAll(features);
            features = Collections.unmodifiableSet(copy);

            // Validate that requiredJavaVersion matches the highest Java version among detected features
            // This check always runs (not just when assertions are enabled)
            int maxFeatureVersion = features.stream()
                    .mapToInt(JavaFeature::getJavaVersion)
                    .max()
                    .orElse(1);
            if (requiredJavaVersion != maxFeatureVersion) {
                throw new IllegalStateException(
                        String.format("Required Java version (%d) does not match highest feature version (%d) for file %s. Features: %s",
                                requiredJavaVersion, maxFeatureVersion, file != null ? file.getName() : "unknown",
                                features.stream().map(JavaFeature::name).collect(java.util.stream.Collectors.joining(", "))));
            }
        }

        /**
         * Get features grouped by Java version.
         */
        public Map<Integer, Set<JavaFeature>> getFeaturesByVersion() {
            Map<Integer, Set<JavaFeature>> result = new TreeMap<>();
            for (JavaFeature feature : features) {
                result.computeIfAbsent(feature.getJavaVersion(), k -> EnumSet.noneOf(JavaFeature.class))
                        .add(feature);
            }
            return result;
        }

        /**
         * Get only syntax features (excluding library/API-based features).
         */
        public Set<JavaFeature> getSyntaxFeatures() {
            EnumSet<JavaFeature> result = EnumSet.noneOf(JavaFeature.class);
            for (JavaFeature feature : features) {
                if (!feature.isLibraryFeature()) {
                    result.add(feature);
                }
            }
            return Collections.unmodifiableSet(result);
        }

        /**
         * Get only library/API-based features.
         */
        public Set<JavaFeature> getLibraryFeatures() {
            EnumSet<JavaFeature> result = EnumSet.noneOf(JavaFeature.class);
            for (JavaFeature feature : features) {
                if (feature.isLibraryFeature()) {
                    result.add(feature);
                }
            }
            return Collections.unmodifiableSet(result);
        }

        /**
         * Get the required Java version based only on syntax features (excluding library features).
         * This represents the minimum version needed for compilation.
         */
        public int getRequiredSyntaxVersion() {
            return features.stream()
                    .filter(f -> !f.isLibraryFeature())
                    .mapToInt(JavaFeature::getJavaVersion)
                    .max()
                    .orElse(8);
        }
    }

    private static final JavaParser parser = new JavaParser(
            new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE)
    );
    static {
        fixJavaValidator(parser);
    }

    /**
     * Pattern to detect yield statements in switch expressions.
     * Matches "yield" followed by whitespace (not followed by letters/digits which would make it an identifier).
     * This pattern is designed to match yield statements like "yield x;" or "yield getSomething();"
     */
    private static final Pattern YIELD_PATTERN = Pattern.compile("\\byield\\s+");

    /**
     * Pattern to detect local enum declarations inside method bodies.
     * Matches enum declarations that appear inside braces (method bodies).
     * Captures: the enum declaration including its body.
     */
    private static final Pattern LOCAL_ENUM_PATTERN = Pattern.compile(
            "(\\benum\\s+\\w+\\s*\\{[^{}]*(?:\\{[^{}]*\\}[^{}]*)*\\})"
    );

    /**
     * Result of preprocessing source code to handle unsupported syntax.
     */
    private record PreprocessResult(String processedSource, boolean hasYield, boolean hasLocalEnum) {}

    /**
     * Preprocess source code to handle syntax that JavaParser doesn't support.
     * - Replaces "yield " with "return " for JavaParser compatibility
     * - Moves local enums outside of method bodies
     *
     * @param source The original source code
     * @return PreprocessResult containing the modified source and detected features
     */
    private static PreprocessResult preprocess(String source) {
        boolean hasYield = false;
        boolean hasLocalEnum = false;

        // Step 1: Handle yield statements
        Matcher yieldMatcher = YIELD_PATTERN.matcher(source);
        if (yieldMatcher.find()) {
            hasYield = true;
            source = yieldMatcher.replaceAll("return ");
        }

        // Step 2: Handle local enums - try to detect and fix them
        // We look for enum declarations that are inside method bodies
        // A simple heuristic: if parsing fails and we find enum patterns, try to move them

        return new PreprocessResult(source, hasYield, hasLocalEnum);
    }

    /**
     * Attempt to fix local enums by moving them outside method bodies.
     * This is called when initial parsing fails.
     *
     * Strategy: Find enum declarations inside method-like contexts and move them out.
     * Transform:
     *   void method() { enum E { A, B } ... }
     * Into:
     *   void method() { ... }
     *   enum E { A, B }
     *   void __localEnumPlaceholder__() {}
     *
     * @param source The source code that failed to parse
     * @return FixResult with the fixed source and whether local enums were found
     */
    private static PreprocessResult fixLocalEnums(String source) {
        List<int[]> localEnumRanges = new ArrayList<>();
        List<String> extractedEnums = new ArrayList<>();
        int enumCounter = 0;

        // Track brace depth to identify when we're inside a method body
        int braceDepth = 0;
        int i = 0;
        int len = source.length();

        while (i < len) {
            char c = source.charAt(i);

            // Skip string literals
            if (c == '"') {
                i++;
                // Check for text block
                if (i + 1 < len && source.charAt(i) == '"' && source.charAt(i + 1) == '"') {
                    // Text block
                    i += 2;
                    while (i + 2 < len) {
                        if (source.charAt(i) == '"' && source.charAt(i + 1) == '"' && source.charAt(i + 2) == '"') {
                            i += 3;
                            break;
                        }
                        i++;
                    }
                } else {
                    // Regular string
                    while (i < len) {
                        char sc = source.charAt(i);
                        if (sc == '\\' && i + 1 < len) {
                            i += 2;
                            continue;
                        }
                        if (sc == '"') {
                            i++;
                            break;
                        }
                        i++;
                    }
                }
                continue;
            }

            // Skip character literals
            if (c == '\'') {
                i++;
                while (i < len) {
                    char sc = source.charAt(i);
                    if (sc == '\\' && i + 1 < len) {
                        i += 2;
                        continue;
                    }
                    if (sc == '\'') {
                        i++;
                        break;
                    }
                    i++;
                }
                continue;
            }

            // Skip line comments
            if (c == '/' && i + 1 < len && source.charAt(i + 1) == '/') {
                while (i < len && source.charAt(i) != '\n') {
                    i++;
                }
                continue;
            }

            // Skip block comments
            if (c == '/' && i + 1 < len && source.charAt(i + 1) == '*') {
                i += 2;
                while (i + 1 < len) {
                    if (source.charAt(i) == '*' && source.charAt(i + 1) == '/') {
                        i += 2;
                        break;
                    }
                    i++;
                }
                continue;
            }

            // Track brace depth
            if (c == '{') {
                braceDepth++;
                i++;
                continue;
            }

            if (c == '}') {
                braceDepth--;
                i++;
                continue;
            }

            // Look for 'enum' keyword when inside a method body (braceDepth >= 2)
            if (braceDepth >= 2 && c == 'e' && i + 4 <= len) {
                // Check if this is "enum" keyword
                if (source.startsWith("enum", i) &&
                    (i == 0 || !Character.isJavaIdentifierPart(source.charAt(i - 1))) &&
                    (i + 4 >= len || !Character.isJavaIdentifierPart(source.charAt(i + 4)))) {

                    // Found local enum - extract it
                    int enumStart = i;

                    // Find the enum name and opening brace
                    i += 4; // skip "enum"
                    while (i < len && Character.isWhitespace(source.charAt(i))) i++;

                    // Skip enum name
                    while (i < len && Character.isJavaIdentifierPart(source.charAt(i))) i++;

                    // Skip to opening brace (might have implements clause)
                    while (i < len && source.charAt(i) != '{') i++;

                    if (i < len) {
                        // Find matching closing brace
                        int enumBraceDepth = 0;
                        while (i < len) {
                            char ec = source.charAt(i);

                            // Skip strings in enum body
                            if (ec == '"') {
                                i++;
                                while (i < len) {
                                    if (source.charAt(i) == '\\' && i + 1 < len) {
                                        i += 2;
                                        continue;
                                    }
                                    if (source.charAt(i) == '"') {
                                        i++;
                                        break;
                                    }
                                    i++;
                                }
                                continue;
                            }

                            // Skip char literals
                            if (ec == '\'') {
                                i++;
                                while (i < len) {
                                    if (source.charAt(i) == '\\' && i + 1 < len) {
                                        i += 2;
                                        continue;
                                    }
                                    if (source.charAt(i) == '\'') {
                                        i++;
                                        break;
                                    }
                                    i++;
                                }
                                continue;
                            }

                            if (ec == '{') {
                                enumBraceDepth++;
                            } else if (ec == '}') {
                                enumBraceDepth--;
                                if (enumBraceDepth == 0) {
                                    i++; // include closing brace
                                    break;
                                }
                            }
                            i++;
                        }

                        // Record this enum's location and content
                        String enumDecl = source.substring(enumStart, i);
                        localEnumRanges.add(new int[]{enumStart, i});
                        extractedEnums.add(enumDecl + "\nvoid __localEnumPlaceholder" + enumCounter++ + "__() {}\n");
                        continue;
                    }
                }
            }

            i++;
        }

        if (localEnumRanges.isEmpty()) {
            return new PreprocessResult(source, false, false);
        }

        // Build result by removing local enums and inserting them at class level
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        // Find the first class-level closing brace to insert enums before it
        // We'll insert the extracted enums right before the final closing brace of the outermost class

        // First, build the source without the local enums
        for (int[] range : localEnumRanges) {
            result.append(source, lastEnd, range[0]);
            lastEnd = range[1];
        }
        result.append(source.substring(lastEnd));

        // Now find the last '}' and insert extracted enums before it
        String withoutEnums = result.toString();
        int lastBrace = withoutEnums.lastIndexOf('}');
        if (lastBrace > 0) {
            StringBuilder finalResult = new StringBuilder();
            finalResult.append(withoutEnums, 0, lastBrace);
            for (String extracted : extractedEnums) {
                finalResult.append("\n").append(extracted);
            }
            finalResult.append(withoutEnums.substring(lastBrace));
            return new PreprocessResult(finalResult.toString(), false, true);
        }

        return new PreprocessResult(withoutEnums, false, true);
    }

    /**
     * Default minimum Java version to consider. Features from versions below this are ignored.
     * By default, Java 5 and lower features are ignored as they're ubiquitous in modern Java code.
     */
    public static final int DEFAULT_MIN_VERSION = 6;


    /**
     * Parse a file and detect all Java language features used, considering only features
     *
     * @param file The Java source file
     * @return FeatureCheckResult containing all detected features, or null if parsing failed
     */
    public static FeatureCheckResult check(File file) throws FileNotFoundException {
        // Read file content for preprocessing
        String sourceCode;
        try {
            sourceCode = Files.readString(file.toPath());
        } catch (java.io.IOException e) {
            throw new FileNotFoundException("Could not read file: " + file.getAbsolutePath());
        }

        // Preprocess to handle yield statements (replace with return for JavaParser compatibility)
        PreprocessResult preprocessResult = preprocess(sourceCode);

        ParseResult<CompilationUnit> parseResult;
        try {
            // Parse the preprocessed source code
            parseResult = parser.parse(new java.io.StringReader(preprocessResult.processedSource()));
        } catch (StackOverflowError e) {
            return null;
        }

        // If parsing failed, try to fix local enums and retry
        if (!parseResult.isSuccessful() || parseResult.getResult().isEmpty()) {
            PreprocessResult localEnumFix = fixLocalEnums(preprocessResult.processedSource());
            if (localEnumFix.hasLocalEnum()) {
                // Retry parsing with fixed source
                try {
                    parseResult = parser.parse(new java.io.StringReader(localEnumFix.processedSource()));
                } catch (StackOverflowError e) {
                    return null;
                }
                if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                    // Update preprocessResult to include local enum detection
                    preprocessResult = new PreprocessResult(
                            localEnumFix.processedSource(),
                            preprocessResult.hasYield(),
                            true
                    );
                }
            }
        }

        if (!parseResult.isSuccessful() || parseResult.getResult().isEmpty()) {
            return null;
        }

        CompilationUnit cu = parseResult.getResult().get();
        Set<JavaFeature> features = EnumSet.noneOf(JavaFeature.class);

        // Add YIELD feature if yield statements were detected during preprocessing
        if (preprocessResult.hasYield()) {
            features.add(JavaFeature.YIELD);
        }

        // Add LOCAL_ENUMS feature if local enums were detected during preprocessing
        if (preprocessResult.hasLocalEnum()) {
            features.add(JavaFeature.LOCAL_ENUMS);
        }

        FeatureVisitor visitor = new FeatureVisitor(features::add);
        visitor.visit(cu, null);

        // Java 23: Check for Markdown documentation comments (/// style)
        // Use original source code for this check
        for (String line : sourceCode.split("\n")) {
            if (line.trim().startsWith("///")) {
                features.add(JavaFeature.MARKDOWN_DOC_COMMENTS);
                break;
            }
        }

        // Don't filter features - include all detected features regardless of minVersion
        // The minVersion parameter is now only used for setting the baseline version
        int maxVersion = features.stream()
                .mapToInt(JavaFeature::getJavaVersion)
                .max()
                .orElse(1);

        return new FeatureCheckResult(file, features, maxVersion);
    }

    /**
     * Visitor that detects Java language features.
     * Based on validators from JavaParser.
     */
    private static class FeatureVisitor extends VoidVisitorAdapter<Void> {
        private final Consumer<JavaFeature> featureConsumer;
        private final Set<String> wildcardImports = new java.util.HashSet<>();
        private final Set<String> explicitImports = new java.util.HashSet<>();
        private final Set<String> fullyQualifiedTypesUsed = new java.util.HashSet<>();

        FeatureVisitor(Consumer<JavaFeature> featureConsumer) {
            this.featureConsumer = featureConsumer;
        }

        private void addFeature(JavaFeature feature) {
            featureConsumer.accept(feature);
        }

        // ==================== Fully Qualified Name Detection ====================

        /**
         * Visit a type and record if it's a fully qualified name.
         * Also detect features from simple type names (e.g., ForkJoinPool, WatchService)
         * and generics usage.
         */
        @Override
        public void visit(com.github.javaparser.ast.type.ClassOrInterfaceType n, Void arg) {
            // Check for generics - if type has type arguments like List<String>
            if (n.getTypeArguments().isPresent()) {
                if (n.getTypeArguments().get().isEmpty()) {
                    // Diamond operator: List<>
                    addFeature(JavaFeature.DIAMOND_OPERATOR);
                } else {
                    // Explicit type arguments: List<String>
                    addFeature(JavaFeature.GENERICS);
                }
            }

            // Check if this is a fully qualified name (has scope)
            if (n.getScope().isPresent()) {
                String fullName = n.asString();
                fullyQualifiedTypesUsed.add(fullName);
                // Detect features from fully qualified type usage
                detectFeaturesFromTypeName(fullName);
            } else {
                // Simple type name - detect features from well-known types
                String typeName = n.getNameAsString();
                detectFeaturesFromSimpleTypeName(typeName);
            }
            super.visit(n, arg);
        }

        /**
         * Detect features from simple type names using the DSL rules.
         * These are types that when used (after appropriate wildcard import) indicate specific Java versions.
         * We verify that a matching wildcard import exists to avoid false positives from user-defined classes.
         */
        private void detectFeaturesFromSimpleTypeName(String typeName) {
            for (TypeFeatureRule rule : TYPE_FEATURE_RULES) {
                // Check if simple type matches with wildcard import
                if (rule.matchesSimpleType(typeName, wildcardImports)) {
                    addFeature(rule.feature);
                }

                // Special case: java.lang types don't need imports (always available)
                if (rule.packageName.equals("java.lang") && !rule.packageLevelMatch && rule.typeNames != null) {
                    for (String t : rule.typeNames) {
                        if (typeName.equals(t)) {
                            addFeature(rule.feature);
                        }
                    }
                }
            }

            // Additional detection for commonly used types with wildcard imports

            // Java 5: Concurrent API (java.util.concurrent.*)
            if (wildcardImports.contains("java.util.concurrent")) {
                switch (typeName) {
                    case "ExecutorService", "Executor", "Executors", "Future", "Callable",
                         "ThreadPoolExecutor", "ScheduledExecutorService", "ScheduledFuture",
                         "TimeUnit", "CountDownLatch", "CyclicBarrier", "Semaphore",
                         "BlockingQueue", "LinkedBlockingQueue", "ArrayBlockingQueue",
                         "ConcurrentHashMap", "ConcurrentLinkedQueue", "ConcurrentMap",
                         "CopyOnWriteArrayList", "CopyOnWriteArraySet",
                         "Exchanger", "CompletionService", "ExecutorCompletionService",
                         "CompletableFuture", "Phaser" ->
                        addFeature(JavaFeature.CONCURRENT_API);
                    case "ForkJoinPool", "ForkJoinTask", "RecursiveAction", "RecursiveTask",
                         "CountedCompleter", "ForkJoinWorkerThread" ->
                        addFeature(JavaFeature.FORK_JOIN);
                    case "Flow" ->
                        addFeature(JavaFeature.REACTIVE_STREAMS);
                }
            }

            // Java 5: Atomic classes (java.util.concurrent.atomic.*)
            if (wildcardImports.contains("java.util.concurrent.atomic")) {
                switch (typeName) {
                    case "AtomicInteger", "AtomicLong", "AtomicBoolean", "AtomicReference",
                         "AtomicIntegerArray", "AtomicLongArray", "AtomicReferenceArray",
                         "AtomicMarkableReference", "AtomicStampedReference" ->
                        addFeature(JavaFeature.CONCURRENT_API);
                }
            }

            // Java 5: Locks (java.util.concurrent.locks.*)
            if (wildcardImports.contains("java.util.concurrent.locks")) {
                switch (typeName) {
                    case "Lock", "ReentrantLock", "ReadWriteLock", "ReentrantReadWriteLock",
                         "Condition", "StampedLock", "LockSupport" ->
                        addFeature(JavaFeature.CONCURRENT_API);
                }
            }

            // Java 8: Date/Time API (java.time.*)
            if (wildcardImports.contains("java.time")) {
                switch (typeName) {
                    case "LocalDate", "LocalTime", "LocalDateTime", "Instant", "ZonedDateTime",
                         "Duration", "Period", "DateTimeFormatter", "ZoneId", "ZoneOffset",
                         "OffsetDateTime", "OffsetTime", "Year", "YearMonth", "MonthDay",
                         "DayOfWeek", "Month", "Clock", "ChronoUnit", "ChronoField" ->
                        addFeature(JavaFeature.DATE_TIME_API);
                }
            }

            // Java 8: Stream API (java.util.stream.*)
            if (wildcardImports.contains("java.util.stream")) {
                switch (typeName) {
                    case "Stream", "IntStream", "LongStream", "DoubleStream",
                         "Collector", "Collectors", "StreamSupport", "BaseStream" ->
                        addFeature(JavaFeature.STREAM_API);
                }
            }

            // Java 7: NIO2 (java.nio.file.*)
            if (wildcardImports.contains("java.nio.file")) {
                switch (typeName) {
                    case "Path", "Paths", "Files", "FileSystem", "FileSystems",
                         "FileVisitor", "SimpleFileVisitor", "FileVisitResult",
                         "StandardOpenOption", "StandardCopyOption", "LinkOption",
                         "WatchService", "WatchKey", "WatchEvent", "DirectoryStream" ->
                        addFeature(JavaFeature.NIO2);
                }
            }

            // Java 4: Regex API (java.util.regex.*)
            if (wildcardImports.contains("java.util.regex")) {
                switch (typeName) {
                    case "Pattern", "Matcher", "PatternSyntaxException" ->
                        addFeature(JavaFeature.REGEX);
                }
            }

            // Java 11: Flight Recorder (jdk.jfr.*)
            if (wildcardImports.contains("jdk.jfr")) {
                switch (typeName) {
                    case "FlightRecorder", "Event", "Recording", "RecordingState",
                         "Configuration", "EventType", "ValueDescriptor" ->
                        addFeature(JavaFeature.FLIGHT_RECORDER);
                }
            }
        }


        /**
         * Detect features from a fully qualified type name or import using the DSL rules.
         * This is the unified detection method used by both import handling and FQN detection.
         *
         * @param name The fully qualified name or import name
         * @param isWildcardImport True if this is a wildcard import (only for imports)
         */
        private void detectFeaturesFromName(String name, boolean isWildcardImport) {
            if (name == null || name.isEmpty()) return;

            // For wildcard imports, only trigger package-level features
            // (e.g., import java.lang.classfile.* should trigger CLASS_FILE_API)
            if (isWildcardImport) {
                for (TypeFeatureRule rule : TYPE_FEATURE_RULES) {
                    if (rule.packageLevelMatch && rule.packageName.equals(name)) {
                        addFeature(rule.feature);
                    }
                }
                return;
            }

            // For direct imports or FQN, check both direct import and FQN matching
            for (TypeFeatureRule rule : TYPE_FEATURE_RULES) {
                if (rule.matchesDirectImport(name) || rule.matchesFQN(name)) {
                    addFeature(rule.feature);
                }
            }
        }


        /**
         * Detect features from a fully qualified type name used in code.
         * Delegates to the unified detection method.
         */
        private void detectFeaturesFromTypeName(String typeName) {
            detectFeaturesFromName(typeName, false);
        }

        /**
         * Detect all features based on an import declaration.
         * Delegates to the unified detection method.
         */
        private void detectFeaturesFromImport(String importName, boolean isWildcard) {
            detectFeaturesFromName(importName, isWildcard);
        }

        /**
         * Extract fully qualified name from a chain of field accesses.
         * E.g., for "java.util.List" in "java.util.List.of()", returns "java.util.List"
         */
        private String extractFullyQualifiedName(Expression expr) {
            if (expr.isFieldAccessExpr()) {
                FieldAccessExpr fae = expr.asFieldAccessExpr();
                String scope = extractFullyQualifiedName(fae.getScope());
                if (scope != null) {
                    return scope + "." + fae.getNameAsString();
                }
            } else if (expr.isNameExpr()) {
                return expr.asNameExpr().getNameAsString();
            }
            return null;
        }

        /**
         * Check if a name looks like a fully qualified class name (starts with lowercase package).
         */
        private boolean looksLikeFullyQualifiedName(String name) {
            if (name == null || name.isEmpty()) return false;
            // FQN typically starts with lowercase (package) and contains dots
            // e.g., "java.util.List", "javax.swing.JFrame"
            return name.contains(".") && Character.isLowerCase(name.charAt(0));
        }

        /**
         * Visit method calls to detect:
         * - Fully qualified static method calls (e.g., java.util.List.of())
         * - Generics
         * - Virtual Threads API
         * - String API methods (Java 11-12)
         * - Stream.toList() (Java 16)
         * - Collection factory methods (Java 9)
         * - Files API methods (Java 11-12)
         */
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            // Check for fully qualified name usage
            n.getScope().ifPresent(scope -> {
                String fqn = extractFullyQualifiedName(scope);
                if (looksLikeFullyQualifiedName(fqn)) {
                    fullyQualifiedTypesUsed.add(fqn);
                    detectFeaturesFromTypeName(fqn);
                }
            });

            // Check for generics
            checkGenerics(n);

            String methodName = n.getNameAsString();

            // Java 21: Virtual Threads (JEP 444) - detect Thread.ofVirtual(), Thread.startVirtualThread()
            if (methodName.equals("ofVirtual") || methodName.equals("startVirtualThread")) {
                n.getScope().ifPresent(scope -> {
                    if (scope.toString().equals("Thread")) {
                        addFeature(JavaFeature.VIRTUAL_THREADS);
                    }
                });
            }

            // Detect API-specific method calls
            detectApiMethodCalls(n, methodName);

            // Detect API types from method call scope (e.g., Stream.of() -> Stream is the scope type)
            detectTypesFromMethodCallScope(n);

            // Detect autoboxing in method calls (e.g., asList(1, 2, 3))
            checkAutoboxingInMethodCall(n);

            super.visit(n, arg);
        }

        /**
         * Detect API-specific method calls that indicate particular Java versions.
         */
        private void detectApiMethodCalls(MethodCallExpr n, String methodName) {
            n.getScope().ifPresent(scope -> {
                String scopeStr = scope.toString();

                // === Java 9: Collection factory methods ===
                // List.of(), Set.of(), Map.of(), Map.ofEntries(), Map.entry()
                if ((scopeStr.equals("List") || scopeStr.equals("Set") || scopeStr.equals("Map")) &&
                    (methodName.equals("of") || methodName.equals("ofEntries") || methodName.equals("entry"))) {
                    addFeature(JavaFeature.COLLECTION_FACTORY_METHODS);
                }

                // === Java 10: Collection copyOf methods ===
                // List.copyOf(), Set.copyOf(), Map.copyOf()
                if ((scopeStr.equals("List") || scopeStr.equals("Set") || scopeStr.equals("Map")) &&
                    methodName.equals("copyOf")) {
                    addFeature(JavaFeature.COLLECTION_COPY_OF);
                }


                // === Java 12: Collectors.teeing() ===
                if (scopeStr.equals("Collectors") && methodName.equals("teeing")) {
                    addFeature(JavaFeature.COLLECTORS_TEEING);
                }
            });
        }

        /**
         * Detect API types used as method call scopes (e.g., Stream.of(), LocalDate.now()).
         * This uses the DSL-based type detection for consistent feature mapping.
         */
        private void detectTypesFromMethodCallScope(MethodCallExpr n) {
            n.getScope().ifPresent(scope -> {
                String scopeStr = scope.toString();

                // Detect Stream API from simple type names used as scope (e.g., Stream.of(), IntStream.range())
                if (scopeStr.equals("Stream") || scopeStr.equals("IntStream") ||
                    scopeStr.equals("LongStream") || scopeStr.equals("DoubleStream") ||
                    scopeStr.equals("Collectors") || scopeStr.equals("StreamSupport")) {
                    addFeature(JavaFeature.STREAM_API);
                }

                // Detect Date/Time API from simple type names (e.g., LocalDate.now(), Instant.now())
                if (scopeStr.equals("LocalDate") || scopeStr.equals("LocalTime") ||
                    scopeStr.equals("LocalDateTime") || scopeStr.equals("Instant") ||
                    scopeStr.equals("ZonedDateTime") || scopeStr.equals("Duration") ||
                    scopeStr.equals("Period") || scopeStr.equals("DateTimeFormatter") ||
                    scopeStr.equals("ZoneId") || scopeStr.equals("OffsetDateTime")) {
                    addFeature(JavaFeature.DATE_TIME_API);
                }

                // Detect NIO2 from simple type names (e.g., Files.readAllLines(), Paths.get())
                if (scopeStr.equals("Files") || scopeStr.equals("Paths") || scopeStr.equals("Path")) {
                    addFeature(JavaFeature.NIO2);
                }

                // Detect Flight Recorder from simple type names
                if (scopeStr.equals("FlightRecorder") || scopeStr.startsWith("jdk.jfr.")) {
                    addFeature(JavaFeature.FLIGHT_RECORDER);
                }
            });
        }

        /**
         * Visit field access to detect fully qualified field access.
         * E.g., java.lang.System.out, java.util.concurrent.TimeUnit.SECONDS
         */
        @Override
        public void visit(FieldAccessExpr n, Void arg) {
            String fqn = extractFullyQualifiedName(n.getScope());
            if (looksLikeFullyQualifiedName(fqn)) {
                fullyQualifiedTypesUsed.add(fqn);
                detectFeaturesFromTypeName(fqn);
            }
            super.visit(n, arg);
        }

        // ==================== AST Visitors ====================

        // === Java 1.1: Inner classes ===
        // Based on Java1_0Validator.noInnerClasses
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (!n.isTopLevelType()) {
                addFeature(JavaFeature.INNER_CLASSES);
            }
            // Check for generics (type parameters on class/interface declaration)
            if (n.getTypeParameters().isNonEmpty()) {
                addFeature(JavaFeature.GENERICS);
            }
            // Check for local interfaces (Java 16)
            n.getParentNode().ifPresent(p -> {
                if (p instanceof LocalClassDeclarationStmt && n.isInterface()) {
                    addFeature(JavaFeature.LOCAL_INTERFACES);
                }
            });
            // Check for sealed classes (Java 17)
            if (n.hasModifier(Modifier.Keyword.SEALED) || n.hasModifier(Modifier.Keyword.NON_SEALED)) {
                addFeature(JavaFeature.SEALED_CLASSES);
            }
            if (n.getPermittedTypes().isNonEmpty()) {
                addFeature(JavaFeature.SEALED_CLASSES);
            }
            // Check for strictfp (Java 1.2)
            if (n.hasModifier(Modifier.Keyword.STRICTFP)) {
                addFeature(JavaFeature.STRICTFP);
            }
            // Check for compact source files (Java 25)
            if (n.isCompact()) {
                addFeature(JavaFeature.COMPACT_SOURCE_FILES);
            }
            super.visit(n, arg);
        }

        // === Java 1.1: Reflection ===
        // Based on Java1_0Validator.noReflection
        @Override
        public void visit(ClassExpr n, Void arg) {
            addFeature(JavaFeature.REFLECTION);
            super.visit(n, arg);
        }

        // === Java 1.4: Assert ===
        // Based on Java1_0Validator.noAssertKeyword
        @Override
        public void visit(AssertStmt n, Void arg) {
            addFeature(JavaFeature.ASSERT);
            super.visit(n, arg);
        }

        // === Java 5: Generics ===
        // Based on Java1_0Validator.noGenerics and Java5Validator.genericsWithoutDiamondOperator
        private void checkGenerics(Node node) {
            if (node instanceof NodeWithTypeArguments) {
                var typeArgs = ((NodeWithTypeArguments<?>) node).getTypeArguments();
                if (typeArgs.isPresent()) {
                    if (typeArgs.get().isEmpty()) {
                        // Diamond operator
                        addFeature(JavaFeature.DIAMOND_OPERATOR);
                    } else {
                        addFeature(JavaFeature.GENERICS);
                    }
                }
            }
            if (node instanceof NodeWithTypeParameters) {
                if (((NodeWithTypeParameters<?>) node).getTypeParameters().isNonEmpty()) {
                    addFeature(JavaFeature.GENERICS);
                }
            }
        }

        @Override
        public void visit(ObjectCreationExpr n, Void arg) {
            checkGenerics(n);
            // Check diamond operator on the type itself (e.g., new ArrayList<>())
            if (n.getType().isUsingDiamondOperator()) {
                addFeature(JavaFeature.DIAMOND_OPERATOR);
                // Java 9: Diamond operator with anonymous classes
                if (n.getAnonymousClassBody().isPresent()) {
                    addFeature(JavaFeature.DIAMOND_WITH_ANONYMOUS);
                }
            }
            // Check for explicit type arguments (e.g., new ArrayList<String>())
            if (n.getType().getTypeArguments().isPresent() && !n.getType().getTypeArguments().get().isEmpty()) {
                addFeature(JavaFeature.GENERICS);
            }
            // Java 1.1: Anonymous inner classes
            if (n.getAnonymousClassBody().isPresent()) {
                addFeature(JavaFeature.INNER_CLASSES);
            }
            super.visit(n, arg);
        }

        // === Java 5: Enums ===
        // Based on Java1_0Validator.noEnums
        @Override
        public void visit(EnumDeclaration n, Void arg) {
            addFeature(JavaFeature.ENUMS);
            // Check for strictfp (Java 1.2)
            if (n.hasModifier(Modifier.Keyword.STRICTFP)) {
                addFeature(JavaFeature.STRICTFP);
            }
            // Check for local enums (Java 16)
            n.getParentNode().ifPresent(p -> {
                if (p instanceof LocalClassDeclarationStmt) {
                    addFeature(JavaFeature.LOCAL_ENUMS);
                }
            });
            super.visit(n, arg);
        }

        // === Java 5: Autoboxing ===
        // Detect autoboxing patterns: wrapper type = primitive literal
        @Override
        public void visit(com.github.javaparser.ast.body.VariableDeclarator n, Void arg) {
            // Check for autoboxing: Integer x = 42; Double d = 3.14; etc.
            String typeName = n.getTypeAsString();
            n.getInitializer().ifPresent(init -> {
                if (isWrapperType(typeName) && isPrimitiveLiteral(init)) {
                    addFeature(JavaFeature.AUTOBOXING);
                }
                // Also check for autoboxing in generic collections: List<Integer> = Arrays.asList(1, 2, 3)
                if (typeName.contains("List<Integer>") || typeName.contains("List<Long>") ||
                    typeName.contains("List<Double>") || typeName.contains("List<Float>") ||
                    typeName.contains("List<Short>") || typeName.contains("List<Byte>") ||
                    typeName.contains("List<Character>") || typeName.contains("List<Boolean>") ||
                    typeName.contains("Set<Integer>") || typeName.contains("Set<Long>") ||
                    typeName.contains("Collection<Integer>") || typeName.contains("Collection<Long>")) {
                    // Check if initialization involves primitive literals
                    if (init.isMethodCallExpr()) {
                        MethodCallExpr mce = init.asMethodCallExpr();
                        for (Expression argExpr : mce.getArguments()) {
                            if (isPrimitiveLiteral(argExpr)) {
                                addFeature(JavaFeature.AUTOBOXING);
                                break;
                            }
                        }
                    }
                }
            });
            super.visit(n, arg);
        }

        /**
         * Additional autoboxing detection: method calls with primitive arguments to generic methods.
         * E.g., asList(1, 2, 3) where asList expects T... (autoboxes int to Integer)
         */
        private void checkAutoboxingInMethodCall(MethodCallExpr n) {
            String methodName = n.getNameAsString();
            // Common methods that trigger autoboxing with varargs
            if (methodName.equals("asList") || methodName.equals("of") ||
                methodName.equals("add") || methodName.equals("put") ||
                methodName.equals("addAll") || methodName.equals("contains")) {
                for (Expression argExpr : n.getArguments()) {
                    if (isPrimitiveLiteral(argExpr)) {
                        addFeature(JavaFeature.AUTOBOXING);
                        return;
                    }
                }
            }
        }

        /**
         * Check if a type name is a primitive wrapper type.
         */
        private boolean isWrapperType(String typeName) {
            return typeName.equals("Integer") || typeName.equals("Long") ||
                   typeName.equals("Double") || typeName.equals("Float") ||
                   typeName.equals("Short") || typeName.equals("Byte") ||
                   typeName.equals("Character") || typeName.equals("Boolean") ||
                   typeName.equals("java.lang.Integer") || typeName.equals("java.lang.Long") ||
                   typeName.equals("java.lang.Double") || typeName.equals("java.lang.Float") ||
                   typeName.equals("java.lang.Short") || typeName.equals("java.lang.Byte") ||
                   typeName.equals("java.lang.Character") || typeName.equals("java.lang.Boolean");
        }

        /**
         * Check if an expression is a primitive literal.
         */
        private boolean isPrimitiveLiteral(Expression expr) {
            return expr.isIntegerLiteralExpr() || expr.isLongLiteralExpr() ||
                   expr.isDoubleLiteralExpr() || expr.isCharLiteralExpr() ||
                   expr.isBooleanLiteralExpr();
        }

        // === Java 5: Annotations ===
        // Based on Java1_0Validator.noAnnotations
        @Override
        public void visit(MarkerAnnotationExpr n, Void arg) {
            addFeature(JavaFeature.ANNOTATIONS);
            checkRepeatingAnnotations(n);
            checkTypeAnnotation(n);
            super.visit(n, arg);
        }

        @Override
        public void visit(NormalAnnotationExpr n, Void arg) {
            addFeature(JavaFeature.ANNOTATIONS);
            checkRepeatingAnnotations(n);
            checkTypeAnnotation(n);
            super.visit(n, arg);
        }

        @Override
        public void visit(SingleMemberAnnotationExpr n, Void arg) {
            addFeature(JavaFeature.ANNOTATIONS);
            checkRepeatingAnnotations(n);
            checkTypeAnnotation(n);
            super.visit(n, arg);
        }

        // === Java 8: Type annotations (JEP 104) ===
        // Check if annotation is on a type use (not just a declaration)
        private void checkTypeAnnotation(AnnotationExpr annotation) {
            annotation.getParentNode().ifPresent(parent -> {
                // Type annotations appear on types, type parameters, array types, etc.
                if (parent instanceof com.github.javaparser.ast.type.Type) {
                    addFeature(JavaFeature.TYPE_ANNOTATIONS);
                }
            });
        }

        // === Java 8: Repeating annotations ===
        // Check if the same annotation type appears multiple times on the same element
        private void checkRepeatingAnnotations(AnnotationExpr annotation) {
            annotation.getParentNode().ifPresent(parent -> {
                if (parent instanceof com.github.javaparser.ast.nodeTypes.NodeWithAnnotations<?> annotated) {
                    String annotationName = annotation.getNameAsString();
                    long count = annotated.getAnnotations().stream()
                            .filter(a -> a.getNameAsString().equals(annotationName))
                            .count();
                    if (count > 1) {
                        addFeature(JavaFeature.REPEATING_ANNOTATIONS);
                    }
                }
            });
        }

        @Override
        public void visit(AnnotationDeclaration n, Void arg) {
            addFeature(JavaFeature.ANNOTATIONS);
            super.visit(n, arg);
        }

        // === Java 5: Varargs ===
        // Based on Java1_0Validator.noVarargs
        @Override
        public void visit(Parameter n, Void arg) {
            if (n.isVarArgs()) {
                addFeature(JavaFeature.VARARGS);
            }
            super.visit(n, arg);
        }

        // === Java 5: For-each ===
        // Based on Java1_0Validator.noForEach
        @Override
        public void visit(ForEachStmt n, Void arg) {
            addFeature(JavaFeature.FOR_EACH);
            super.visit(n, arg);
        }

        // === Java 5: Static imports ===
        // Based on Java1_0Validator.noStaticImports
        @Override
        public void visit(ImportDeclaration n, Void arg) {
            if (n.isStatic()) {
                addFeature(JavaFeature.STATIC_IMPORT);
            }
            // Java 25: Module imports
            if (n.isModule()) {
                addFeature(JavaFeature.MODULE_IMPORTS);
            }

            String importName = n.getNameAsString();
            boolean isWildcard = n.isAsterisk();

            // Track imports for later type matching
            if (isWildcard) {
                wildcardImports.add(importName);
            } else {
                explicitImports.add(importName);
            }

            // Detect features from imports using helper methods
            detectFeaturesFromImport(importName, isWildcard);

            super.visit(n, arg);
        }


        // === Java 7/9: Try-with-resources ===
        // Based on Java1_0Validator.tryWithoutResources and Java7Validator.tryWithLimitedResources
        @Override
        public void visit(TryStmt n, Void arg) {
            if (n.getResources().isNonEmpty()) {
                addFeature(JavaFeature.TRY_WITH_RESOURCES);
                // Java 9: Check for effectively final variables in try-with-resources
                // (resources that are just variable references, not declarations)
                for (Expression resource : n.getResources()) {
                    if (resource.isNameExpr() || resource.isFieldAccessExpr()) {
                        addFeature(JavaFeature.TRY_WITH_EFFECTIVELY_FINAL);
                    }
                }
            }
            super.visit(n, arg);
        }

        // === Java 7: Multi-catch ===
        // Based on Java1_0Validator.noMultiCatch
        @Override
        public void visit(UnionType n, Void arg) {
            addFeature(JavaFeature.MULTI_CATCH);
            super.visit(n, arg);
        }

        // === Java 7: Strings in switch ===
        @Override
        public void visit(SwitchStmt n, Void arg) {
            // Check if the selector expression is a String type
            Expression selector = n.getSelector();
            if (selector.isStringLiteralExpr() || selector.isNameExpr() || selector.isMethodCallExpr()) {
                // Check entries for string literals to confirm it's a string switch
                for (SwitchEntry entry : n.getEntries()) {
                    for (Expression label : entry.getLabels()) {
                        if (label.isStringLiteralExpr()) {
                            addFeature(JavaFeature.STRINGS_IN_SWITCH);
                            break;
                        }
                    }
                }
            }
            super.visit(n, arg);
        }

        // === Java 7: Binary literals and underscores ===
        // Based on NoBinaryIntegerLiteralsValidator and NoUnderscoresInIntegerLiteralsValidator
        @Override
        public void visit(IntegerLiteralExpr n, Void arg) {
            String value = n.getValue();
            if (value.startsWith("0b") || value.startsWith("0B")) {
                addFeature(JavaFeature.BINARY_LITERALS);
            }
            if (value.contains("_")) {
                addFeature(JavaFeature.UNDERSCORES_IN_LITERALS);
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(LongLiteralExpr n, Void arg) {
            String value = n.getValue();
            if (value.startsWith("0b") || value.startsWith("0B")) {
                addFeature(JavaFeature.BINARY_LITERALS);
            }
            if (value.contains("_")) {
                addFeature(JavaFeature.UNDERSCORES_IN_LITERALS);
            }
            super.visit(n, arg);
        }

        // === Java 8: Lambdas ===
        // Based on Java1_0Validator.noLambdas
        @Override
        public void visit(LambdaExpr n, Void arg) {
            addFeature(JavaFeature.LAMBDAS);
            // Check for var in lambda parameters (Java 11)
            for (Parameter param : n.getParameters()) {
                if (param.getType() instanceof VarType) {
                    addFeature(JavaFeature.VAR_IN_LAMBDA);
                }
            }
            super.visit(n, arg);
        }

        // === Java 8: Method references ===
        @Override
        public void visit(MethodReferenceExpr n, Void arg) {
            addFeature(JavaFeature.METHOD_REFERENCES);
            super.visit(n, arg);
        }

        // === Java 8: Default and static interface methods ===
        // Based on Java8Validator.defaultMethodsInInterface
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            checkGenerics(n);
            if (n.isDefault()) {
                addFeature(JavaFeature.DEFAULT_INTERFACE_METHODS);
            }
            // Check for strictfp (Java 1.2)
            if (n.hasModifier(Modifier.Keyword.STRICTFP)) {
                addFeature(JavaFeature.STRICTFP);
            }
            // Check for static and private interface methods (Java 8/9)
            n.getParentNode().ifPresent(parent -> {
                if (parent instanceof ClassOrInterfaceDeclaration cid && cid.isInterface()) {
                    if (n.isPrivate()) {
                        addFeature(JavaFeature.PRIVATE_INTERFACE_METHODS);
                    }
                    if (n.isStatic()) {
                        addFeature(JavaFeature.STATIC_INTERFACE_METHODS);
                    }
                }
            });
            super.visit(n, arg);
        }

        // === Java 9: Modules ===
        // Based on Java1_0Validator.noModules
        @Override
        public void visit(ModuleDeclaration n, Void arg) {
            addFeature(JavaFeature.MODULES);
            super.visit(n, arg);
        }

        // === Java 10: Var (local variable type inference) ===
        // Based on Java10Validator.varOnlyOnLocalVariableDefinitionAndForAndTry
        @Override
        public void visit(VarType n, Void arg) {
            // Check if it's in a lambda parameter (Java 11) or regular var (Java 10)
            boolean inLambda = false;
            Node parent = n.getParentNode().orElse(null);
            while (parent != null) {
                if (parent instanceof LambdaExpr) {
                    inLambda = true;
                    break;
                }
                parent = parent.getParentNode().orElse(null);
            }
            if (inLambda) {
                addFeature(JavaFeature.VAR_IN_LAMBDA);
            } else {
                addFeature(JavaFeature.VAR);
            }
            super.visit(n, arg);
        }

        // === Java 14: Switch expressions and yield ===
        // Based on Java1_0Validator.noSwitchExpressions
        @Override
        public void visit(SwitchExpr n, Void arg) {
            addFeature(JavaFeature.SWITCH_EXPRESSIONS);
            super.visit(n, arg);
        }

        // === Java 14: Yield ===
        // Based on Java1_0Validator.noYield
        @Override
        public void visit(YieldStmt n, Void arg) {
            addFeature(JavaFeature.YIELD);
            super.visit(n, arg);
        }

        // === Java 15: Text blocks ===
        // Based on Java1_0Validator.noTextBlockLiteral
        @Override
        public void visit(TextBlockLiteralExpr n, Void arg) {
            addFeature(JavaFeature.TEXT_BLOCKS);
            super.visit(n, arg);
        }

        // === Java 16: Records ===
        // Based on Java1_0Validator.noRecordDeclaration
        @Override
        public void visit(RecordDeclaration n, Void arg) {
            addFeature(JavaFeature.RECORDS);
            super.visit(n, arg);
        }

        // === Java 16: Pattern matching for instanceof ===
        // Based on Java1_0Validator.noPatternMatchingInstanceOf
        @Override
        public void visit(InstanceOfExpr n, Void arg) {
            if (n.getPattern().isPresent()) {
                addFeature(JavaFeature.PATTERN_MATCHING_INSTANCEOF);
            }
            super.visit(n, arg);
        }

        // === Java 21: Record patterns ===
        // Based on Java1_0Validator.noRecordPatterns
        @Override
        public void visit(RecordPatternExpr n, Void arg) {
            addFeature(JavaFeature.RECORD_PATTERNS);
            super.visit(n, arg);
        }

        // === Java 14/21: Switch patterns and multiple labels ===
        // Based on Java1_0Validator.noSwitchPatterns, onlyOneLabelInSwitchCase, noSwitchNullDefault
        @Override
        public void visit(SwitchEntry n, Void arg) {
            // Check for multiple labels in switch case (Java 14)
            if (n.getLabels().size() > 1) {
                addFeature(JavaFeature.SWITCH_MULTIPLE_LABELS);
            }
            // Check for pattern matching in switch (guard or pattern expressions) (Java 21)
            if (n.getGuard().isPresent()) {
                addFeature(JavaFeature.SWITCH_PATTERN_MATCHING);
            }
            for (Expression label : n.getLabels()) {
                if (label.isPatternExpr() || label.isTypePatternExpr() || label.isRecordPatternExpr()) {
                    addFeature(JavaFeature.SWITCH_PATTERN_MATCHING);
                }
                // Check for null literal in switch case (Java 21)
                if (label.isNullLiteralExpr()) {
                    addFeature(JavaFeature.SWITCH_NULL_DEFAULT);
                }
            }
            // Check for switch case null, default (Java 21) - combined null/default label
            if (n.getLabels().isNonEmpty() && n.isDefault()) {
                addFeature(JavaFeature.SWITCH_NULL_DEFAULT);
            }
            super.visit(n, arg);
        }

        // === Java 22: Unnamed variables ===
        // Based on Java22Validator.unnamedVarOnlyWhereAllowedByJep456
        @Override
        public void visit(SimpleName n, Void arg) {
            if (n.getIdentifier().equals("_")) {
                addFeature(JavaFeature.UNNAMED_VARIABLES);
            }
            super.visit(n, arg);
        }

        // === Java 25: Flexible constructor bodies ===
        // Based on Java1_0Validator.explicitConstructorInvocationMustBeFirstStatement
        @Override
        public void visit(ExplicitConstructorInvocationStmt n, Void arg) {
            n.getParentNode().ifPresent(parent -> {
                if (parent instanceof BlockStmt block) {
                    int index = block.getStatements().indexOf(n);
                    if (index > 0) {
                        addFeature(JavaFeature.FLEXIBLE_CONSTRUCTOR_BODIES);
                    }
                }
            });
            super.visit(n, arg);
        }
    }

    /**
     * Main method for testing.
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            // Test all feature test files
            File featureTestDir = new File("feature_tests");
            if (featureTestDir.exists() && featureTestDir.isDirectory()) {
                File[] files = featureTestDir.listFiles((dir, name) -> name.endsWith(".java"));
                if (files != null) {
                    Arrays.sort(files);
                    System.out.println("=== Testing all feature files ===\n");
                    for (File file : files) {
                        testFeatureFile(file);
                    }
                }

                // Also test edge cases folder
                File edgeCasesDir = new File("feature_tests/edge_cases");
                if (edgeCasesDir.exists() && edgeCasesDir.isDirectory()) {
                    File[] edgeCaseFiles = edgeCasesDir.listFiles((dir, name) -> name.endsWith(".java"));
                    if (edgeCaseFiles != null && edgeCaseFiles.length > 0) {
                        Arrays.sort(edgeCaseFiles);
                        System.out.println("\n=== Testing edge case files ===\n");
                        for (File file : edgeCaseFiles) {
                            testFeatureFile(file);
                        }
                    }
                }

                // Also test tiny test files folder
                File tinyDir = new File("feature_tests/edge_cases/tiny");
                if (tinyDir.exists() && tinyDir.isDirectory()) {
                    File[] tinyFiles = tinyDir.listFiles((dir, name) -> name.endsWith(".java"));
                    if (tinyFiles != null && tinyFiles.length > 0) {
                        Arrays.sort(tinyFiles);
                        System.out.println("\n=== Testing tiny feature files ===\n");
                        for (File file : tinyFiles) {
                            testFeatureFile(file);
                        }
                    }
                }
            } else {
                // Fallback to example files
                testWithFile(new File("test2.java"));
                testWithFile(new File("test.java"));
            }
        } else {
            for (String arg : args) {
                testWithFile(new File(arg));
            }
        }
    }

    private static void testFeatureFile(File file) throws FileNotFoundException {
        // Extract expected Java version from filename
        // Supports: "Java14_..." -> 14, or "...EdgeCases_Java8.java" -> 8
        String name = file.getName();
        int parsedVersion = -1;

        // Try "JavaX_..." pattern first
        if (name.startsWith("Java")) {
            int underscoreIdx = name.indexOf('_');
            if (underscoreIdx > 4) {
                try {
                    parsedVersion = Integer.parseInt(name.substring(4, underscoreIdx));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

        // Try "..._JavaX.java" pattern (for edge case files)
        if (parsedVersion < 0) {
            int javaIdx = name.lastIndexOf("_Java");
            if (javaIdx > 0) {
                int dotIdx = name.lastIndexOf('.');
                if (dotIdx > javaIdx + 5) {
                    try {
                        parsedVersion = Integer.parseInt(name.substring(javaIdx + 5, dotIdx));
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }

        final int expectedVersion = parsedVersion;

        FeatureCheckResult result = check(file);

        if (result == null) {
            System.out.println(file.getName() + ": PARSE FAILED");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-45s -> Java %2d (syntax: %2d)",
                file.getName(), result.requiredJavaVersion(), result.getRequiredSyntaxVersion()));

        if (expectedVersion > 0) {
            boolean matches = result.requiredJavaVersion() == expectedVersion ||
                    result.features().stream().anyMatch(f -> f.getJavaVersion() == expectedVersion);
            sb.append(matches ? " ✓" : " ✗ (expected " + expectedVersion + ")");
        }

        sb.append("  [");
        boolean first = true;
        for (JavaFeature feature : result.features()) {
            if (!first) sb.append(", ");
            sb.append(feature.name());
            if (feature.isLibraryFeature()) {
                sb.append("(API)");
            }
            first = false;
        }
        sb.append("]");

        System.out.println(sb);
    }

    private static void testWithFile(File file) throws FileNotFoundException {
        System.out.println("\n=== Checking: " + file.getName() + " ===");
        if (!file.exists()) {
            System.out.println("File not found: " + file);
            return;
        }

        FeatureCheckResult result = check(file);
        if (result == null) {
            System.out.println("Failed to parse file");
            return;
        }

        System.out.println("Required Java version: " + result.requiredJavaVersion());
        System.out.println("Required Java version (syntax only): " + result.getRequiredSyntaxVersion());

        System.out.println("\nSyntax features (" + result.getSyntaxFeatures().size() + "):");
        for (JavaFeature feature : result.getSyntaxFeatures()) {
            System.out.println("  " + feature);
        }

        if (!result.getLibraryFeatures().isEmpty()) {
            System.out.println("\nLibrary/API features (" + result.getLibraryFeatures().size() + "):");
            for (JavaFeature feature : result.getLibraryFeatures()) {
                System.out.println("  " + feature);
            }
        }
    }
}