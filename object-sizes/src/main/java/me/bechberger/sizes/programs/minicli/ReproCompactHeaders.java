package me.bechberger.sizes.programs.minicli;

/*
 * Tiny program used by MiniCli repro tests: has a different-scope boolean option in Main.
 * Not used by the object-size runner.
 */
class ReproCompactHeaders {
    Object value = new Object();
}