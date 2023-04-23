package net.iselink.jobsscraper;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.dataclasses.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
				region, sb.toString(), education, radius
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


}
