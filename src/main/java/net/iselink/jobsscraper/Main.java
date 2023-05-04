package net.iselink.jobsscraper;

import net.iselink.jobsscraper.scrapers.JobsCzScraper;
import net.iselink.jobsscraper.script.Script;
import net.iselink.jobsscraper.utils.ArgumentParser;
import net.iselink.jobsscraper.utils.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

	public static void main(String[] args) {
		ArgumentParser ap = new ArgumentParser("./program");
		ap.getRootCommand().setSubcommandRequired(true);

		ArgumentParser.Flag confFlag = ap.getRootCommand().addFlag(new ArgumentParser.Flag("config", "Configuration file to load", "config.json"));
		ArgumentParser.Command scrapeCommand = ap.getRootCommand().addSubcommand(new ArgumentParser.Command("scrape", "Scrape website for info"));
		ArgumentParser.Command scriptCommand = ap.getRootCommand().addSubcommand(new ArgumentParser.Command("script", "Execute JavaScript."));

		ArgumentParser.Flag scriptFlag = scriptCommand.addFlag(new ArgumentParser.Flag("script", "JS script to execute.", "script.js"));
		try {
			ap.parse(args);
		} catch (ArgumentParser.UndefinedFlagException | ArgumentParser.UndefinedCommandException |
				 ArgumentParser.SubcommandNotSelectedException e) {
			ap.printError(e);
			System.exit(1);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		if (ap.getRootCommand().getSelectedCommand() == scrapeCommand) {
			try {
				scrapeAction(ap, confFlag);
			} catch (IOException | URISyntaxException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		} else if (ap.getRootCommand().getSelectedCommand() == scriptCommand) {
			try {
				executeScript(scriptFlag.getValue());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void executeScript(String file) throws IOException {
		Script script = Script.loadFromFile(file);
		script.execute();
	}

	private static void scrapeAction(ArgumentParser ap, ArgumentParser.Flag confFlag) throws IOException, URISyntaxException, InterruptedException {
		Configuration configuration;
		try {
			configuration = Configuration.loadConfig(confFlag.getValue());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		JobsCzScraper jobsCzScraper = new JobsCzScraper(configuration.getScraper().getRegion(),
				configuration.getScraper().getFields(),
				configuration.getScraper().getEducation(),
				configuration.getScraper().getRadius()
		);
		jobsCzScraper.scrape();
		jobsCzScraper.saveToFile("jobs_cz.json");

		/**DiscordWebHook webhook = new DiscordWebHook(new DiscordWebHook.Embed("Job search report", "", new DiscordWebHook.Field[]{
		 new DiscordWebHook.Field("Added jobs", Integer.toString(c.getAddedEntries().size()), true),
		 new DiscordWebHook.Field("Removed jobs", Integer.toString(c.getRemovedEntries().size()), true),
		 new DiscordWebHook.Field("Unchanged jobs", Integer.toString(c.getUnchangedEntries().size()), true),
		 new DiscordWebHook.Field("Total count now", Integer.toString(jobsCzScraper.getEntriesCount()), true)
		 }));
		 webhook.send(configuration.getWebhookAddress());

		 CodeSplitter cs = new CodeSplitter(1000, "diff");
		 c.getAddedEntries().forEach(entry -> {
		 cs.addLine("+ ", entry.getTitle(), "\t", entry.getLink());
		 });
		 c.getRemovedEntries().forEach(entry -> {
		 cs.addLine("- ", entry.getTitle());
		 });
		 cs.getList().forEach(s -> {
		 try {
		 new DiscordWebHook(s).send(configuration.getWebhookAddress());
		 } catch (IOException | InterruptedException | URISyntaxException e) {
		 e.printStackTrace();
		 }
		 });**/

	}

}