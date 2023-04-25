package net.iselink.jobsscraper;

import net.iselink.jobsscraper.utils.ArgumentParser;
import net.iselink.jobsscraper.utils.Configuration;
import net.iselink.jobsscraper.utils.DiscordWebHook;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

	public static void main(String[] args) {
		ArgumentParser ap = new ArgumentParser("./program");

		ArgumentParser.Flag confFlag = new ArgumentParser.Flag("config", "Configuration file to load", "config.json");
		ap.getRootCommand().addFlag(confFlag);


		try {
			ap.parse(args);
		} catch (ArgumentParser.UndefinedFlagException | ArgumentParser.UndefinedCommandException |
				 ArgumentParser.SubcommandNotSelectedException e) {
			//TODO: print help
			System.exit(1);
		}

		try {
			scrapeAction(ap, confFlag);
		} catch (IOException | URISyntaxException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static void scrapeAction(ArgumentParser ap, ArgumentParser.Flag confFlag) throws IOException, URISyntaxException, InterruptedException {
		Configuration configuration;
		try {
			configuration = Configuration.loadConfig(confFlag.getValue());
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