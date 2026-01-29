// Java 5 feature: Enumerations
// Expected Version: 5
// Required Features: ENUMS
class Java5_Enums {
    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    public Day getDay() {
        return Day.MONDAY;
    }
}