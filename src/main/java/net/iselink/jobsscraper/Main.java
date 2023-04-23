package net.iselink.jobsscraper;

import net.iselink.jobsscraper.utils.Configuration;
import net.iselink.jobsscraper.utils.DiscordWebHook;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
		Configuration configuration;
		try {
			configuration = Configuration.loadConfig("config.json");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Scraper scraper = new Scraper(configuration.getScraper().getRegion(),
				configuration.getScraper().getFields(),
				configuration.getScraper().getEducation(),
				configuration.getScraper().getRadius()
		);

		scraper.scrape();
		scraper.save("xd.json");

		DiscordWebHook webhook = new DiscordWebHook(new DiscordWebHook.Embed("Job search report", String.format("Current count of job items: %d", scraper.getEntriesCount()), new DiscordWebHook.Field[]{
				new DiscordWebHook.Field("Job count", String.format("%d", scraper.getEntriesCount()))
		}));

		webhook.send(configuration.getWebhookAddress());
	}
}