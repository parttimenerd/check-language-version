// Java 7 feature: ForkJoin Framework
// Expected Version: 7
// Required Features: ALPHA3_ARRAY_SYNTAX, ANNOTATIONS, CONCURRENT_API, FORK_JOIN, GENERICS, INNER_CLASSES
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Java7_ForkJoin {
    public void testForkJoin() {
        ForkJoinPool pool = ForkJoinPool.commonPool();

        // Create a recursive task
        RecursiveTask<Long> task = new RecursiveTask<Long>() {
            @Override
            protected Long compute() {
                return 42L;
            }
        };

        // Execute the task
        Long result = pool.invoke(task);
        System.out.println("Result: " + result);

        // Check pool parallelism
        System.out.println("Parallelism: " + pool.getParallelism());
    }

    // Example of a more complex recursive task
    static class SumTask extends RecursiveTask<Long> {
        private final long[] numbers;
        private final int start;
        private final int end;
        private static final int THRESHOLD = 1000;

        SumTask(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += numbers[i];
                }
                return sum;
            }
            int mid = start + length / 2;
            SumTask left = new SumTask(numbers, start, mid);
            SumTask right = new SumTask(numbers, mid, end);
            left.fork();
            long rightResult = right.compute();
            long leftResult = left.join();
            return leftResult + rightResult;
        }
    }
}