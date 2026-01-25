// Java 7 feature: Try-with-resources
// Expected Version: 7
// Required Features: TRY_WITH_RESOURCES
import java.io.*;

public class Java7_TryWithResources {
    public String readFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.readLine();
        }
    }

    public void copyFile(String from, String to) throws IOException {
        try (InputStream in = new FileInputStream(from);
             OutputStream out = new FileOutputStream(to)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }
}