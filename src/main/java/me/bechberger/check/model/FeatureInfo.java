package me.bechberger.check.model;

/**
 * Model for a detected Java feature with its name, human-readable label, and Java version
 */
public record FeatureInfo(String label, int java) {
}