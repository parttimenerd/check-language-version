// Edge case: HTTP Client API usage (Java 11)
// Expected Version: 11
// Required Features: HTTP_CLIENT
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientEdgeCases_Java11 {

    // Java 11: Create HttpClient
    public void testCreateClient() {
        HttpClient client = HttpClient.newHttpClient();

        HttpClient customClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    // Java 11: Create HttpRequest
    public void testCreateRequest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/data"))
            .GET()
            .build();
    }

    // Java 11: Send request
    public void testSendRequest() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/data"))
            .build();

        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        String body = response.body();
    }
}