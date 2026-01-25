// Java 16 feature: Local interfaces
// Expected Version: 16
// Required Features: INNER_CLASSES, LAMBDAS, LOCAL_INTERFACES, RECORDS
public class Java16_LocalInterfaces {
    public void method() {
        // Local interface (allowed since Java 16)
        interface Printer {
            void print(String message);
        }

        // Local record
        record Item(String name, int quantity) {}

        Printer consolePrinter = message -> System.out.println(message);
        consolePrinter.print("Hello from local interface!");

        Item item = new Item("Widget", 5);
        System.out.println(item);
    }
}