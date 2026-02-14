package me.bechberger.check;

import me.bechberger.check.model.FileInfo;
import me.bechberger.check.model.JsonOutput;
import me.bechberger.check.model.UnparsableFileInfo;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

@Command(name = "summary",
         mixinStandardHelpOptions = true,
         description = "Show a summary table of Java versions from JSON output files")
public class SummaryCommand implements Callable<Integer> {

    @Parameters(index = "0..*",
                description = "JSON files to process")
    private List<File> jsonFiles = new ArrayList<>();

    @Override
    public Integer call() {
        if (jsonFiles.isEmpty()) {
            System.err.println("Error: No JSON files specified");
            return 1;
        }

        // Track version statistics across all JSON files
        Map<Integer, VersionStats> versionStatsMap = new TreeMap<>();
        int unparsableCount = 0;
        int unparsableLines = 0;

        ObjectMapper mapper = new ObjectMapper();

        for (File jsonFile : jsonFiles) {
            if (!jsonFile.exists()) {
                System.err.println("Error: File not found: " + jsonFile);
                return 1;
            }

            try {
                JsonOutput output = mapper.readValue(jsonFile, JsonOutput.class);

                // Process parsable files
                for (Map.Entry<String, FileInfo> entry : output.files().entrySet()) {
                    FileInfo fileInfo = entry.getValue();
                    int version = fileInfo.java();
                    VersionStats stats = versionStatsMap.computeIfAbsent(version, k -> new VersionStats());
                    stats.fileCount++;
                    stats.lineCount += fileInfo.lines();
                }

                // Count unparsable files and their lines
                unparsableCount += output.unparsable_files().size();
                unparsableLines += output.unparsable_files().values().stream()
                        .mapToInt(UnparsableFileInfo::lines)
                        .sum();

            } catch (Exception e) {
                System.err.println("Error reading JSON file " + jsonFile + ": " + e.getMessage());
                return 1;
            }
        }

        // Calculate totals
        int totalParsableFiles = versionStatsMap.values().stream()
                .mapToInt(s -> s.fileCount)
                .sum();
        int totalLines = versionStatsMap.values().stream()
                .mapToInt(s -> s.lineCount)
                .sum();

        // Print table
        printSummaryTable(versionStatsMap, totalParsableFiles, totalLines);

        // Print unparsable files info
        if (unparsableCount > 0) {
            System.out.println("\nFiles that could not be parsed: " + unparsableCount + " (" + unparsableLines + " lines)");
        }

        // Print version thresholds
        printVersionThresholds(versionStatsMap, totalParsableFiles, totalLines, List.of(90.0, 95.0, 99.0, 100.0));

        return 0;
    }

    private void printVersionThresholds(Map<Integer, VersionStats> versionStatsMap, int totalFiles, int totalLines, List<Double> percentiles) {
        if (versionStatsMap.isEmpty()) {
            return;
        }

        System.out.println("\nJava version required for:");

        // Calculate cumulative statistics and track versions for each percentile
        Map<Double, Integer> fileVersions = new LinkedHashMap<>();
        Map<Double, Integer> lineVersions = new LinkedHashMap<>();

        // Initialize maps
        for (Double percentile : percentiles) {
            fileVersions.put(percentile, null);
            lineVersions.put(percentile, null);
        }

        int cumulativeFiles = 0;
        int cumulativeLines = 0;

        for (Map.Entry<Integer, VersionStats> entry : versionStatsMap.entrySet()) {
            int version = entry.getKey();
            VersionStats stats = entry.getValue();

            cumulativeFiles += stats.fileCount;
            cumulativeLines += stats.lineCount;

            double filePercentage = totalFiles > 0 ? (cumulativeFiles * 100.0 / totalFiles) : 0.0;
            double linePercentage = totalLines > 0 ? (cumulativeLines * 100.0 / totalLines) : 0.0;

            // Track thresholds for each percentile
            for (Double percentile : percentiles) {
                if (fileVersions.get(percentile) == null && filePercentage >= percentile) {
                    fileVersions.put(percentile, version);
                }
                if (lineVersions.get(percentile) == null && linePercentage >= percentile) {
                    lineVersions.put(percentile, version);
                }
            }
        }

        // Get the maximum version (for 100% fallback)
        Integer maxVersion = versionStatsMap.isEmpty() ? null : versionStatsMap.keySet().stream().max(Integer::compare).get();

        // Print table header
        System.out.printf(Locale.US, "%-10s | %-10s | %-10s%n", "%", "Files", "Lines");
        System.out.println("-".repeat(37));

        // Print table rows
        for (Double percentile : percentiles) {
            Integer fileVersion = fileVersions.get(percentile);
            Integer lineVersion = lineVersions.get(percentile);

            // Fallback to max version if not found
            if (fileVersion == null) {
                fileVersion = maxVersion;
            }
            if (lineVersion == null) {
                lineVersion = maxVersion;
            }

            String percentileStr = percentile == 100.0 ? "100%" : String.format(Locale.US, "%.0f%%", percentile);
            System.out.printf(Locale.US, "%-10s | %-10s | %-10s%n",
                    percentileStr,
                    "Java " + fileVersion,
                    "Java " + lineVersion);
        }
    }

    private void printSummaryTable(Map<Integer, VersionStats> versionStatsMap, int totalFiles, int totalLines) {
        // Print header
        System.out.printf(Locale.US, "%-10s | %10s | %10s | %10s | %10s%n",
                "Version", "Files", "% Files", "Lines", "% Lines");
        System.out.println("-".repeat(67));

        // Print rows for each version
        for (Map.Entry<Integer, VersionStats> entry : versionStatsMap.entrySet()) {
            int version = entry.getKey();
            VersionStats stats = entry.getValue();
            double filePercentage = totalFiles > 0 ? (stats.fileCount * 100.0 / totalFiles) : 0.0;
            double linePercentage = totalLines > 0 ? (stats.lineCount * 100.0 / totalLines) : 0.0;

            System.out.printf(Locale.US, "%-10d | %10d | %9.2f%% | %10d | %9.2f%%%n",
                    version,
                    stats.fileCount,
                    filePercentage,
                    stats.lineCount,
                    linePercentage);
        }

        // Print totals
        System.out.println("-".repeat(67));
        System.out.printf(Locale.US, "%-10s | %10d | %9s%% | %10d | %9s%%%n",
                "Total", totalFiles, "100.00", totalLines, "100.00");
    }

    private static class VersionStats {
        int fileCount = 0;
        int lineCount = 0;
    }
}