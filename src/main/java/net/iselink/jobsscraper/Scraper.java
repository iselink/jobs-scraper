package net.iselink.jobsscraper;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.dataclasses.Entry;
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

public class Scraper {

	@Expose
	@SerializedName("timestamp")
	private String timestamp = Instant.now().toString();

	@Expose
	@SerializedName("address")
	private String adress;

	@Expose
	@SerializedName("items")
	private List<Entry> items = new ArrayList<>();

	public Scraper(String region, String[] fields, String education, int radius) {
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
			Entry e = Entry.fromElement(element);
			if (e != null)
				this.items.add(e);
		});

		Elements next = doc.getElementsByClass("button button--slim pager__next");
		return next.isEmpty();
	}

	public List<Entry> getEntries() {
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
			Scraper scraper = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, Scraper.class);
			return compareWith(scraper.items);
		}
	}


	public Comparation compareWith(List<Entry> entries) {
		Comparation comp = new Comparation();

		//find all unchanged items
		//i.e. - only items in both lists
		comp.unchangedEntries = items.stream().filter(entry -> {
			for (Entry entry1 : entries) {
				if (entry.getLink().equalsIgnoreCase(entry1.getLink())) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());

		//find new offers
		//i.e. - only items from new list which aren't in old
		comp.addedEntries = items.stream().filter(entry -> {
			for (Entry e : entries) {
				if (entry.getLink().equalsIgnoreCase(e.getLink())) {
					return false;
				}
			}
			return true;
		}).collect(Collectors.toList());

		//find old (removed) offers
		//i.e. - only items from old list which aren't in new
		comp.removedEntries = entries.stream().filter(entry -> {
			for (Entry e : items) {
				if (entry.getLink().equalsIgnoreCase(e.getLink())) {
					return false;
				}
			}
			return true;
		}).collect(Collectors.toList());

		return comp;
	}

	public static class Comparation {

		private List<Entry> unchangedEntries = new ArrayList<>();
		private List<Entry> removedEntries = new ArrayList<>();
		private List<Entry> addedEntries = new ArrayList<>();


		private Comparation() {
		}

		public Comparation(List<Entry> unchangedEntries, List<Entry> removedEntries, List<Entry> addedEntries) {
			this.unchangedEntries = unchangedEntries;
			this.removedEntries = removedEntries;
			this.addedEntries = addedEntries;
		}


		public List<Entry> getUnchangedEntries() {
			return unchangedEntries;
		}

		public Comparation setUnchangedEntries(List<Entry> unchangedEntries) {
			this.unchangedEntries = unchangedEntries;
			return this;
		}

		public List<Entry> getRemovedEntries() {
			return removedEntries;
		}

		public Comparation setRemovedEntries(List<Entry> removedEntries) {
			this.removedEntries = removedEntries;
			return this;
		}

		public List<Entry> getAddedEntries() {
			return addedEntries;
		}

		public Comparation setAddedEntries(List<Entry> addedEntries) {
			this.addedEntries = addedEntries;
			return this;
		}
	}
}
