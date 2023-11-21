package nl.tiesvandeven;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tiesvandeven.models.InitialResult;
import nl.tiesvandeven.models.InitialResults;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Client {

	private final String baseUrl;

	public Client(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public List<InitialResult> doInitialCall() {
		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpRequest request = HttpRequest.newBuilder()
							.uri(URI.create(baseUrl + "/testurl"))
							.GET()
							.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(response.body(), InitialResults.class).results();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String doSecondaryCall(String name){
		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpRequest request = HttpRequest.newBuilder()
							.uri(URI.create(baseUrl + "/testurl2?name=" + name))
							.GET()
							.build();
			return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
