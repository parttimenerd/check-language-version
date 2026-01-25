// Test: WatchService (Java 7)
// Expected Version: 7
// Required Features: NIO2, WATCH_SERVICE
import java.nio.file.*;
public class Tiny_WatchService_Java7 {
    public void test() throws Exception {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get("/tmp");
        dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
    }
}