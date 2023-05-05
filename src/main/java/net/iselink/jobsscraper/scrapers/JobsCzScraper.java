package net.iselink.jobsscraper.scrapers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.dataclasses.JobsCzEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Scraper responsible for jobs.cz server.
 */
public class JobsCzScraper extends Scraper<JobsCzEntry> {

	@Expose
	@SerializedName("address")
	private String address;


	public JobsCzScraper(String region, String[] fields, String education, int radius) {
		StringBuilder sb = new StringBuilder();

		for (String field : fields) {
			sb.append(sb.isEmpty() ? '?' : '&');
			sb.append("field[]=").append(field);
		}

		address = String.format(
				"https://www.jobs.cz/prace/%s/%s&education=%s&locality[radius]=%d",
				region, sb, education, radius
		);
	}

	@Override
	protected void onScrape() {
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
			fullAdr = String.format("%s&page=%d", address, page);
		} else {
			fullAdr = address;
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

}
