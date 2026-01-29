// Java 25 feature: Scoped Values (JEP 506)
// Expected Version: 25
// Required Features: GENERICS, LAMBDAS, SCOPED_VALUES
// Scoped Values enable sharing of immutable data within and across threads
import java.lang.ScopedValue;

class Java25_ScopedValues {
    // Declare a scoped value
    private static final ScopedValue<String> USER = ScopedValue.newInstance();
    private static final ScopedValue<Integer> REQUEST_ID = ScopedValue.newInstance();

    public void testScopedValues() {
        // Run with a bound value
        ScopedValue.where(USER, "Alice")
            .where(REQUEST_ID, 12345)
            .run(() -> {
                processRequest();
            });
    }

    private void processRequest() {
        // Access the scoped value
        String user = USER.get();
        int requestId = REQUEST_ID.get();

        System.out.println("Processing request " + requestId + " for user " + user);

        // Scoped values are automatically available to child operations
        validateUser();
        performAction();
    }

    private void validateUser() {
        // Can access scoped value without passing parameters
        if (USER.isBound()) {
            System.out.println("Validating user: " + USER.get());
        }
    }

    private void performAction() {
        // Use orElse for default values
        String user = USER.orElse("anonymous");
        System.out.println("Action performed by: " + user);
    }

    public String callWithResult() {
        // Use call() for operations that return a value
        return ScopedValue.where(USER, "Bob")
            .call(() -> "Hello, " + USER.get());
    }
}