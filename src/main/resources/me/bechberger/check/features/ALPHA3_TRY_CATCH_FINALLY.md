### Summary

Adds the combined `try`/`catch`/`finally` statement.

### Details

HotJava/Java 1.0-alpha3 introduced the ability to use both `catch` and `finally` in a single `try` statement.

This matters because it makes exception handling and cleanup composable: `catch` handles exceptions, and `finally` performs cleanup regardless of normal completion or exceptional control flow.

### Links

- [HotJava 1.0 alpha3 changes (language changes: combined try/catch/finally)](https://github.com/Marcono1234/HotJava-1.0-alpha/blob/master/hj-alpha3/hotjava/doc/misc/changes.html)