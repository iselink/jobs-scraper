package net.iselink.jobsscraper.scrapers;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.iselink.jobsscraper.dataclasses.BaseEntry;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all scrapers used through whole program.
 */
public abstract class Scraper<T extends BaseEntry> {

	@Expose
	@SerializedName("scraper")
	public final String type = getClass().getSimpleName();

	/**
	 * List of job offers from page.
	 */
	protected final List<T> items = new ArrayList<>();

	@Expose
	@SerializedName("timestamp")
	private String timestamp = null;

	/**
	 * Perform scraping.
	 */
	//TODO: make return future or some kind of processing parallelization
	public final void scrape() {
		timestamp = Instant.now().toString();
		onScrape();
	}

	protected abstract void onScrape();

	public void loadFromFile(String filename) throws IOException {
		try (FileReader reader = new FileReader(filename)) {
			new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, getClass());
		}
	}

	public void saveToFile(String filename) throws IOException {
		try (FileWriter writer = new FileWriter(filename)) {
			new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this, writer);
		}
	}


	/**
	 * Compare current scraped list with different list.
	 *
	 * @param list List to compare
	 * @return Returns comparation of both lists.
	 */
	public Comparation<T> compareWith(List<T> list) {
		//this is the place, where time complexity dies
		//added list - items in current list, which are not in second list
		//removed list - items missing in current list, which are in second list
		//unchanged list - items which are in both lists
		List<T> addedList = new ArrayList<>();
		List<T> removedList = new ArrayList<>();
		List<T> unchagedList = new ArrayList<>();

		//added
		for (T currentItem : items) {
			boolean found = false;
			for (T secondItem : list) {
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
		for (T secondItem : list) {
			boolean found = false;
			for (T currentItem : items) {
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
		for (T currentItem : items) {
			for (T secondItem : list) {
				if (currentItem.getLink().equalsIgnoreCase(secondItem.getLink())) {
					unchagedList.add(currentItem);
				}
			}
		}

		return new Comparation<T>(unchagedList, addedList, removedList);
	}

	public List<T> getItems() {
		return items;
	}

	public String getTimestamp() {
		return timestamp;
	}
}
