package net.iselink.jobsscraper.scrapers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.dataclasses.JobsCzEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


	@Override
	public Comparation<JobsCzEntry> compareWith(List<JobsCzEntry> secondList) {
		//this is the place, where time complexity dies
		//added list - items in current list, which are not in second list
		//removed list - items missing in current list, which are in second list
		//unchanged list - items which are in both lists
		List<JobsCzEntry> addedList = new ArrayList<>();
		List<JobsCzEntry> removedList = new ArrayList<>();
		List<JobsCzEntry> unchagedList = new ArrayList<>();

		//added
		for (JobsCzEntry currentItem : items) {
			boolean found = false;
			for (JobsCzEntry secondItem : secondList) {
				if (currentItem.getLink().equalsIgnoreCase(secondItem.getLink())) {
					found = true;
					break;
				}
			}

			if (!found) {
				addedList.add(currentItem);
			}
		}

		//removed
		for (JobsCzEntry secondItem : secondList) {
			boolean found = false;
			for (JobsCzEntry currentItem : items) {
				if (currentItem.getLink().equalsIgnoreCase(secondItem.getLink())) {
					found = true;
					break;
				}
			}

			if (!found) {
				removedList.add(secondItem);
			}
		}

		//unchanged
		for (JobsCzEntry currentItem : items) {
			for (JobsCzEntry secondItem : secondList) {
				if (currentItem.getLink().equalsIgnoreCase(secondItem.getLink())) {
					unchagedList.add(currentItem);
				}
			}
		}

		return new Comparation<>(unchagedList, addedList, removedList);
	}

}
