// Java 6 feature: SwingWorker API (but file also uses Java 8 lambdas)
// Expected Version: 8
// Required Features: ANNOTATIONS, COLLECTIONS_FRAMEWORK, CONCURRENT_API, FOR_EACH, GENERICS, INNER_CLASSES, LAMBDAS, MULTI_CATCH, SWING, SWING_WORKER
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JFrame;
import java.util.List;
import java.util.concurrent.ExecutionException;

class Java6_SwingWorker {

    public void testSwingWorker() {
        // SwingWorker<Result type, Intermediate type>
        SwingWorker<String, Integer> worker = new SwingWorker<String, Integer>() {

            @Override
            protected String doInBackground() throws Exception {
                // This runs in background thread
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(100);
                    publish(i);  // Send intermediate results
                    setProgress(i);  // Update progress property
                }
                return "Done!";
            }

            @Override
            protected void process(List<Integer> chunks) {
                // This runs on EDT - update UI with intermediate results
                for (Integer progress : chunks) {
                    System.out.println("Progress: " + progress + "%");
                }
            }

            @Override
            protected void done() {
                // This runs on EDT when doInBackground completes
                try {
                    String result = get();
                    System.out.println("Result: " + result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        // Execute the worker
        worker.execute();

        // Can also add property change listener for progress
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                System.out.println("Property progress: " + evt.getNewValue() + "%");
            }
        });
    }
}