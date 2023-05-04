package net.iselink.jobsscraper;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.dataclasses.JobsCzEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobsCzScraper {

	@Expose
	@SerializedName("timestamp")
	private String timestamp = Instant.now().toString();

	@Expose
	@SerializedName("address")
	private String adress;

	@Expose
	@SerializedName("items")
	private List<JobsCzEntry> items = new ArrayList<>();

	public JobsCzScraper(String region, String[] fields, String education, int radius) {
		StringBuilder sb = new StringBuilder();

		for (String field : fields) {
			sb.append(sb.isEmpty() ? '?' : '&');
			sb.append("field[]=").append(field);
		}

		adress = String.format(
				"https://www.jobs.cz/prace/%s/%s&education=%s&locality[radius]=%d",
				region, sb, education, radius
		);
	}

	public void scrape() {
		for (int page = 1; ; page++) {
			boolean nextPageUnavailable = false;

			try {
				nextPageUnavailable = scrapePage(page);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			if (nextPageUnavailable) {
				break;
			}
		}

	}

	private boolean scrapePage(int page) throws IOException {
		String fullAdr;
		if (page > 1) {
			fullAdr = String.format("%s&page=%d", adress, page);
		} else {
			fullAdr = adress;
		}

		Document doc = Jsoup
				.connect(fullAdr)
				.get();
		Elements items = doc.getElementsByClass("standalone search-list__item");

		items.forEach(element -> {
			JobsCzEntry e = JobsCzEntry.fromElement(element);
			if (e != null)
				this.items.add(e);
		});

		Elements next = doc.getElementsByClass("button button--slim pager__next");
		return next.isEmpty();
	}

	public List<JobsCzEntry> getEntries() {
		return items;
	}

	public int getEntriesCount() {
		return items.size();
	}

	public void save(String filename) throws IOException {
		try (FileWriter writer = new FileWriter(filename)) {
			new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this, writer);
		}
	}

	public String getTimestamp() {
		return timestamp;
	}

	public Instant getTimestampAsInstant() {
		return Instant.parse(timestamp);
	}

	public Comparation compareWith(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			return new Comparation(new ArrayList<>(), new ArrayList<>(), items);
		}

		try (FileReader reader = new FileReader(filename)) {
			JobsCzScraper jobsCzScraper = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, JobsCzScraper.class);
			return compareWith(jobsCzScraper.items);
		}
	}


	public Comparation compareWith(List<JobsCzEntry> entries) {
		Comparation comp = new Comparation();

		//find all unchanged items
		//i.e. - only items in both lists
		comp.unchangedEntries = items.stream().filter(jobsCzEntry -> {
			for (JobsCzEntry jobsCzEntry1 : entries) {
				if (jobsCzEntry.getLink().equalsIgnoreCase(jobsCzEntry1.getLink())) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());

		//find new offers
		//i.e. - only items from new list which aren't in old
		comp.addedEntries = items.stream().filter(jobsCzEntry -> {
			for (JobsCzEntry e : entries) {
				if (jobsCzEntry.getLink().equalsIgnoreCase(e.getLink())) {
					return false;
				}
			}
			return true;
		}).collect(Collectors.toList());

		//find old (removed) offers
		//i.e. - only items from old list which aren't in new
		comp.removedEntries = entries.stream().filter(jobsCzEntry -> {
			for (JobsCzEntry e : items) {
				if (jobsCzEntry.getLink().equalsIgnoreCase(e.getLink())) {
					return false;
				}
			}
			return true;
		}).collect(Collectors.toList());

		return comp;
	}

	public static class Comparation {

		private List<JobsCzEntry> unchangedEntries = new ArrayList<>();
		private List<JobsCzEntry> removedEntries = new ArrayList<>();
		private List<JobsCzEntry> addedEntries = new ArrayList<>();


		private Comparation() {
		}

		public Comparation(List<JobsCzEntry> unchangedEntries, List<JobsCzEntry> removedEntries, List<JobsCzEntry> addedEntries) {
			this.unchangedEntries = unchangedEntries;
			this.removedEntries = removedEntries;
			this.addedEntries = addedEntries;
		}


		public List<JobsCzEntry> getUnchangedEntries() {
			return unchangedEntries;
		}

		public Comparation setUnchangedEntries(List<JobsCzEntry> unchangedEntries) {
			this.unchangedEntries = unchangedEntries;
			return this;
		}

		public List<JobsCzEntry> getRemovedEntries() {
			return removedEntries;
		}

		public Comparation setRemovedEntries(List<JobsCzEntry> removedEntries) {
			this.removedEntries = removedEntries;
			return this;
		}

		public List<JobsCzEntry> getAddedEntries() {
			return addedEntries;
		}

		public Comparation setAddedEntries(List<JobsCzEntry> addedEntries) {
			this.addedEntries = addedEntries;
			return this;
		}
	}
}
