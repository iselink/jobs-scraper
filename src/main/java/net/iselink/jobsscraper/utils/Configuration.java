package net.iselink.jobsscraper.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Configuration {

	@Expose
	@SerializedName("scraper")
	private Scraper scraper = null;

	@Expose
	@SerializedName("webhook")
	private String webhookAddress = null;

	public static Configuration loadConfig(String filename) throws IOException {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		try (FileReader reader = new FileReader(filename, StandardCharsets.UTF_8)) {
			return gson.fromJson(reader, Configuration.class);
		}
	}

	public Scraper getScraper() {
		return scraper;
	}

	public String getWebhookAddress() {
		return webhookAddress;
	}

	public static class Scraper {
		@Expose
		@SerializedName("region")
		private String region;

		@Expose
		@SerializedName("fields")
		private String[] fields;

		@Expose
		@SerializedName("education")
		private String education;

		@Expose
		@SerializedName("radius")
		private int radius;

		public String getRegion() {
			return region;
		}

		public String[] getFields() {
			return fields;
		}

		public String getEducation() {
			return education;
		}

		public int getRadius() {
			return radius;
		}
	}
}
