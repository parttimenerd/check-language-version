// Java 6 feature: java.io.Console
// Expected Version: 6
// Required Features: CONSOLE, IO_API

import java.io.Console;

class Java23_IO_ExplicitImport {
    public void demo() {
        Console console = System.console();
        if (console != null) {
            console.printf("hello\n");
        }
    }
}