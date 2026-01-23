package me.bechberger.check.model;

import java.util.Map;

/**
 * Root model for JSON output
 */
public record JsonOutput(
        Map<String, FileInfo> files,
        Map<String, UnparsableFileInfo> unparsable_files
) {
}