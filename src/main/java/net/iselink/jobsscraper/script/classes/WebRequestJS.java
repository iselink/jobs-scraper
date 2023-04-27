package net.iselink.jobsscraper.script.classes;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * JS implementation of WebRequest for sending data across web.
 * Implements also functions for quick & easy use.
 */
public class WebRequestJS extends ScriptableObject {

	private int statusCode = 0;
	private HttpClient client = HttpClient.newHttpClient();
	private String response = null;

	@Override
	public String getClassName() {
		return "WebRequest";
	}

	@JSFunction("sendGet")
	public void sendGet(String address) throws IOException, URISyntaxException, InterruptedException {
		sendRequest("GET", address, null);
	}

	@JSFunction("sendPost")
	public void sendPost(String address, String content) throws IOException, URISyntaxException, InterruptedException {
		sendRequest("POST", address, content);
	}


	@JSFunction("send")
	public void sendPost(String address, String method, String content) throws IOException, URISyntaxException, InterruptedException {
		sendRequest(method, address, content);
	}

	private void sendRequest(String method, String address, String body) throws URISyntaxException, IOException, InterruptedException {
		HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(address));
		builder.version(HttpClient.Version.HTTP_2);

		method = method.toUpperCase();

		HttpRequest.BodyPublisher publisher;
		if (body == null || method.equalsIgnoreCase("HEAD") || method.equalsIgnoreCase("GET")) {
			publisher = HttpRequest.BodyPublishers.noBody();
		} else {
			publisher = HttpRequest.BodyPublishers.ofString(body);
		}
		builder.method(method, publisher);

		HttpRequest request = builder.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		this.response = response.body();
		statusCode = response.statusCode();
	}

	@JSGetter("status_code")
	public int getStatusCode() {
		return this.statusCode;
	}

	@JSGetter("response")
	public String getResponse() {
		return this.response;
	}


}
