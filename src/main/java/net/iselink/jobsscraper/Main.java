package net.iselink.jobsscraper;

import net.iselink.jobsscraper.config.Configuration;
import net.iselink.jobsscraper.scrapers.JobsCzScraper;
import net.iselink.jobsscraper.scrapers.PraceCzScraper;
import net.iselink.jobsscraper.script.Script;
import net.iselink.jobsscraper.utils.ArgumentParser;

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

		JobsCzScraper jobsCzScraper = new JobsCzScraper(configuration.getScrapers().getScraperJobCz().getRegion(),
				configuration.getScrapers().getScraperJobCz().getFields(),
				configuration.getScrapers().getScraperJobCz().getEducation(),
				configuration.getScrapers().getScraperJobCz().getRadius()
		);
		jobsCzScraper.scrape();
		jobsCzScraper.saveToFile("jobs_cz.json");

		PraceCzScraper scraper = new PraceCzScraper(configuration.getScrapers().getPraceCzUrl());
		scraper.scrape();
		scraper.saveToFile("prace_cz.json");


	}

}