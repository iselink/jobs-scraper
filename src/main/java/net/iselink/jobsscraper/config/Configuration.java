package net.iselink.jobsscraper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class Configuration {

	@Expose
	@SerializedName("scrapers")
	private Scrapers scrapers = null;

	@Expose
	@SerializedName("webhook")
	private String webhookAddress = null;

	public static Configuration loadConfig(String filename) throws IOException {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		try (FileReader reader = new FileReader(filename, StandardCharsets.UTF_8)) {
			return gson.fromJson(reader, Configuration.class);
		}
	}


	public String getWebhookAddress() {
		return webhookAddress;
	}

	public Scrapers getScrapers() {
		return scrapers;
	}

	public static class Scrapers {
		@Expose
		@SerializedName("scraper_job_cz")
		private ScraperJobCz scraperJobCz;

		@Expose
		@SerializedName("scraper_prace_cz_url")
		private String praceCzUrl;

		public ScraperJobCz getScraperJobCz() {
			return scraperJobCz;
		}

		public String getPraceCzUrl() {
			return praceCzUrl;
		}
	}

	/**
	 * Dedicated class for holding job.cz configuration.
	 */
	public static class ScraperJobCz {
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

	/**
	 * Base class for holding prace.cz configuration.
	 */
	//TODO: rework config for this scraper (currently using direct url)

	//	public static class ScraperPraceCz {
	//
	//		private List<String> localityCodes;
	//		private List<String> professions;
	//		private List<String> searchTerms;
	//		private List<String> employmentTypes;
	//		private ;
	//	}
}
