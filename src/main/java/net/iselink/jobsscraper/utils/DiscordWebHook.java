package net.iselink.jobsscraper.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class DiscordWebHook {

	private String content = null;
	private Embed[] embeds = null;

	public DiscordWebHook(String content) {
		this.content = content;
	}

	public DiscordWebHook(Embed... embeds) {
		this.embeds = embeds;
	}

	public void send(String url) throws IOException, InterruptedException, URISyntaxException {
		String payloadString = new GsonBuilder()
				.create()
				.toJson(new Payload(content, embeds));
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest.Builder request = HttpRequest.newBuilder();
		request.POST(HttpRequest.BodyPublishers.ofString(payloadString));
		request.header("Content-Type", "application/json");
		request.uri(new URI(url));
		client.send(request.build(), HttpResponse.BodyHandlers.discarding());
	}

	public static class Embed {
		@Expose
		@SerializedName("title")
		private String title = null;

		@Expose
		@SerializedName("type")
		private String type = "rich";

		@Expose
		@SerializedName("description")
		private String description = null;

		@Expose
		@SerializedName("url")
		private String url = null;

		@Expose
		@SerializedName("color")
		private Integer color = null;

		@Expose
		@SerializedName("fields")
		private Field[] fields = null;

		public Embed(String description) {
			this.description = description;
		}

		public Embed(String title, String description, Field[] fields) {
			this.title = title;
			this.description = description;
			this.fields = fields;
		}

		public Embed(String title, String description, String url, Integer color, Field[] fields) {
			this.title = title;
			this.description = description;
			this.url = url;
			this.color = color;
			this.fields = fields;
		}

		public String getTitle() {
			return title;
		}

		public Embed setTitle(String title) {
			this.title = title;
			return this;
		}

		public String getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public Embed setDescription(String description) {
			this.description = description;
			return this;
		}

		public String getUrl() {
			return url;
		}

		public Embed setUrl(String url) {
			this.url = url;
			return this;
		}


		public Integer getColor() {
			return color;
		}

		public Embed setColor(Integer color) {
			this.color = color;
			return this;
		}

		public Field[] getFields() {
			return fields;
		}

		public Embed setFields(Field[] fields) {
			this.fields = fields;
			return this;
		}
	}

	public static class Field {
		@Expose
		@SerializedName("name")
		private String name = null;

		@Expose
		@SerializedName("value")
		private String value = null;

		@Expose
		@SerializedName("inline")
		private boolean inline = false;

		public Field(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public Field(String name, String value, boolean inline) {
			this.name = name;
			this.value = value;
			this.inline = inline;
		}

		public String getName() {
			return name;
		}

		public Field setName(String name) {
			this.name = name;
			return this;
		}

		public String getValue() {
			return value;
		}

		public Field setValue(String value) {
			this.value = value;
			return this;
		}

		public boolean isInline() {
			return inline;
		}

		public Field setInline(boolean inline) {
			this.inline = inline;
			return this;
		}
	}

	private static class Payload {
		@Expose
		@SerializedName("content")
		private String content = null;

		@Expose
		@SerializedName("embeds")
		private Embed[] embeds = null;

		public Payload(String content, Embed[] embeds) {
			this.content = content;
			this.embeds = embeds;
		}
	}
}
