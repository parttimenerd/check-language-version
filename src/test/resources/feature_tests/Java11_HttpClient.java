// Java 11 feature: HTTP Client API (JEP 321)
// Expected Version: 11
// Required Features: GENERICS, HTTP_CLIENT
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
class Java11_HttpClient {
    public void method() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://example.com"))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}