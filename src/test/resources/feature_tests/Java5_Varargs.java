// Java 5 feature: Varargs
// Expected Version: 5
// Required Features: VARARGS
public class Java5_Varargs {
    public void printAll(String... messages) {
        for (String msg : messages) {
            System.out.println(msg);
        }
    }

    public int sum(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }
}