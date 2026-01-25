// Java 7 feature: Java7_StringsInSwitch
// Test: Java7_StringsInSwitch
// Expected Version: 7
// Required Features: STRINGS_IN_SWITCH
/**
 * Test file for Java 7 Strings in switch feature.
 */
public class Java7_StringsInSwitch {
    public void testStringsInSwitch(String day) {
        switch (day) {
            case "Monday":
                System.out.println("Start of week");
                break;
            case "Friday":
                System.out.println("End of week");
                break;
            case "Saturday":
            case "Sunday":
                System.out.println("Weekend!");
                break;
            default:
                System.out.println("Midweek");
        }
    }
}