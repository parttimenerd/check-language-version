### Summary


Adds the Fork/Join framework via [`ForkJoinPool`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ForkJoinPool.html).

### Details

The Fork/Join framework supports parallel execution of tasks that can be recursively split into smaller subtasks.

It is designed for divide-and-conquer algorithms and is optimized around work-stealing. The main pieces are:

- [`ForkJoinPool`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ForkJoinPool.html) as the execution engine
- Task types like [`RecursiveTask<V>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/RecursiveTask.html) and [`RecursiveAction`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/RecursiveAction.html)

Tasks typically split work by calling [`ForkJoinTask.fork()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ForkJoinTask.html#fork()) and then combining results via `join()`.

### Example

```java
// Compute sum of an int array in parallel using RecursiveTask
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class Example {
    static class Sum extends RecursiveTask<Long> {
        private final int[] a;
        private final int lo, hi;

        Sum(int[] a, int lo, int hi) {
            this.a = a;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Long compute() {
            if (hi - lo <= 1_000) {
                long s = 0;
                for (int i = lo; i < hi; i++) s += a[i];
                return s; // base case
            }
            int mid = (lo + hi) >>> 1;
            Sum left = new Sum(a, lo, mid);
            Sum right = new Sum(a, mid, hi);
            left.fork();              // run left asynchronously
            long r = right.compute(); // compute right directly
            return left.join() + r;   // combine results
        }
    }

    long demo(int[] a) {
        return ForkJoinPool.commonPool().invoke(new Sum(a, 0, a.length)); // run in common pool
    }
}
```

### Historical

Introduced in Java 7 as part of the `java.util.concurrent` evolution (commonly associated with the JSR 166y updates).

### Links

- [ForkJoinPool (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ForkJoinPool.html)
- [RecursiveTask (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/RecursiveTask.html)
- [ForkJoinTask (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ForkJoinTask.html)