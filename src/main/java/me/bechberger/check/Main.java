package me.bechberger.check;

import me.bechberger.check.model.FileInfo;
import me.bechberger.check.model.JsonOutput;
import me.bechberger.check.model.UnparsableFileInfo;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Command(name = "check-language-version",
         mixinStandardHelpOptions = true,
         version = "1.0",
         description = "Check the minimum Java language version required for Java source files",
         subcommands = {SummaryCommand.class})
public class Main implements Callable<Integer> {

    @Parameters(index = "0..*",
                description = "Java source files or directories to check")
    private List<File> files = new ArrayList<>();

    @Option(names = {"-v", "--verbose"},
            description = "Show verbose output")
    private boolean verbose = false;

    @Option(names = {"-s", "--summary"},
            description = "Show summary of all files")
    private boolean summary = false;

    @Option(names = {"-j", "--json"},
            description = "Output results in JSON format")
    private boolean json = false;

    @Option(names = {"-i", "--ignore-errors"},
            description = "Ignore files with parsing errors (do not log them)")
    private boolean ignoreErrors = false;

    @Option(names = {"--include-alpha-features"},
            description = "Include Java 1.0 alpha (-2/-1) features in JSON output")
    private boolean includeAlphaFeatures = false;

    @Override
    public Integer call() throws Exception {
        if (files.isEmpty()) {
            System.err.println("Error: No files specified");
            return 1;
        }

        List<File> javaFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                javaFiles.addAll(collectJavaFiles(file));
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }

        if (javaFiles.isEmpty()) {
            System.err.println("Error: No Java files found");
            return 1;
        }

        Map<Integer, Integer> versionCounts = new TreeMap<>();
        int maxVersion = 0;
        int errorCount = 0;

        // For JSON mode: store file results
        Map<String, FileResult> parsableFiles = new LinkedHashMap<>();
        Map<String, FileResult> unparsableFiles = new LinkedHashMap<>();

        for (File file : javaFiles) {
            try {
                int lineCount = countLines(file);
                String filePath = file.getPath();

                // Use FeatureChecker for detailed feature detection
                FeatureChecker.FeatureCheckResult checkResult = FeatureChecker.check(file);

                if (checkResult == null) {
                    if (!json && !ignoreErrors) {
                        System.err.println("Error parsing: " + file);
                    }
                    unparsableFiles.put(filePath, new FileResult(lineCount, null, null));
                    errorCount++;
                } else {
                    int version = checkResult.requiredJavaVersion();
                    List<String> features = checkResult.features().stream()
                        .filter(f -> includeAlphaFeatures || f.getJavaVersion() >= 0)
                        .map(FeatureChecker.JavaFeature::name)
                        .sorted()
                        .toList();

                    if (verbose || (!summary && !json)) {
                        System.out.println(file + ", " + version + ", " + lineCount);
                    }
                    parsableFiles.put(filePath, new FileResult(lineCount, version, features));
                    versionCounts.merge(version, 1, Integer::sum);
                    maxVersion = Math.max(maxVersion, version);
                }
            } catch (Exception e) {
                if (!json && !ignoreErrors) {
                    System.err.println("Error processing " + file + ": " + e.getMessage());
                    if (verbose) {
                        e.printStackTrace();
                    }
                }
                unparsableFiles.put(file.getPath(), new FileResult(0, null, null));
                errorCount++;
            }
        }

        if (json) {
            printJsonOutput(parsableFiles, unparsableFiles);
        } else if (summary) {
            System.out.println("\n=== Summary ===");
            System.out.println("Total files processed: " + javaFiles.size());
            System.out.println("Files with errors: " + errorCount);
            System.out.println("\nVersion distribution:");
            for (Map.Entry<Integer, Integer> entry : versionCounts.entrySet()) {
                System.out.println("  Java " + entry.getKey() + ": " + entry.getValue() + " file(s)");
            }
            if (maxVersion > 0) {
                System.out.println("\nMinimum required Java version: " + maxVersion);
            }
        }

        return 0;
    }

    private void printJsonOutput(Map<String, FileResult> parsableFiles, Map<String, FileResult> unparsableFiles) {
        // Create feature labels map (enum name -> FeatureInfo with label and Java version)
        Map<String, me.bechberger.check.model.FeatureInfo> featureLabels = new LinkedHashMap<>();
        for (FeatureChecker.JavaFeature feature : FeatureChecker.JavaFeature.values()) {
            if (!includeAlphaFeatures && feature.getJavaVersion() < 0) {
                continue;
            }
            featureLabels.put(feature.name(),
                new me.bechberger.check.model.FeatureInfo(FeatureMarkdownDescriptions.markdown(feature), feature.getJavaVersion()));
        }

        Map<String, FileInfo> filesMap = new LinkedHashMap<>();
        Map<String, UnparsableFileInfo> unparsableMap = new LinkedHashMap<>();

        // Convert parsableFiles
        for (Map.Entry<String, FileResult> entry : parsableFiles.entrySet()) {
            filesMap.put(entry.getKey(),
                new FileInfo(entry.getValue().lines, entry.getValue().javaVersion, entry.getValue().features));
        }

        // Convert unparsableFiles
        for (Map.Entry<String, FileResult> entry : unparsableFiles.entrySet()) {
            unparsableMap.put(entry.getKey(),
                new UnparsableFileInfo(entry.getValue().lines));
        }

        JsonOutput output = new JsonOutput(featureLabels, filesMap, unparsableMap);

        ObjectMapper mapper = JsonMapper.builder()
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .build();
        System.out.println(mapper.writeValueAsString(output));
    }

    private record FileResult(int lines, Integer javaVersion, List<String> features) {
    }


    private List<File> collectJavaFiles(File dir) throws IOException {
        List<File> result = new ArrayList<>();
        if (!dir.isDirectory()) {
            return result;
        }

        try (Stream<Path> paths = Files.walk(dir.toPath())) {
            paths.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java"))
                 .forEach(p -> result.add(p.toFile()));
        }

        return result;
    }

    private static int countLines(File file) throws IOException {
        return Files.readAllLines(file.toPath()).size();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}