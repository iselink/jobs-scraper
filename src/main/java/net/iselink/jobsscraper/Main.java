package net.iselink.jobsscraper;

import net.iselink.jobsscraper.utils.ArgumentParser;
import net.iselink.jobsscraper.utils.Configuration;
import net.iselink.jobsscraper.utils.DiscordWebHook;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
		ArgumentParser ap = new ArgumentParser();

		ap.parse(args);

		Configuration configuration;
		try {
			configuration = Configuration.loadConfig(ap.getValue("config", "config.json"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Scraper scraper = new Scraper(configuration.getScraper().getRegion(),
				configuration.getScraper().getFields(),
				configuration.getScraper().getEducation(),
				configuration.getScraper().getRadius()
		);

		scraper.scrape();

		Scraper.Comparation c = scraper.compareWith("save.json");
		scraper.save("save.json");

		if (c.getRemovedEntries().size() == 0 && c.getAddedEntries().size() == 0) {
			//as there isn't any changes, don't push pointless message
			return;
		}

		DiscordWebHook webhook = new DiscordWebHook(new DiscordWebHook.Embed("Job search report", "", new DiscordWebHook.Field[]{
				new DiscordWebHook.Field("Added jobs", Integer.toString(c.getAddedEntries().size()), true),
				new DiscordWebHook.Field("Removed jobs", Integer.toString(c.getRemovedEntries().size()), true),
				new DiscordWebHook.Field("Unchanged jobs", Integer.toString(c.getUnchangedEntries().size()), true),
				new DiscordWebHook.Field("Total count now", Integer.toString(scraper.getEntriesCount()), true)
		}));
		webhook.send(configuration.getWebhookAddress());

		StringBuilder msg = new StringBuilder();
		msg.append("```diff");
		c.getAddedEntries().forEach(entry -> {
			msg.append("\n+ ").append(entry.getTitle()).append("\t").append(entry.getLink());
		});
		c.getRemovedEntries().forEach(entry -> {
			msg.append("\n- ").append(entry.getTitle());
		});
		msg.append("```");

		new DiscordWebHook(msg.toString()).send(configuration.getWebhookAddress());
	}
}