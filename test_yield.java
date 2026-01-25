public class TestYield {
    public static void main(String[] args) {
        String day = "MONDAY";
        int numLetters = switch (day) {
            case "MONDAY", "FRIDAY", "SUNDAY" -> 6;
            case "TUESDAY" -> {
                System.out.println("Tuesday");
                yield 7;
            }
            case "THURSDAY", "SATURDAY" -> 8;
            case "WEDNESDAY" -> {
                int len = day.length();
                yield len;
            }
            default -> throw new IllegalStateException("Invalid day: " + day);
        };
        System.out.println(numLetters);
    }
}