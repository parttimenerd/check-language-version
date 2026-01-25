package me.bechberger.check.model;

import java.util.List;

/**
 * Model for a parsable file with Java version information and detected features
 */
public record FileInfo(int lines, int java, List<String> features) {
}